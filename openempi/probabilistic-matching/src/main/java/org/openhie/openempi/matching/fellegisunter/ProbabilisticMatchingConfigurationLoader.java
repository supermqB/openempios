/**
 *
 * Copyright (C) 2002-2012 "SYSNET International, Inc."
 * support@sysnetint.com [http://www.sysnetint.com]
 *
 * This file is part of OpenEMPI.
 *
 * OpenEMPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openhie.openempi.matching.fellegisunter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhie.openempi.Constants;
import org.openhie.openempi.InitializationException;
import org.openhie.openempi.configuration.ComparatorFunction;
import org.openhie.openempi.configuration.ConfigurationLoader;
import org.openhie.openempi.configuration.ConfigurationRegistry;
import org.openhie.openempi.configuration.MatchField;
import org.openhie.openempi.configuration.Component.ComponentType;
import org.openhie.openempi.configuration.xml.MatchingConfigurationType;
import org.openhie.openempi.configuration.xml.MpiConfigDocument.MpiConfig;
import org.openhie.openempi.configuration.xml.probabilisticmatching.Classification;
import org.openhie.openempi.configuration.xml.probabilisticmatching.LogByVectors;
import org.openhie.openempi.configuration.xml.probabilisticmatching.LogByWeight;
import org.openhie.openempi.configuration.xml.probabilisticmatching.LogDestination;
import org.openhie.openempi.configuration.xml.probabilisticmatching.LoggingConfiguration;
import org.openhie.openempi.configuration.xml.probabilisticmatching.ProbabilisticMatchingType;
import org.openhie.openempi.configuration.xml.probabilisticmatching.VectorClassification;
import org.openhie.openempi.configuration.xml.probabilisticmatching.VectorClassifications;
import org.openhie.openempi.context.Context;
import org.openhie.openempi.model.VectorConfiguration;
import org.openhie.openempi.notification.ObservationEventType;

public class ProbabilisticMatchingConfigurationLoader implements ConfigurationLoader
{
	private final Log log = LogFactory.getLog(ProbabilisticMatchingConfigurationLoader.class);
	static final double DEFAULT_M_INITIAL_VALUE = 0.9;
	static final double DEFAULT_U_INITIAL_VALUE = 0.1;
	static final double DEFAULT_P_INITIAL_VALUE = 0.1;
	static final int DEFAULT_MAX_EM_ITERATIONS = 100;
	static final double DEFAULT_CONVERGENCE_ERROR = 0.00001;
	
	private String entityName;
	
	public void loadAndRegisterComponentConfiguration(ConfigurationRegistry registry, Object configurationFragment) throws InitializationException {

		// This loader only knows how to process configuration information specifically
		// for the probabilistic matching service
		//
		if (!(configurationFragment instanceof ProbabilisticMatchingType)) {
			log.error("Custom configuration loader " + getClass().getName() + 
			        " is unable to process the configuration fragment " + configurationFragment);
			throw new InitializationException("Custom configuration loader is unable to load this configuration fragment.");
		}
		
        ProbabilisticMatchingType matchConfigXml = (ProbabilisticMatchingType) configurationFragment;
        entityName = matchConfigXml.getEntityName();
        Context.getConfiguration().registerConfigurationLoader(ComponentType.MATCHING, entityName, this);

		// Register the configuration information with the Configuration registry so that
		// it is available for the matching service to use when needed.
		//
		ArrayList<MatchField> matchFields = new ArrayList<MatchField>();
		Map<String,Object> configurationData = new java.util.HashMap<String,Object>();
		
		log.debug("Received xml fragment to parse: " + matchConfigXml);
		configurationData.put(ProbabilisticMatchingConstants.FALSE_NEGATIVE_PROBABILITY_REGISTRY_KEY,
				matchConfigXml.getFalseNegativeProbability());
		configurationData.put(ProbabilisticMatchingConstants.FALSE_POSITIVE_PROBABILITY_REGISTRY_KEY, 
				matchConfigXml.getFalsePositiveProbability());
		for (int i=0; i < matchConfigXml.getMatchFields().sizeOfMatchFieldArray(); i++) {
			org.openhie.openempi.configuration.xml.probabilisticmatching.MatchField field = 
			        matchConfigXml.getMatchFields().getMatchFieldArray(i);
			MatchField matchField = buildMatchFieldFromXml(field);
			matchFields.add(matchField);
		}
        configurationData.put(Constants.MATCHING_FIELDS_REGISTRY_KEY, matchFields);
		configurationData.put(ConfigurationRegistry.ENTITY_NAME, matchConfigXml.getEntityName());
		configurationData.put(ProbabilisticMatchingConstants.CONFIGURATION_DIRECTORY_REGISTRY_KEY,
				matchConfigXml.getConfigFileDirectory());
		if (matchConfigXml.getLoggingConfiguration() != null) {
			loadLoggingConfiguration(matchConfigXml.getLoggingConfiguration(), configurationData);
		}
		loadAdvancedParameters(matchConfigXml.getConfigFileDirectory(), matchConfigXml.getEntityName(),
		        configurationData, matchFields.size());
		
		VectorConfigurationHelper.loadVectorConfiguration(configurationData, matchFields.size());		
		if (matchConfigXml.getVectorClassifications() != null) {
			loadVectorClassifications(matchConfigXml.getVectorClassifications(), configurationData);
		}
        registry.registerConfigurationEntry(matchConfigXml.getEntityName(), ConfigurationRegistry.MATCH_CONFIGURATION,
                configurationData);
		registry.registerConfigurationEntry(matchConfigXml.getEntityName(),
		        ConfigurationRegistry.MATCHING_ALGORITHM_NAME_KEY, 
				ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_ALGORITHM_NAME);
	}

    public void saveAndRegisterComponentConfiguration(ConfigurationRegistry registry, Map<String,Object> configurationData)
            throws InitializationException {
        @SuppressWarnings("unchecked")
        List<MatchField> matchFields = (List<MatchField>) configurationData.get(Constants.MATCHING_FIELDS_REGISTRY_KEY);
        Float falseNegativeProbability = (Float) configurationData.get(ProbabilisticMatchingConstants.FALSE_NEGATIVE_PROBABILITY_REGISTRY_KEY);
        Float falsePositiveProbability = (Float) configurationData.get(ProbabilisticMatchingConstants.FALSE_POSITIVE_PROBABILITY_REGISTRY_KEY);
        String configurationDirectory = (String) configurationData.get(ProbabilisticMatchingConstants.CONFIGURATION_DIRECTORY_REGISTRY_KEY);
        String entityName = (String) configurationData.get(ConfigurationRegistry.ENTITY_NAME);
        if (entityName == null) {
            log.error("The entity name is not registered in the configuration data.");
            throw new RuntimeException("The matching algorithm has not been properly configured.");
        }
        checkForFieldCountChange(registry, entityName, configurationData);
        ProbabilisticMatchingType xmlConfigurationFragment = buildMatchingConfigurationFragment(matchFields, falseNegativeProbability,
                falsePositiveProbability, configurationDirectory, configurationData);
        log.debug("Saving matching info xml configuration fragment: " + xmlConfigurationFragment);
        updateConfigurationInFile(entityName, xmlConfigurationFragment);
        Context.getConfiguration().saveConfiguration();
        saveAdvancedParameters(configurationDirectory, entityName, configurationData, matchFields);
        log.debug("Storing updated matching configuration in configuration registry: " + configurationData);
        registry.registerConfigurationEntry(entityName, ConfigurationRegistry.MATCH_CONFIGURATION, configurationData);

        // Generate a notification event to inform interested listeners via the lightweight mechanism that this event has occurred.
        Context.notifyObserver(ObservationEventType.MATCHING_CONFIGURATION_UPDATE_EVENT, configurationData);
    }

    private void updateConfigurationInFile(String thisEntityName, ProbabilisticMatchingType fragment) {
        log.debug("Saving matching xml configuration fragment: " + fragment);
        MpiConfig config = Context.getConfiguration().getMpiConfig();
        int count = config.getMatchingConfigurationArray().length;
        int index = -1;
        for (int i = 0; i < count; i++) {
            MatchingConfigurationType type = config.getMatchingConfigurationArray(i);
            if (type instanceof ProbabilisticMatchingType) {
                ProbabilisticMatchingType matchingType = (ProbabilisticMatchingType) type;
                String entityName = matchingType.getEntityName();
                if (entityName.equals(thisEntityName)) {
                    index = i;
                    break;
                }
            }
        }
        if (index >= 0) {
            config.setMatchingConfigurationArray(index, fragment);
        } else {
            log.error("Unable to save the matching configuration since no such section currently "
                    + "exists in the configuration file:\n" + fragment);
        }
    }
    
    public String getComponentEntity() {
        return entityName;
    }

	private void loadLoggingConfiguration(LoggingConfiguration loggingConfiguration, Map<String, Object> configurationData) {
		// Check and validate for logByVectors
		if (loggingConfiguration.getLogByVectors() != null) {
			loadLogByVectorsConfiguration(loggingConfiguration.getLogByVectors(), configurationData);
		}
		if (loggingConfiguration.getLogByWeight() != null) {
			loadLogByWeightConfiguration(loggingConfiguration.getLogByWeight(), configurationData);
		}
		if (loggingConfiguration.getLogDestination() != null &&
				loggingConfiguration.getLogDestination().intValue() == LogDestination.INT_LOG_TO_DB) {
			configurationData.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_DESTINATION,
					LogDestination.LOG_TO_DB.toString());
		} else {
			configurationData.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_DESTINATION,
					LogDestination.LOG_TO_FILE.toString());
		}
	}

	private void loadVectorClassifications(VectorClassifications vectorClassXml, Map<String, Object> data) {
		Map<Integer,Integer> vectorClassifications = new HashMap<Integer,Integer>();
		for (VectorClassification vectorXml : vectorClassXml.getVectorClassificationArray()) {
			Integer vectorValue = vectorXml.getVector().intValue();
			Integer classification = 0;
			if (vectorXml.getClassification().intValue() == Classification.INT_MATCH) {
				classification = Constants.MATCH_CLASSIFICATION;
			} else if (vectorXml.getClassification().intValue() == Classification.INT_NON_MATCH) {
				classification = Constants.NON_MATCH_CLASSIFICATION;
			} else if (vectorXml.getClassification().intValue() == Classification.INT_PROBABLE_MATCH) {
				classification = Constants.PROBABLE_MATCH_CLASSIFICATION;
			}
			if (classification == 0) {
				log.error("Encountered an invalid vector classification; entry will be ignored.");
				continue;
			}
			log.info("Stored explicit classification: <" + vectorValue + "," + 
					vectorXml.getClassification().toString());
			vectorClassifications.put(vectorValue, classification);
		}
		if (!vectorClassifications.isEmpty()) {
			data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_VECTOR_CLASSIFICATIONS, vectorClassifications);
		}
	}

	private void loadLogByWeightConfiguration(LogByWeight logByWeight, Map<String, Object> data) {
		// Check the sample size; if it is invalid, there is no need to do anything more.
		double sampleSize = logByWeight.getSampleSizePercentage();
		if (sampleSize <= 0 || sampleSize > 1.0) {
			log.warn("The sample size percentage for logging by weight is invalid: " + sampleSize + "; logging will be disabled.");
			return;
		}
		double weightLowerBound = logByWeight.getWeightLowerBound();
		double weightUpperBound = logByWeight.getWeightUpperBound();
		if (weightUpperBound < weightLowerBound) {
			log.warn("The values for weight lower and upper bound are not valid; logging will be disabled.");
		}
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_WEIGHT_LOWER_BOUND_KEY, weightLowerBound);
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_WEIGHT_UPPER_BOUND_KEY, weightUpperBound);
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_WEIGHT_FRACTION_KEY, sampleSize);
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_WEIGHT_KEY, Boolean.TRUE);
	}

	private void loadLogByVectorsConfiguration(LogByVectors logByVectors, Map<String, Object> data) {
		// Check the sample size; if it is invalid, there is no need to do anything more.
		double sampleSize = logByVectors.getSampleSizePercentage();
		if (sampleSize <= 0 || sampleSize > 1.0) {
			log.warn("The sample size percentage for logging by vectors is invalid: " + sampleSize + "; logging will be disabled.");
			return;
		}
		int[] vectors = logByVectors.getVectorArray();
		Set<Integer> vectorValues = new HashSet<Integer>();
		for (Integer value : vectors) {
			vectorValues.add(value);
		}
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_VECTORS_VECTORS_KEY, vectorValues);
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_VECTORS_FRACTION_KEY, sampleSize);
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_VECTORS_KEY, Boolean.TRUE);
	}

	@SuppressWarnings("unchecked")
	private void checkForFieldCountChange(ConfigurationRegistry registry, String entityName, Map<String, Object> data) {
	    Map<String,Object> currConfig = (Map<String,Object>) registry
		    .lookupConfigurationEntry(entityName, ConfigurationRegistry.MATCH_CONFIGURATION);
	    if (currConfig == null || currConfig.size() == 0) {
	        return;
	    }
	    List<MatchField> matchFields = (List<MatchField>) data.get(Constants.MATCHING_FIELDS_REGISTRY_KEY);
	    List<MatchField> currentMatchFields = (List<MatchField>) currConfig.get(Constants.MATCHING_FIELDS_REGISTRY_KEY);
	    if (matchFields == null || currentMatchFields == null) {
	        return;
	    }
	    if (matchFields.size() != currentMatchFields.size()) {
	        resetConfigurationFileSettings(entityName, data, matchFields);
	        VectorConfigurationHelper.loadVectorConfiguration(data, matchFields.size());
	    }
	}

    private void resetConfigurationFileSettings(String entityName, Map<String, Object> data,
            List<MatchField> matchFields) {
        FellegiSunterParameters params = new FellegiSunterParameters(matchFields.size());
        loadDefaultValues(params);
        String configurationDirectory = (String) data.get(ProbabilisticMatchingConstants.CONFIGURATION_DIRECTORY_REGISTRY_KEY);
        if (configurationDirectory == null || configurationDirectory.length() == 0) {
            configurationDirectory = ".";
        }
        FellegiSunterConfigurationManager.saveParameters(configurationDirectory, entityName, params);
    }

	private void saveAdvancedParameters(String configDirectory, String entityName, Map<String, Object> data, List<MatchField> matchFields) {
		if (configDirectory == null || configDirectory.length() == 0) {
			configDirectory = ".";
		}
		int count = matchFields.size();
		FellegiSunterParameters params = loadParametersFromFile(configDirectory, entityName, count);

		double[] mValues = (double[]) data.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_M_VALUES_KEY);
		validateValueCount(count, mValues.length, "The number of m-values is " + mValues.length + " instead of " + count);
		params.setMValues(mValues);
		double[] uValues = (double[]) data.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_U_VALUES_KEY);
		validateValueCount(count, uValues.length, "The number of u-values is " + mValues.length + " instead of " + count);
		params.setUValues(uValues);
		VectorConfigurationHelper.calculateAllVectorWeights(params);
		params.setPValue(((Double) data.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_P_VALUE_KEY)).doubleValue());
		params.setMInitialValue(((Double) data
				.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_INITIAL_M_VALUE_KEY)).doubleValue());
		params.setUInitialValue(((Double) data
				.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_INITIAL_U_VALUE_KEY)).doubleValue());
		params.setPInitialValue(((Double) data
				.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_INITIAL_P_VALUE_KEY)).doubleValue());
        params.setConvergenceError(((Double) data
                .get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_CONVERGENCE_ERROR_KEY)).doubleValue());
		params.setMaxIterations(((Integer) data
				.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_MAX_ITERATIONS_KEY)).intValue());
		params.setLowerBound(((Double) data
				.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOWER_BOUND_KEY)).doubleValue());
		params.setUpperBound(((Double) data
				.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_UPPER_BOUND_KEY)).doubleValue());
		params.setLambda(((Float) data.get(ProbabilisticMatchingConstants.FALSE_NEGATIVE_PROBABILITY_REGISTRY_KEY)).floatValue());
		params.setMu(((Float) data.get(ProbabilisticMatchingConstants.FALSE_POSITIVE_PROBABILITY_REGISTRY_KEY)).floatValue());
		
		// The number of matching fields has changed; they need to regenerate the model
		if (params.fieldCount != count || params.getMatchingFieldNames() == null || 
		        params.getMatchingFieldNames().length != count) {
		    String[] fieldNames = new String[matchFields.size()];
		    int i=0;
		    for (MatchField field : matchFields) {
		        fieldNames[i] = field.getFieldName();
		        i++;
		    }
		    params.setMatchingFieldNames(fieldNames);
		    int vectorCount = (int) Math.pow(2,count);
		    params.setFieldCount(count);
		    params.setVectorCount(vectorCount);
		    params.setVectorFrequencies(new int[vectorCount]);
		    params.setVectorWeights(new double[vectorCount]);
		}

		saveParametersToFile(configDirectory, entityName, params);
	}

	private void validateValueCount(int count, int valueCount, String message) {
		if (valueCount != count) {
			log.error("Unable to save invalid configuration. " + message);
			throw new RuntimeException("Unable to save invalid configuration. " + message);
		}
	}

	private FellegiSunterParameters loadParametersFromFile(String configDirectory, String entityName, int count) {
		FellegiSunterParameters params=null;
		try {
			params = FellegiSunterConfigurationManager.loadParameters(configDirectory, entityName);
		} catch (RuntimeException e) {
			params = new FellegiSunterParameters(count);
		}
		loadDefaultValues(params);
		return params;
	}
	
	private void saveParametersToFile(String configDirectory, String entityName, FellegiSunterParameters params) {
		try {
			FellegiSunterConfigurationManager.saveParameters(configDirectory, entityName, params);
		} catch (Exception e) {
			log.warn("Unable to save the model configuration of the probabilistic algorithm: " + e, e);
		}
	}

	private void loadAdvancedParameters(String configDirectory, String entityName, Map<String, Object> data, int count) {
		if (configDirectory == null || configDirectory.length() == 0) {
			configDirectory = ".";
		}
		FellegiSunterParameters params = loadParametersFromFile(configDirectory, entityName, count);
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_CONVERGENCE_ERROR_KEY, params.getConvergenceError());
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_M_VALUES_KEY, params.getMValues());
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_U_VALUES_KEY, params.getUValues());
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_P_VALUE_KEY, params.getPValue());
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_INITIAL_M_VALUE_KEY, params.getMInitialValue());
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_INITIAL_U_VALUE_KEY, params.getUInitialValue());
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_INITIAL_P_VALUE_KEY, params.getPInitialValue());
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_MAX_ITERATIONS_KEY, params.getMaxIterations());
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOWER_BOUND_KEY, params.getLowerBound());
		data.put(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_UPPER_BOUND_KEY, params.getUpperBound());
	}

	public static void loadDefaultValues(FellegiSunterParameters params) {
		if (params.getMaxIterations() == 0) {
			params.setMaxIterations(DEFAULT_MAX_EM_ITERATIONS);
		}
		if (params.getConvergenceError() == 0) {
		    params.setConvergenceError(DEFAULT_CONVERGENCE_ERROR);
		}
		if (params.getPInitialValue() == 0) {
			params.setPInitialValue(DEFAULT_P_INITIAL_VALUE);
		}
		if (params.getMInitialValue() == 0) {
			params.setMInitialValue(DEFAULT_M_INITIAL_VALUE);
		}
		if (params.getUInitialValue() == 0) {
			params.setUInitialValue(DEFAULT_U_INITIAL_VALUE);
		}
	}
	
	private org.openhie.openempi.configuration.xml.probabilisticmatching.ProbabilisticMatchingType buildMatchingConfigurationFragment(List<MatchField> fields,
			Float falseNegativeProb, Float falsePositiveProb, String configDirectory, Map<String, Object> data) {
		org.openhie.openempi.configuration.xml.probabilisticmatching.ProbabilisticMatchingType matchingConfigurationType =
			org.openhie.openempi.configuration.xml.probabilisticmatching.ProbabilisticMatchingType.Factory.newInstance();
		matchingConfigurationType.setEntityName((String) data.get(ConfigurationRegistry.ENTITY_NAME));
		matchingConfigurationType.setFalseNegativeProbability(falseNegativeProb);
		matchingConfigurationType.setFalsePositiveProbability(falsePositiveProb);
		org.openhie.openempi.configuration.xml.probabilisticmatching.MatchFields matchFieldsXml = matchingConfigurationType.addNewMatchFields();
		for (MatchField matchField : fields) {
			org.openhie.openempi.configuration.xml.probabilisticmatching.MatchField matchFieldXml =
			matchFieldsXml.addNewMatchField();
			matchFieldXml.setFieldName(matchField.getFieldName());
			matchFieldXml.setAgreementProbability(matchField.getAgreementProbability());
			matchFieldXml.setDisagreementProbability(matchField.getDisagreementProbability());
			matchFieldXml.setComparatorFunction(buildComparatorFunctionFragment(matchField.getComparatorFunction()));
			matchFieldXml.setMatchThreshold(matchField.getMatchThreshold());
		}
		matchingConfigurationType.setConfigFileDirectory(configDirectory);
		if ((data.containsKey(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_VECTORS_KEY) &&
				(Boolean)data.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_VECTORS_KEY))
				||
			(data.containsKey(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_WEIGHT_KEY) &&
				(Boolean) data.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_WEIGHT_KEY))) {
				buildLoggingConfiguration(matchingConfigurationType, data);
		}
		@SuppressWarnings("unchecked")
		List<VectorConfiguration> vectors = (List<VectorConfiguration>) data.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_VECTOR_CONFIGURATION);
		if (vectors != null && vectors.size() > 0) {
			VectorClassifications classificationsXml = null;
			for (VectorConfiguration vectorConf : vectors) {
				// We only need to store in the file the changes that have been made.
				if (vectorConf.getAlgorithmClassification() != vectorConf.getManualClassification()) {
					if (classificationsXml == null) {
						classificationsXml = matchingConfigurationType.addNewVectorClassifications();
					}
					VectorClassification classificationXml = classificationsXml.addNewVectorClassification();
					classificationXml.setVector(new BigInteger(new Integer(vectorConf.getVectorValue()).toString()));
					if (vectorConf.getManualClassification() == Constants.MATCH_CLASSIFICATION) {
						classificationXml.setClassification(Classification.MATCH);
					} else if (vectorConf.getManualClassification() == Constants.NON_MATCH_CLASSIFICATION) {
						classificationXml.setClassification(Classification.NON_MATCH);
					} else if (vectorConf.getManualClassification() == Constants.PROBABLE_MATCH_CLASSIFICATION) {
						classificationXml.setClassification(Classification.PROBABLE_MATCH);
					}
				}
			}
			// Update the registry with the manual classifications
			if (classificationsXml != null) {
				loadVectorClassifications(classificationsXml, data);
			}
		}
		return matchingConfigurationType;
	}

	private void buildLoggingConfiguration(ProbabilisticMatchingType matchingConfigurationType, Map<String, Object> data) {
		LoggingConfiguration logConfig = matchingConfigurationType.addNewLoggingConfiguration();
		if ( data.containsKey(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_VECTORS_KEY) &&
				(Boolean) data.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_VECTORS_KEY)) {
			buildLogByVectorConfiguration(logConfig, data);
		}
		if (data.containsKey(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_WEIGHT_KEY) &&
				(Boolean) data.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_WEIGHT_KEY)) {
			buildLogByWeightConfiguration(logConfig, data);
		}
		if (data.containsKey(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_DESTINATION)) {
			String logDestination = (String) data.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_DESTINATION);
			if (logDestination.equalsIgnoreCase(LogDestination.LOG_TO_DB.toString())) {
				logConfig.setLogDestination(LogDestination.LOG_TO_DB);
			} else {
				logConfig.setLogDestination(LogDestination.LOG_TO_FILE);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void buildLogByVectorConfiguration(LoggingConfiguration logConfig, Map<String, Object> data) {
		LogByVectors logByVectors = logConfig.addNewLogByVectors();
		Set<Integer> vectors = (Set<Integer>) data.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_VECTORS_VECTORS_KEY);
		for (Integer vector : vectors) {
			logByVectors.addVector(vector.intValue());
		}
		logByVectors.setSampleSizePercentage(((Double) data
				.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_VECTORS_FRACTION_KEY)).floatValue());
	}

	private void buildLogByWeightConfiguration(LoggingConfiguration logConfig, Map<String, Object> data) {
		LogByWeight logByWeight = logConfig.addNewLogByWeight();
		logByWeight.setWeightLowerBound(((Double) data
				.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_WEIGHT_LOWER_BOUND_KEY)));
		logByWeight.setWeightUpperBound(((Double) data
				.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_WEIGHT_UPPER_BOUND_KEY)).floatValue());
		logByWeight.setSampleSizePercentage(((Double) data
				.get(ProbabilisticMatchingConstants.PROBABILISTIC_MATCHING_LOGGING_BY_WEIGHT_FRACTION_KEY)).floatValue());
	}

	private MatchField buildMatchFieldFromXml(org.openhie.openempi.configuration.xml.probabilisticmatching.MatchField field) {
		MatchField matchField = new MatchField();
		matchField.setFieldName(field.getFieldName());
		matchField.setAgreementProbability(field.getAgreementProbability());
		matchField.setDisagreementProbability(field.getDisagreementProbability());
		matchField.setMatchThreshold(field.getMatchThreshold());
		matchField.setComparatorFunction(buildComparatorFunctionFromXml(field.getComparatorFunction()));
		return matchField;
	}

	private ComparatorFunction buildComparatorFunctionFromXml(org.openhie.openempi.configuration.xml.ComparatorFunction comparatorFunction) {
		ComparatorFunction function = new ComparatorFunction();
		function.setFunctionName(comparatorFunction.getFunctionName());
		if (comparatorFunction.isSetParameters() && comparatorFunction.getParameters().sizeOfParameterArray() > 0) {
			for (org.openhie.openempi.configuration.xml.Parameter parameter : comparatorFunction.getParameters().getParameterArray()) {
				log.debug("Adding parameter (" + parameter.getName() + "," + parameter.getValue() + ") to comparator function " + 
						function.getFunctionName());
				function.addParameter(parameter.getName(), parameter.getValue());
			}
		}
		return function;
	}

	private org.openhie.openempi.configuration.xml.ComparatorFunction buildComparatorFunctionFragment(ComparatorFunction comparatorFunction) {
		org.openhie.openempi.configuration.xml.ComparatorFunction function =
			org.openhie.openempi.configuration.xml.ComparatorFunction.Factory.newInstance();
		function.setFunctionName(comparatorFunction.getFunctionName());
		if (comparatorFunction.getParameterMap().size() == 0) {
			return function;
		}

		org.openhie.openempi.configuration.xml.Parameters parameters = 
			org.openhie.openempi.configuration.xml.Parameters.Factory.newInstance();
		for (String parameterName : comparatorFunction.getParameterMap().keySet()) {
			org.openhie.openempi.configuration.xml.Parameter parameter = parameters.addNewParameter();
			parameter.setName(parameterName);
			parameter.setValue(comparatorFunction.getParameterMap().get(parameterName));
		}
		return function;
	}
}
