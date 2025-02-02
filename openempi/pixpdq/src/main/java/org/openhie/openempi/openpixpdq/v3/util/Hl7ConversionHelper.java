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
package org.openhie.openempi.openpixpdq.v3.util;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.v3.ADExplicit;
import org.hl7.v3.AcknowledgementType;
import org.hl7.v3.ActClassControlAct;
import org.hl7.v3.AdxpExplicitCity;
import org.hl7.v3.AdxpExplicitCountry;
import org.hl7.v3.AdxpExplicitPostalCode;
import org.hl7.v3.AdxpExplicitState;
import org.hl7.v3.AdxpExplicitStreetAddressLine;
import org.hl7.v3.CE;
import org.hl7.v3.COCTMT090003UV01AssignedEntity;
import org.hl7.v3.COCTMT150002UV01Organization;
import org.hl7.v3.ENExplicit;
import org.hl7.v3.EnExplicitFamily;
import org.hl7.v3.EnExplicitGiven;
import org.hl7.v3.II;
import org.hl7.v3.INT;
import org.hl7.v3.IVLTSExplicit;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.MFMIMT700711UV01Custodian;
import org.hl7.v3.MFMIMT700711UV01QueryAck;
import org.hl7.v3.PNExplicit;
import org.hl7.v3.PRPAIN201305UV02;
import org.hl7.v3.PRPAIN201305UV02QUQIMT021001UV01ControlActProcess;
import org.hl7.v3.PRPAIN201306UV02;
import org.hl7.v3.PRPAIN201306UV02MFMIMT700711UV01ControlActProcess;
import org.hl7.v3.PRPAIN201306UV02MFMIMT700711UV01RegistrationEvent;
import org.hl7.v3.PRPAIN201306UV02MFMIMT700711UV01Subject1;
import org.hl7.v3.PRPAIN201306UV02MFMIMT700711UV01Subject2;
import org.hl7.v3.PRPAMT201306UV02LivingSubjectAdministrativeGender;
import org.hl7.v3.PRPAMT201306UV02LivingSubjectBirthTime;
import org.hl7.v3.PRPAMT201306UV02LivingSubjectId;
import org.hl7.v3.PRPAMT201306UV02LivingSubjectName;
import org.hl7.v3.PRPAMT201306UV02OtherIDsScopingOrganization;
import org.hl7.v3.PRPAMT201306UV02ParameterList;
import org.hl7.v3.PRPAMT201306UV02PatientAddress;
import org.hl7.v3.PRPAMT201306UV02PatientTelecom;
import org.hl7.v3.PRPAMT201306UV02QueryByParameter;
import org.hl7.v3.PRPAMT201310UV02OtherIDs;
import org.hl7.v3.PRPAMT201310UV02Patient;
import org.hl7.v3.PRPAMT201310UV02Person;
import org.hl7.v3.PRPAMT201310UV02QueryMatchObservation;
import org.hl7.v3.PRPAMT201310UV02Subject;
import org.hl7.v3.ParticipationTargetSubject;
import org.hl7.v3.QUQIIN000003UV01Type;
import org.hl7.v3.QUQIMT000001UV01ControlActProcess;
import org.hl7.v3.QUQIMT000001UV01QueryContinuation;
import org.hl7.v3.QueryResponse;
import org.hl7.v3.XActMoodIntentEvent;
import org.openhealthtools.openexchange.actorconfig.Configuration;
import org.openhealthtools.openexchange.actorconfig.IActorDescription;
import org.openhealthtools.openexchange.actorconfig.IheConfigurationException;
import org.openhealthtools.openexchange.datamodel.Address;
import org.openhealthtools.openexchange.datamodel.Identifier;
import org.openhealthtools.openexchange.datamodel.Patient;
import org.openhealthtools.openexchange.datamodel.PatientIdentifier;
import org.openhealthtools.openexchange.datamodel.PersonName;
import org.openhealthtools.openexchange.datamodel.PhoneNumber;
import org.openhealthtools.openexchange.datamodel.SharedEnums.SexType;
import org.openhealthtools.openpixpdq.api.PdqQuery;
import org.openhealthtools.openpixpdq.common.ContinuationPointer;

public class Hl7ConversionHelper
{
    private static final Log log = LogFactory.getLog(Hl7ConversionHelper.class);
    private static final SimpleDateFormat hl7DateFormat = new SimpleDateFormat("yyyyMMdd");
 
    private static boolean allowAddressQueryAttribute=true;
    private static boolean allowTelecomQueryAttribute=true;
    private static boolean allowSocialSecurityNumberQueryAttribute=true;
    private static final boolean allowAddressResponseAttribute=true;
    private static boolean allowTelecomResponseAttribute=true;
    private static boolean allowSocialSecurityNumberResponseAttribute=true;
    
    private static String localHomeCommunityId;
    
    /**
     * 	We parse the request message based on the requirements of the NHIN Patient Discovery
     *  Web Service Interface Specification:
     *  
     *  Required values for the Patient Discovery request:
     *  1.LivingSubjectName Parameter: Both “family” and “given” elements are required.  
     *  	If patients are known by multiple names or have had a name change, the alternative names shall be
     *  	specified as multiple instances of LivingSubjectName.  Inclusion of all current and former names
     *  	increases the likelihood of a correct match but may incur privacy concerns.
     *  	The sequence of given names in the given name list reflects the sequence in which they are
     *  	known – first, second, third etc. The first name is specified in the first “given” element in the list.
     *  	Any middle name is specified in the second “given” element in the list when there are no more than two
     *  	“given” elements.
     *  2.LivingSubjectAdministrativeGender Parameter: Is required. Coded using the HL7 coding for AdministrativeGender;
     *  	namely code equal to one of “M” (Male) or “F “(Female) or “UN” (Undifferentiated).
     *  3.LivingSubjectBirthTime Parameter: Is required. The contents must contain the greatest degree of detail
     *  	as is available.
     * @param iActorDescription 
     * @param connection 
     */
	public static PdqQuery extractQueryPerson(IActorDescription actor, org.hl7.v3.PRPAIN201305UV02 findCandidatesRequest) {
		PdqQuery query = new PdqQuery();
		log.debug("Extracting query criteria from message:\n" + findCandidatesRequest);

		try {
			query.setPrefix( Configuration.getPropertySetValue(actor, "QueryProperties", "WildcardBefore", false) );
			query.setSuffix( Configuration.getPropertySetValue(actor, "QueryProperties", "WildcardAfter", false) );
		} catch (IheConfigurationException e) {
			log.error("Enable to load the QueryProperties for the PDQ Supplier: " + e, e);
		}
		
        if (findCandidatesRequest == null) {
            log.warn("input message was null, no query parameters present in message");
            return null;
        }

        PRPAMT201306UV02ParameterList queryParamList = extractQueryParameterList(findCandidatesRequest);
        if (queryParamList == null) {
        	log.warn("Unable to extract query parameter list from input message.");
        	return null;
        }

        populatePersonIdentifiers(query, queryParamList);
        populatePersonNameAttributes(query, queryParamList);
        populatePersonGender(query, queryParamList);
        populatePersonBirthDate(query, queryParamList);
        if (allowAddressQueryAttribute) {
        	populatePersonAddress(query, queryParamList);
        }
        if (allowTelecomQueryAttribute) {
        	populatePersonPhoneNumber(query, queryParamList);
        }
        
        populateReturnDomains(query, getIdsFromOtherIDsScopingOrganization(findCandidatesRequest) );		
        return query;
	}

	public static ContinuationPointer extractContinuation(PRPAIN201305UV02 message) {
		log.debug("Extracting continuation parameters from query request:\n" + message);
		
		if (message.getControlActProcess() == null ||
				message.getControlActProcess().getQueryByParameter() == null ||
				message.getControlActProcess().getQueryByParameter().getValue() == null) {
			log.info("controlActProcess or query parameter section is missing.");
            return null;
		}
		PRPAMT201306UV02QueryByParameter queryParams = message.getControlActProcess()
				.getQueryByParameter().getValue();
		ContinuationPointer contPointer = new ContinuationPointer();
		if (queryParams.getInitialQuantity() == null ||
				queryParams.getInitialQuantity().getValue() == null ||
				queryParams.getInitialQuantity().getValue().intValue() == 0 ||
				queryParams.getQueryId() == null ||
				(queryParams.getQueryId().getRoot() == null && queryParams.getQueryId().getExtension() == null)) {
			return null;
		}
		contPointer.setPointer(buildPointerFromQueryId(queryParams.getQueryId()));
		return contPointer;
	}

	public static Integer extractQueryInitialQuantity(PRPAIN201305UV02 message) {
		// We don't need to make sure that the intermediate objects are not null because the assumption
		// is that you will only call this one if the method extractContinuation returned a non-null ContinuationPointer
		// object.
		//
		PRPAMT201306UV02QueryByParameter queryParams = message.getControlActProcess()
				.getQueryByParameter().getValue();
		
		if( queryParams.getInitialQuantity() == null ) {
			return null;
		}
			
		return queryParams.getInitialQuantity().getValue().intValue();
	}
	
    public static String buildPointerFromQueryId(II id) {
    	StringBuffer pointer = new StringBuffer();
    	if (id.getRoot() != null) {
    		pointer.append(id.getRoot());
    	}
    	if (id.getExtension() != null) {
    		pointer.append(":").append(id.getExtension());
    	}
		return pointer.toString();
	}

	private static boolean hasLivingSubjectIds(PRPAMT201306UV02ParameterList params) {
		return params.getLivingSubjectId() != null &&
			params.getLivingSubjectId().size() > 0 &&
			params.getLivingSubjectId().get(0) != null;
	}

	private static boolean hasLivingSubjectIdValue(PRPAMT201306UV02LivingSubjectId id) {
		return id.getValue() != null &&
			id.getValue().size() > 0 &&
			id.getValue().get(0) != null &&
			id.getValue().get(0).getExtension() != null &&
			id.getValue().get(0).getRoot() != null;
	}

	private static boolean hasLivingSubjectBirthTimeValue(
			PRPAMT201306UV02LivingSubjectBirthTime birthTime) {
		return birthTime.getValue() != null &&
		        birthTime.getValue().size() > 0 &&
		        birthTime.getValue().get(0) != null;
	}

	private static boolean hasLivingSubjectAdministrativeGenderCodeValue(PRPAMT201306UV02LivingSubjectAdministrativeGender gender) {
		return gender.getValue() != null &&
		        gender.getValue().size() > 0 &&
		        gender.getValue().get(0) != null;
	}

	private static boolean hasLivingSubjectBirthTime(PRPAMT201306UV02ParameterList params) {
		return params.getLivingSubjectBirthTime() != null &&
	        params.getLivingSubjectBirthTime().size() > 0 &&
	        params.getLivingSubjectBirthTime().get(0) != null;
	}

	private static boolean hasPatientAddress(PRPAMT201306UV02ParameterList params) {
		return params.getPatientAddress() != null &&
			params.getPatientAddress().size() > 0 &&
			params.getPatientAddress().get(0) != null;
	}

	private static boolean hasPatientAddressValue(PRPAMT201306UV02PatientAddress address) {
		return address != null &&
			address.getValue() != null &&
			address.getValue().size() > 0 &&
			address.getValue().get(0) != null;
	}

	private static boolean hasPatientPhoneNumber(PRPAMT201306UV02ParameterList params) {
		return params.getPatientTelecom() != null &&
			params.getPatientTelecom().size() > 0 &&
			params.getPatientTelecom().get(0) != null;
	}

	private static boolean hasPatientPhoneNumberValue(PRPAMT201306UV02PatientTelecom phoneNumber) {
		return phoneNumber.getValue() != null &&
			phoneNumber.getValue().size() > 0 &&
			phoneNumber.getValue().get(0) != null;
	}

	private static boolean hasLivingSubjectAdministrativeGender(PRPAMT201306UV02ParameterList params) {
		return params.getLivingSubjectAdministrativeGender() != null &&
			params.getLivingSubjectAdministrativeGender().size() > 0 &&
	        params.getLivingSubjectAdministrativeGender().get(0) != null;
	}

	private static boolean hasLivingSubjectNameValue(PRPAMT201306UV02LivingSubjectName name) {
		return name.getValue() != null && name.getValue().size() > 0 && name.getValue().get(0) != null;
	}

	private static boolean hasLivingSubjectName(PRPAMT201306UV02ParameterList params) {
		return params.getLivingSubjectName() != null
				&& params.getLivingSubjectName().size() > 0
				&& params.getLivingSubjectName().get(0) != null;
	}

	private static PRPAMT201306UV02ParameterList extractQueryParameterList(
			org.hl7.v3.PRPAIN201305UV02 findCandidatesRequest) {
		PRPAMT201306UV02ParameterList queryParamList = null;
		PRPAIN201305UV02QUQIMT021001UV01ControlActProcess controlActProcess = findCandidatesRequest.getControlActProcess();
        if (controlActProcess == null) {
            log.info("controlActProcess is null - no query parameters present in message");
            return null;
        }

        if (controlActProcess.getQueryByParameter() != null &&
                controlActProcess.getQueryByParameter().getValue() != null) {
            PRPAMT201306UV02QueryByParameter queryParams = controlActProcess.getQueryByParameter().getValue();

            if (queryParams.getParameterList() != null) {
                queryParamList = queryParams.getParameterList();
            }

        }
        return queryParamList;
	}

	public static QUQIMT000001UV01QueryContinuation extractQueryContinuation(QUQIIN000003UV01Type queryContinuationRequest) {
		
		QUQIMT000001UV01ControlActProcess controlActProcess = queryContinuationRequest.getControlActProcess();		
        if (controlActProcess == null) {
            log.info("controlActProcess is null - no query continuation present in message");
            return null;
        }		
        QUQIMT000001UV01QueryContinuation queryContinuation = controlActProcess.getQueryContinuation();	
        
        return queryContinuation;
	}
	
	private static void populateReturnDomains(PdqQuery query, List<Identifier> identifiers) {
		query.addReturnDomains( identifiers );		
	}
	
	private static void populatePersonIdentifiers(PdqQuery query, PRPAMT201306UV02ParameterList queryParamList) {
		log.debug("Entering Hl7ConversionHelper.populatePersonIdentifiers method...");
		
		//Extract the patient identifiers in the requesting message
		if (!hasLivingSubjectIds(queryParamList)) {
			log.warn("Message does not include a living subject ID.");
			return;
		}
		
		List<PRPAMT201306UV02LivingSubjectId> idList = queryParamList.getLivingSubjectId();
		for (PRPAMT201306UV02LivingSubjectId id : idList) {
			if (!hasLivingSubjectIdValue(id)) {
				log.warn("Message does not include a living subject ID value.");
				continue;
			}
			
			II identifier = id.getValue().get(0);
			// If this is the SSN then it requires special treatment within OpenEMPI
			// The following text below is quoted from the NHIN Patient Discovery Specification
			// Social Security Number – SSN is specified in a LivingSubjectId element – potentially one of several. 
			// SSN is designated using the OID 2.16.840.1.113883.4.1
			//
			if ((identifier.getRoot().equalsIgnoreCase(ConversionConstants.SSN_OID) && allowSocialSecurityNumberQueryAttribute)) {
				log.debug("Message includes the SSN as a query attribute.");
				query.setSsn(identifier.getExtension());
				continue;
			}
			
			// If the identifier is not the SSN then it is the Patient ID assigned to the patient by the initiating gateway
			// for v3, NamespaceIdentifier as null. 
			Identifier domain = new Identifier(null, identifier.getRoot(), HL7Constants.UNIVERSAL_IDENTIFIER_TYPE_CODE_ISO);
				
			PatientIdentifier patientIdentifier = new PatientIdentifier(identifier.getExtension(), domain);
			
			query.setPatientIdentifier(patientIdentifier);
			
			log.debug("Added patient identifier of: " + patientIdentifier);
		}
	}

	@SuppressWarnings("unchecked")
	private static void populatePersonNameAttributes(PdqQuery query, PRPAMT201306UV02ParameterList params) {
		log.debug("Populating person name attributes from name.");

		// Extract the name from the query parameters - Assume only one was
		// specified
		if (!hasLivingSubjectName(params)) {
			log.warn("Message does not include a living subject name: " + params);
			return;
		}
		PRPAMT201306UV02LivingSubjectName name = params.getLivingSubjectName().get(0);
		
		if (!hasLivingSubjectNameValue(name)) {
			log.warn("Message does not include a living subject name: " + params);
			return;
		}
		
		boolean isWildcardSearch=false;
		Object obj = name.getValue().get(0);
		if (obj != null && obj instanceof ENExplicit) {
			ENExplicit names = (ENExplicit) obj;
			if (names.getUse() != null && names.getUse().size() > 0 && names.getUse().get(0).equalsIgnoreCase(HL7Constants.USE_CODE_SRCH)) {
				isWildcardSearch = true;
			}
		}
		List<Serializable> choice = name.getValue().get(0).getContent();

		String nameString = "";
		int givenNameIndex=0;
		EnExplicitGiven givenName = null;
		EnExplicitGiven middleName = null;
		EnExplicitFamily familyName = null;
		for (Iterator<Serializable> iterSerialObjects = choice.iterator(); iterSerialObjects.hasNext(); ) {
			log.info("in iterSerialObjects.hasNext() loop");
			Serializable contentItem = iterSerialObjects.next();

			if (contentItem instanceof String) {
				log.debug("contentItem is string");
				String strValue = (String) contentItem;
				
				if (nameString != null) {
					nameString += strValue;
				} else {
					nameString = strValue;
				}
				log.debug("nameString=" + nameString);
			} else if (contentItem instanceof JAXBElement) {
				log.debug("contentItem is JAXBElement");
				JAXBElement oJAXBElement = (JAXBElement) contentItem;
				if (oJAXBElement.getValue() instanceof EnExplicitFamily) {
					familyName = (EnExplicitFamily) oJAXBElement.getValue();
					log.debug("found lastname element; content=" + familyName.getContent());
				} else if (oJAXBElement.getValue() instanceof EnExplicitGiven) {
					if (givenNameIndex == 0) {
						givenName = (EnExplicitGiven) oJAXBElement.getValue();
						log.info("found firstname element; content=" + givenName.getContent());
						givenNameIndex++;
					} else {
						middleName = (EnExplicitGiven) oJAXBElement.getValue();
						log.info("found middlename element; content=" + givenName.getContent());
						givenNameIndex++;
					}
				} else {
					log.warn("other name part=" + oJAXBElement.getValue());
				}
			} else {
				log.info("contentItem is other");
			}
		}

		PersonName personName = new PersonName();
		if ((familyName != null && familyName.getContent() != null) || 
				(givenName != null && givenName.getContent() != null) ||
				(middleName != null && middleName.getContent() != null)) {
			
			if (familyName != null && familyName.getContent() != null) {

				if (isWildcardSearch) {
					personName.setLastName(familyName.getContent() + "*");
				} else {
					personName.setLastName(familyName.getContent());			
				}
			}
			
			if (givenName != null && givenName.getContent() != null) {
				if (isWildcardSearch) {
					personName.setFirstName(givenName.getContent() + "*");
				} else {					
					personName.setFirstName(givenName.getContent());
				}
			}
			
			if (middleName != null && middleName.getContent() != null) {

			}
		} else {

			if (nameString.length() > 0) {

				personName.setLastName(nameString);
			}
		}
		query.setPersonName(personName);
		
		log.debug("Added person name attributes to the query person: " + query);
	}
	
	private static void populatePersonBirthDate(PdqQuery query, PRPAMT201306UV02ParameterList queryParamList) {
        log.debug("Entering Hl7ConversionHelper.populatePersonIdentifiers.populatePersonBirthDate method...");
        
        // Extract the birth time from the query parameters - Assume only one was specified
        if (!hasLivingSubjectBirthTime(queryParamList)) {
        	log.warn("Message does not include a living subject birth time.");
        	return;
        }
        
        PRPAMT201306UV02LivingSubjectBirthTime birthTime = queryParamList.getLivingSubjectBirthTime().get(0);
        if (!hasLivingSubjectBirthTimeValue(birthTime)) {
        	log.warn("Message does not include a valid living subject birth time value.");
        	return;
        }
		
        IVLTSExplicit birthday = birthTime.getValue().get(0);
        log.debug("Found birthTime in query parameters = " + birthday.getValue());

        try {
            java.util.Date dob = hl7DateFormat.parse(birthday.getValue());
        	log.debug("Extracted dob = " + dob.toString());

        	Calendar cal = Calendar.getInstance();
        	cal.setTime(dob);
        	query.setBirthDate(cal);
        	
        } catch (Exception ex) {
        	log.warn("Message does not include a valid living subject birth time value.");
        }
	}

	/**
	 * LivingSubjectAdministrativeGender Parameter: Is required. Coded using the HL7 coding for
	 * AdministrativeGender; namely code equal to one of “M” (Male) or “F “(Female) or “UN”
	 * (Undifferentiated).
	 * 
	 * @param person
	 * @param queryParamList
	 */
	private static void populatePersonGender(PdqQuery query, PRPAMT201306UV02ParameterList queryParamList) {
        // Extract the gender from the query parameters - Assume only one was specified
        if (!hasLivingSubjectAdministrativeGender(queryParamList)) {
        	log.warn("Message does not include a living subject admistrative gender code.");
        	return;
        }

        PRPAMT201306UV02LivingSubjectAdministrativeGender adminGender = queryParamList.getLivingSubjectAdministrativeGender().get(0);
        if (!hasLivingSubjectAdministrativeGenderCodeValue(adminGender)) {
        	log.warn("Message does not include a living subject admistrative gender code value.");
        	return;
        }
        
        CE administrativeGenderCode = adminGender.getValue().get(0);
        String value = administrativeGenderCode.getCode();
        log.debug("Found living subject administrative gender code value of " + value);
        if (value.equalsIgnoreCase(ConversionConstants.MALE_GENDER_CODE)) {

        	query.setSex(SexType.MALE);        	
        } else if (value.equalsIgnoreCase(ConversionConstants.FEMALE_GENDER_CODE)) {

        	query.setSex(SexType.FEMALE);  
        } else if (value.equalsIgnoreCase(ConversionConstants.UNDIFFERENTIATED_GENDER_CODE)) {

        	query.setSex(SexType.UNKNOWN);  
        } else {
        	log.warn("Found unknown living subject administrative gender code value of " + value);
        }
    }

	@SuppressWarnings("unchecked")
	private static void populatePersonAddress(PdqQuery query, PRPAMT201306UV02ParameterList queryParamList) {
		// Extract the address of the person from the query parameters - This is an optional attribute
		// so it may not be present in the message
		if (!hasPatientAddress(queryParamList)) {
			log.debug("Message does not include the address as a query criterion.");
			return;
		}
		PRPAMT201306UV02PatientAddress address = queryParamList.getPatientAddress().get(0);

		if (!hasPatientAddressValue(address)) {
			log.debug("Message does not include the address value as a query criterion.");
			return;
		}
        
		List<Serializable> addressValue = address.getValue().get(0).getContent();
		
		String nameString = "";
		Address queryAddress = new Address();
		for (Iterator<Serializable> iterSerialObjects = addressValue.iterator(); iterSerialObjects.hasNext(); ) {
			log.info("in iterSerialObjects.hasNext() loop");
			Serializable contentItem = iterSerialObjects.next();
			if (contentItem instanceof String) {
				log.debug("contentItem is string");
				String strValue = (String) contentItem;
				
				if (nameString != null) {
					nameString += strValue;
				} else {
					nameString = strValue;
				}
				log.debug("nameString=" + nameString);
			} else if (contentItem instanceof JAXBElement) {
				log.debug("contentItem is JAXBElement");
				JAXBElement oJAXBElement = (JAXBElement) contentItem;
				log.debug("found element of type: " + oJAXBElement.getValue().getClass());
				

				if (oJAXBElement.getValue() instanceof AdxpExplicitStreetAddressLine) {
					AdxpExplicitStreetAddressLine addressLine = (AdxpExplicitStreetAddressLine) oJAXBElement.getValue();
					if (addressLine.getContent() != null) {
						log.debug("found Address Line element; content=" + addressLine.getContent());

						queryAddress.setAddLine1(addressLine.getContent());
					}
				} else if (oJAXBElement.getValue() instanceof AdxpExplicitCity) {
					AdxpExplicitCity city = (AdxpExplicitCity) oJAXBElement.getValue();
					if (city.getContent() != null) {
						log.debug("found city element; content=" + city.getContent());

						queryAddress.setAddCity(city.getContent());
					}
				} else if (oJAXBElement.getValue() instanceof AdxpExplicitState) {
					AdxpExplicitState state = (AdxpExplicitState) oJAXBElement.getValue();
					if (state.getContent() != null) {
						log.debug("found state element; content=" + state.getContent());

						queryAddress.setAddState(state.getContent());
					}
				} else if (oJAXBElement.getValue() instanceof AdxpExplicitPostalCode) {
					AdxpExplicitPostalCode postalCode = (AdxpExplicitPostalCode) oJAXBElement.getValue();
					if (postalCode.getContent() != null) {
						log.debug("found postalCode element; content=" + postalCode.getContent());

						queryAddress.setAddZip(postalCode.getContent());
					}
				} else if (oJAXBElement.getValue() instanceof AdxpExplicitCountry) {
					AdxpExplicitCountry country = (AdxpExplicitCountry) oJAXBElement.getValue();
					if (country.getContent() != null) {
						log.debug("found country element; content=" + country.getContent());
						queryAddress.setAddCountry(country.getContent());
					}
				} else {
					log.warn("other name part=" + oJAXBElement.getValue());
				}				
			} else {
				log.info("contentItem is other");
			}						
		}
		query.setAddress(queryAddress);
	}

	private static void populatePersonPhoneNumber(PdqQuery query, PRPAMT201306UV02ParameterList queryParamList) {
		// Extract the phone number of the person from the query parameters - This is an optional attribute
		// so it may not be present in the message
		if (!hasPatientPhoneNumber(queryParamList)) {
			log.debug("Message does not include the phone number as a query criterion.");
			return;
		}
		PRPAMT201306UV02PatientTelecom phoneNumber = queryParamList.getPatientTelecom().get(0);

		if (!hasPatientPhoneNumberValue(phoneNumber)) {
			log.debug("Message does not include the phone number value as a query criterion.");
			return;
		}
		
		String value = phoneNumber.getValue().get(0).getValue();
        log.debug("Found patientTelecom in query parameters = " + value);
        
        PhoneNumber phone = new PhoneNumber();
        if (value.startsWith(ConversionConstants.TELECOM_URL_SCHEME)) {

        	phone.setNumber(value.substring(value.indexOf(ConversionConstants.TELECOM_URL_SCHEME)+ConversionConstants.TELECOM_URL_SCHEME.length()));
        } else {

        	phone.setNumber(value);
        }
        query.setPhone(phone);
	}
	
	
	/**
	 * Required values for the Patient Discovery response:
	 * 1.Person.name element: Both “family” and “given” elements are required.  If patients are known by multiple
	 * 		names or have had a name change, the alternative names shall be specified as multiple Patient.name elements.
	 * 		Inclusion of all current and former names increases the likelihood of a correct match. 
	 * 		The sequence of given names in the given name list reflects the sequence in which they are known – first, second, third etc.
	 * 		The first name is specified in the first “given” element in the list. Any middle name is specified in the second “given”
	 * 		element in the list when there are no more than two “given” elements.
	 * 2.Person.AdministrativeGenderCode element: Is required.  Coded using the HL7 coding for AdministrativeGender;
	 * 		namely code equal to one of “M” (Male) or “F “(Female) or “UN” (Undifferentiated).
	 * 3.Person.BirthTime Parameter: Is required.  The contents must contain the greatest degree of detail as is available.
	 * 
	 * Required values if available and if allowed for the Patient Discovery Request and Response:
	 * 1.Address – The “streetAddressLine”, “city”, “state”, “postalCode” shall be used for elements of the address.
	 * 		Multiple “streetAddressLine” elements may be used if necessary and are specified in order of appearance in the address.
	 * 		For more information about coding of addresses see 
	 * 		htp://www.hl7.org/v3ballot/html/infrastructure/datatypes/datatypes.htm#prop-AD.formatted
	 * 2.PatientTelecom – a single phone number. See section 3.1.5.1 for details regarding coding of phone numbers.
	 * 3.Social Security Number – SSN is specified in a LivingSubjectId element – potentially one of several.
	 * 		When specified within the response, the SSN is specified in an OtherIDs element.
	 * 		SSN is designated using the OID 2.16.840.1.113883.4.1.
	 * @param initialQuantity 
	 * 
	 * @param candidates
	 * @return
	 */
    public static PRPAIN201306UV02 generateResponseMessage(Integer start, Integer quantity, List<Patient> candidates, PRPAIN201305UV02 request, boolean isSystemError, List<Identifier> invalidDomains) {
    	log.debug("Begin generateResponseMesssage");  
    	
    	PRPAIN201306UV02 message = new PRPAIN201306UV02();   	
    	if(isSystemError) {
			buildMessageTransmissionWrapper(request, message, AcknowledgementType.AE.value(), invalidDomains);
    	} else {
    		buildMessageTransmissionWrapper(request, message, AcknowledgementType.AA.value(), null);    		
    	}
        buildControlActProcess(start, quantity, candidates, request, message, isSystemError);
    	return message;
    }

    public static PRPAIN201306UV02 generateResponseMessage(Integer start, Integer quantity, ContinuationPointer continuation, QUQIIN000003UV01Type request) {
    	log.debug("Begin generateResponseMesssage for continuation request");  
    	
    	PRPAIN201306UV02 message = new PRPAIN201306UV02();   
    	if( continuation != null ) {
	    	buildMessageTransmissionWrapper(request, message, AcknowledgementType.AA.value());    			
	
	        buildControlActProcess(start, quantity, continuation, message);
    	} else {
	    	buildMessageTransmissionWrapper(request, message, AcknowledgementType.AE.value());    		
    	}
    	
    	return message;
    }
    
    public static MCCIIN000002UV01 generateResponseMessage(QUQIIN000003UV01Type request,  boolean isSystemError) {
    	log.debug("Begin generateResponseMesssage for cancellation request");  
    	
    	MCCIIN000002UV01 message = new MCCIIN000002UV01();   
       	if(isSystemError) {
        	buildMessageTransmissionWrapper(request, message, AcknowledgementType.AE.value());    		
    	} else {
        	buildMessageTransmissionWrapper(request, message, AcknowledgementType.CA.value());    			
    	}    			
   	
    	return message;
    }
    
	private static void buildControlActProcess(Integer start, Integer quantity, List<Patient> candidates, PRPAIN201305UV02 request, PRPAIN201306UV02 message, boolean isSystemError) {
		message.setControlActProcess(generateMFMIMT700711UV01ControlActProcess(start, quantity, candidates, request, message, isSystemError));
	}
	
	
	private static void buildControlActProcess(Integer start, Integer quantity, ContinuationPointer continuation, PRPAIN201306UV02 message) {
		message.setControlActProcess(generateMFMIMT700711UV01ControlActProcess(start, quantity, continuation, message));
	}
	/**
	 * The IHE Patient Discovery Specification indicates the following response codes should be used based on
	 * the five different response conditions identified by the specification:
	 * 
	 * The Initiating Gateway Actor shall act on the query response as described by the following 5
	 * cases:
	 * Case 1: The Responding Gateway Actor finds exactly one patient record matching the criteria
	 * sent in the query parameters.
	 * AA (application accept) is returned in Acknowledgement.typeCode (transmission wrapper).
	 * OK (data found, no errors) is returned in QueryAck.queryResponseCode (control act wrapper)
	 * One RegistrationEvent (and the associated Patient role, subject of that event) is returned from the
	 * patient information source for the patient record found. The community associated with the
	 * Initiating Gateway may use the patient demographics and identifiers to: a) run an independent
	 * matching algorithm to ensure the quality of the match b) use the designated patient identifier in a
	 * Cross Gateway Query to get information about records related to the patient c) cache the
	 * correlation for future use (see ITI TF-2b: 3.55.4.2.3.1 for more information about caching) d)
	 * use a Patient Location Query transaction to get a list of patient data locations.
	 * 
	 * Case 2: The Responding Gateway Actor finds more than one patient close to matching the
	 * criteria sent in the query parameters and the policy allows returning multiple.
	 * AA (application accept) is returned in Acknowledgement.typeCode (transmission wrapper).
	 * OK (data found, no errors) is returned in QueryAck.queryResponseCode (control act wrapper)
	 * One RegistrationEvent (and the associated Patient role, subject of that event) is returned for each
	 * patient record found. The community associated with the Initiating Gateway may run its own
	 * matching algorithm to select from the list of returned patients. If a correlation is found the
	 * Responding Gateway may continue as if only one entry had been returned. If a correlation is still
	 * not clear it is expected that human intervention is required, depending on the policies of the
	 * Initiating Gateway’s community.
	 * 
	 * Case 3: The Responding Gateway Actor finds more than one patient close to matching the
	 * criteria sent in the query parameters but no matches close enough for the necessary assurance
	 * level and more attributes might allow the Responding Gateway to return a match.
	 * AA (application accept) is returned in Acknowledgement.typeCode (transmission wrapper).
	 * OK (data found, no errors) is returned in QueryAck.queryResponseCode (control act wrapper)
	 * No RegistrationEvent is returned in the response, but the Responding Gateway provides a
	 * suggestion in terms of demographics that may help identify a match. The mechanism for
	 * specifying the suggestion is detailed in ITI TF-2b: 3.55.4.2.2.6 for description of coding of the
	 * response. The Initiating Gateway may use this feedback to initiate a new Cross Gateway Patient
	 * Discovery request including the requested additional attributes.
	 * 
	 * Case 4: The Responding Gateway Actor finds no patients anywhere close to matching the
	 * criteria sent in the query parameters.
	 * AA (application accept) is returned in Acknowledgement.typeCode (transmission wrapper).
	 * OK (data found, no errors) is returned in QueryAck.queryResponseCode (control act wrapper)
	 * There is no RegistrationEvent returned in the response. The Initiating Gateway can assume this
	 * patient has no healthcare information held by the community represented by the Responding
	 * Gateway. This lack of correlation may be cached, see ITI TF-2b: 3.55.4.2.3.1 for more
	 * information about caching.
	 * 
	 * Case 5: The Responding Gateway Actor is unable to satisfy the request. This may be because
	 * the request came synchronously and an asynchronous request may be feasible, or because the
	 * Responding Gateway Actor is overloaded with other requests and does not expect to answer for a
	 * significant period of time. Or the Responding Gateway may need some manuel configuration
	 * update to authorize responder.
	 * AA (application accept) is returned in Acknowledgement.typeCode (transmission wrapper).
	 * QE (application error) is returned in QueryAck.queryResponseCode (control act wrapper)
	 * There is no RegistrationEvent returned in the response. See ITI TF-2b: 3.55.4.2.2.7 for more
	 * information about coding errors for this case.
	 * @param initialQuantity 
	 */
	private static PRPAIN201306UV02MFMIMT700711UV01ControlActProcess generateMFMIMT700711UV01ControlActProcess(Integer start, Integer quantity, List<Patient> candidates, PRPAIN201305UV02 request,
			PRPAIN201306UV02 message, boolean isSystemError) {
		PRPAIN201306UV02MFMIMT700711UV01ControlActProcess controlActProcess = new PRPAIN201306UV02MFMIMT700711UV01ControlActProcess();
        controlActProcess.setMoodCode(XActMoodIntentEvent.EVN);
        controlActProcess.setClassCode(ActClassControlAct.CACT);
        controlActProcess.setCode(Utilities.generateCd(ConversionConstants.PRPA_TE201306UV02, ConversionConstants.INTERACTION_ID_ROOT));
        
        Integer totalQuantity = 0;
        if (candidates != null) {
        	totalQuantity = candidates.size();
        	
            if (quantity != null) {
            	candidates = getSubList(start, quantity, candidates);
            }
            
	        for (Patient person : candidates) {
	        	// Eliminates the SSN from the list of person identifiers
	        	List<PatientIdentifier> uniqueIdentifiers = extractUniqueIdentifiers(person.getPatientIds());
	        	
	        	// other IDs in ScopingOrganization
				List<Identifier> otherIds = getIdsFromOtherIDsScopingOrganization(request);
				
	
				// If we found any other IDs in ScopingOrganization then we may need to filter the domains
				if (uniqueIdentifiers.size() > 0 && otherIds != null && otherIds.size() > 0) {
					uniqueIdentifiers = filterIdsByScopingOrganization(uniqueIdentifiers, otherIds);
				}
							
	        	for (PatientIdentifier identifier : uniqueIdentifiers) {
	        		controlActProcess.getSubject().add(createSubjectFromPerson(person, identifier, request));
	        	}
	        }
        }
        
        controlActProcess.setQueryAck(createQueryAck(start, quantity, totalQuantity, request.getControlActProcess().getQueryByParameter(), isSystemError));

        // Add in query parameters
        if (request.getControlActProcess() != null &&
        		request.getControlActProcess().getQueryByParameter() != null &&
        		request.getControlActProcess().getQueryByParameter().getValue() != null) {
           controlActProcess.setQueryByParameter(request.getControlActProcess().getQueryByParameter());
           
        }
		return controlActProcess;
	}
	
	private static PRPAIN201306UV02MFMIMT700711UV01ControlActProcess generateMFMIMT700711UV01ControlActProcess(Integer start, Integer quantity, ContinuationPointer continuation, PRPAIN201306UV02 message) {
		PRPAIN201306UV02MFMIMT700711UV01ControlActProcess controlActProcess = new PRPAIN201306UV02MFMIMT700711UV01ControlActProcess();
        controlActProcess.setMoodCode(XActMoodIntentEvent.EVN);
        controlActProcess.setClassCode(ActClassControlAct.CACT);
        controlActProcess.setCode(Utilities.generateCd(ConversionConstants.PRPA_TE201306UV02, ConversionConstants.INTERACTION_ID_ROOT));
        
        Integer totalQuantity = 0;
        if (continuation != null) {
        	List<List<Patient>> listLists = continuation.getPatients();
        	List<Patient> candidates = extractSublist(listLists);       	
        	totalQuantity = listLists.size();
        	
            if (quantity != null) {
            	candidates = getSubList(start, quantity, candidates);  
            	if( candidates.size() == 0 ) {
            		quantity = 0;
            	}
            }
            
	        for (Patient person : candidates) {
	        	// Eliminates the SSN from the list of person identifiers
	        	List<PatientIdentifier> uniqueIdentifiers = extractUniqueIdentifiers(person.getPatientIds());
	        	
	        	// other IDs in ScopingOrganization
				List<Identifier> otherIds = continuation.getReturnDomain();
				
	
				// If we found any other IDs in ScopingOrganization then we may need to filter the domains
				if (uniqueIdentifiers.size() > 0 && otherIds != null && otherIds.size() > 0) {
					uniqueIdentifiers = filterIdsByScopingOrganization(uniqueIdentifiers, otherIds);
				}
							
	        	for (PatientIdentifier identifier : uniqueIdentifiers) {
	        		controlActProcess.getSubject().add(createSubjectFromPerson(person, identifier, null));
	        	}
	        }
        }
        JAXBElement<PRPAMT201306UV02QueryByParameter> queryByParameter = (JAXBElement<PRPAMT201306UV02QueryByParameter>) continuation.getQueryTag();
        
        // queryAck
        controlActProcess.setQueryAck(createQueryAck(start, quantity, totalQuantity, queryByParameter, false));
        
        // queryByParameter
        controlActProcess.setQueryByParameter(queryByParameter);

		return controlActProcess;
	}

	public static List<Patient> extractSublist(List<List<Patient>> lists) {
		List<Patient> subList = new ArrayList<Patient>();
		for (List<Patient> innerList : lists) {
			Patient superPatient = innerList.get(0);
			if (innerList.size() > 0) {
				for (int i=1; i < innerList.size(); i++) {
					superPatient.getPatientIds().addAll(innerList.get(i).getPatientIds());
				}
			}
			subList.add(superPatient);
		}
		return subList;
	}
	
	private static List<Patient> getSubList(Integer start, Integer quantity, List<Patient> list) {
		if (quantity == null || quantity >= list.size()) {
			return list;
		}
		if (start == null) {
			start = 0;
		}
		
		List<Patient> newList = new ArrayList<Patient>();		
		for (int i=start; i < Math.min(start+quantity, list.size()); i++) {
			newList.add(list.get(i));
		}
		return newList;
	}

	private static List<Identifier> getIdsFromOtherIDsScopingOrganization(PRPAIN201305UV02 request) {
		List<Identifier> otherScopingOrganizationIds = new ArrayList<Identifier>();
		
        PRPAMT201306UV02ParameterList queryParamList = extractQueryParameterList(request);
        if (queryParamList != null) {
        	List<PRPAMT201306UV02OtherIDsScopingOrganization> otherIDsScopingOrganization = queryParamList.getOtherIDsScopingOrganization();
        	
			for (PRPAMT201306UV02OtherIDsScopingOrganization otherID : otherIDsScopingOrganization) {
				if (otherID != null && otherID.getValue() != null &&
					otherID.getValue().size() > 0 && otherID.getValue().get(0).getRoot() != null) {
						log.debug("Found an ID in the PDQ query of " + otherID.getValue().get(0));
						Identifier identifier = new Identifier(null, otherID.getValue().get(0).getRoot(), HL7Constants.UNIVERSAL_IDENTIFIER_TYPE_CODE_ISO);
						otherScopingOrganizationIds.add(identifier);
				}
			}
        }	
        return otherScopingOrganizationIds;
	}
	
	private static List<PatientIdentifier> filterIdsByScopingOrganization(List<PatientIdentifier> ids, List<Identifier> scopingOrganization) {
		List<PatientIdentifier> filteredIds = new ArrayList<PatientIdentifier>();
		for (PatientIdentifier id : ids) {
			if (scopingOrganization.contains(id.getAssigningAuthority())) {
				filteredIds.add(id);
			}
		}
		return filteredIds;
	}
	
/*	private static MFMIMT700711UV01QueryAck createQueryAck(Integer start, Integer quantity, Integer total, PRPAIN201305UV02 request, boolean isSystemError) {
        MFMIMT700711UV01QueryAck  result = new MFMIMT700711UV01QueryAck();
        
        if (request.getControlActProcess() != null &&
        		request.getControlActProcess().getQueryByParameter() != null &&
        		request.getControlActProcess().getQueryByParameter().getValue() != null &&
        		request.getControlActProcess().getQueryByParameter().getValue().getQueryId() != null) {
           result.setQueryId(request.getControlActProcess().getQueryByParameter().getValue().getQueryId());
        }
        if (start == null) {
        	start = 0;
        }
        if (quantity == null) {
        	quantity = total;
        }
        result.setResultCurrentQuantity(Utilities.generateINT(quantity));
        result.setResultTotalQuantity(Utilities.generateINT(total));
        int remaining = Math.max(0, total - (start + quantity));
        result.setResultRemainingQuantity(Utilities.generateINT(remaining));
        String responseCode = QueryResponse.OK.name();
        if (isSystemError) {
        	responseCode = QueryResponse.AE.name();
        } else if (quantity.intValue() == 0) {
        	responseCode = QueryResponse.NF.name();
        }
        result.setQueryResponseCode(Utilities.generateCs(responseCode));
        
        return result;
	}
*/	
	private static MFMIMT700711UV01QueryAck createQueryAck(Integer start, Integer quantity, Integer total, JAXBElement<PRPAMT201306UV02QueryByParameter> queryByParameter, boolean isSystemError) {
        MFMIMT700711UV01QueryAck  result = new MFMIMT700711UV01QueryAck();
        
        if (queryByParameter != null &&
        	queryByParameter.getValue() != null &&
        	queryByParameter.getValue().getQueryId() != null) {
            result.setQueryId(queryByParameter.getValue().getQueryId());
        }
        if (start == null) {
        	start = 0;
        }
        if (quantity == null) {
        	quantity = total;
        }
        result.setResultCurrentQuantity(Utilities.generateINT(quantity));
        result.setResultTotalQuantity(Utilities.generateINT(total));
        int remaining = Math.max(0, total - (start + quantity));
        result.setResultRemainingQuantity(Utilities.generateINT(remaining));
        String responseCode = QueryResponse.OK.name();
        if (isSystemError) {
        	responseCode = QueryResponse.AE.name();
        } else if (quantity.intValue() == 0) {
        	responseCode = QueryResponse.NF.name();
        }
        result.setQueryResponseCode(Utilities.generateCs(responseCode));
        
        return result;
	}
	
	/**
	 * Eliminates the SSN from the list of person identifiers if present since it should be 
	 * added to the response message as an asOtherId entry.
	 * 
	 * @param personIdentifiers
	 * @return
	 */
	private static List<PatientIdentifier> extractUniqueIdentifiers(List<PatientIdentifier> personIdentifiers) {
		List<PatientIdentifier> uniqueIds = new java.util.ArrayList<PatientIdentifier>();
		for (PatientIdentifier identifier : personIdentifiers) {
			if (identifier.getAssigningAuthority() != null &&
					identifier.getAssigningAuthority().getNamespaceId() != null &&
					identifier.getAssigningAuthority().getNamespaceId().equalsIgnoreCase(ConversionConstants.SSN_ID_ROOT)) {
				continue;
			}
			uniqueIds.add(identifier);		
		}
		return uniqueIds;
	}
	
	private static void buildMessageTransmissionWrapper(PRPAIN201305UV02 request, PRPAIN201306UV02 message, String acknowledmentCode, List<Identifier> invalidDomains) {
		message.setITSVersion("XML_1.0");
        message.setId(Utilities.generateHl7MessageId(getLocalHomeCommunityId()));
        message.setCreationTime(Utilities.generateCreationTime());
        message.setInteractionId(Utilities.generateHl7Id(ConversionConstants.INTERACTION_ID_ROOT, ConversionConstants.PRPA_IN201306UV02));
        message.setProcessingCode(Utilities.generateCs(ConversionConstants.PROCESSING_CODE_PRODUCTION));
        message.setProcessingModeCode(Utilities.generateCs(ConversionConstants.PROCESSING_MODE_CODE_CURRENT_PROCESSING));
        message.setAcceptAckCode(Utilities.generateCs(ConversionConstants.ACCEPT_ACK_CODE_NEVER));
        message.getReceiver().add(Utilities.generateMCCIMT00300UV01Receiver(request.getSender()));
        message.setSender(Utilities.generateMCCIMT00300UV01Sender(request.getReceiver()));
        message.getAcknowledgement().add(Utilities.generatedAcknowledgment(request.getId(), acknowledmentCode, invalidDomains));
	}

	private static void buildMessageTransmissionWrapper(QUQIIN000003UV01Type request, PRPAIN201306UV02 message, String acknowledmentCode) {
		message.setITSVersion("XML_1.0");
        message.setId(Utilities.generateHl7MessageId(getLocalHomeCommunityId()));
        message.setCreationTime(Utilities.generateCreationTime());
        message.setInteractionId(Utilities.generateHl7Id(ConversionConstants.INTERACTION_ID_ROOT, ConversionConstants.PRPA_IN201306UV02));
        message.setProcessingCode(Utilities.generateCs(ConversionConstants.PROCESSING_CODE_PRODUCTION));
        message.setProcessingModeCode(Utilities.generateCs(ConversionConstants.PROCESSING_MODE_CODE_CURRENT_PROCESSING));
        message.setAcceptAckCode(Utilities.generateCs(ConversionConstants.ACCEPT_ACK_CODE_NEVER));
        message.getReceiver().add(Utilities.generateMCCIMT00300UV01Receiver(request.getSender()));
        message.setSender(Utilities.generateMCCIMT00300UV01SenderByMCCIMT000300UV01Receiver(request.getReceiver()));
        message.getAcknowledgement().add(Utilities.generatedAcknowledgment(request.getId(), acknowledmentCode, null));
	}

	private static void buildMessageTransmissionWrapper(QUQIIN000003UV01Type request, MCCIIN000002UV01 message, String acknowledmentCode) {
		message.setITSVersion("XML_1.0");
        message.setId(Utilities.generateHl7MessageId(getLocalHomeCommunityId()));
        message.setCreationTime(Utilities.generateCreationTime());
        message.setInteractionId(Utilities.generateHl7Id(ConversionConstants.INTERACTION_ID_ROOT, ConversionConstants.MCCI_IN000002UV01));
        message.setProcessingCode(Utilities.generateCs(ConversionConstants.PROCESSING_CODE_PRODUCTION));
        message.setProcessingModeCode(Utilities.generateCs(ConversionConstants.PROCESSING_MODE_CODE_CURRENT_PROCESSING));
        message.setAcceptAckCode(Utilities.generateCs(ConversionConstants.ACCEPT_ACK_CODE_NEVER));
        message.getReceiver().add(Utilities.generateMCCIMT00200UV01Receiver(request.getSender()));
        message.setSender(Utilities.generateMCCIMT00200UV01Sender(request.getReceiver()));
        message.getAcknowledgement().add(Utilities.generatedAcknowledgment(request.getId(), acknowledmentCode));
	}
	
	private static PRPAIN201306UV02MFMIMT700711UV01Subject2 createSubject2FromPerson(Patient person, PatientIdentifier identifier, PRPAIN201305UV02 request) {
		PRPAIN201306UV02MFMIMT700711UV01Subject2 subject = new PRPAIN201306UV02MFMIMT700711UV01Subject2();
		subject.setTypeCode(ParticipationTargetSubject.SBJ);
		PRPAMT201310UV02Patient patient = new PRPAMT201310UV02Patient();
		patient.getClassCode().add(ConversionConstants.CLASS_CODE_PATIENT);
		patient.getId().add(generateIdFromPersonIdentifier(identifier));
		patient.setStatusCode(Utilities.generateCs(ConversionConstants.STATUS_CODE_ACTIVE));
		patient.setPatientPerson(createPatientPersonFromPerson(person, request));
		patient.getSubjectOf1().add(generateSubjectOf1());
		
		subject.setPatient(patient);
		return subject;
	}

	private static JAXBElement<PRPAMT201310UV02Person> createPatientPersonFromPerson(Patient person, PRPAIN201305UV02 request) {
		PRPAMT201310UV02Person thePerson = new PRPAMT201310UV02Person();
		PNExplicit name = Utilities.generatePnExplicit(person.getPatientName().getFirstName(), person.getPatientName().getLastName());
		thePerson.getName().add(name);
		if (person.getAdministrativeSex() != null) {
			thePerson.setAdministrativeGenderCode(Utilities.generateCe(person.getAdministrativeSex().getCDAValue()));	
		}
		if (person.getBirthDateTime() != null) {
			thePerson.setBirthTime(Utilities.generateTSExplicit(person.getBirthDateTime().getTime()));
		}
		
		// If the adapter is configured to return the phone number and we have, it add it to the message.
		if (allowTelecomResponseAttribute && person.getPhoneNumbers() != null) {
			thePerson.getTelecom().add(Utilities.generateTELExplicit(person));
		}
		
		// If the adapter is configured to return the address and we have it, add it to the message
		if (allowAddressResponseAttribute) {
			Address addressPerson = person.getAddresses().get(0);
			ADExplicit address = Utilities.generateADExplicit(addressPerson.getAddLine1(), addressPerson.getAddCity(), addressPerson.getAddState(), addressPerson.getAddZip());
			if (address != null) {
				thePerson.getAddr().add(address);
			}
		}
		
		if (allowSocialSecurityNumberResponseAttribute && Utilities.isNotNullish(person.getSsn())) {
			thePerson.getAsOtherIDs().add(generateSsnAsOtherId(person));
		}
		
        javax.xml.namespace.QName xmlqname = new javax.xml.namespace.QName("urn:hl7-org:v3", "patientPerson");
        JAXBElement<PRPAMT201310UV02Person> jaxbPerson = new JAXBElement<PRPAMT201310UV02Person>(xmlqname, PRPAMT201310UV02Person.class, thePerson);
        
		return jaxbPerson;
	}
	
	private static PRPAMT201310UV02OtherIDs generateSsnAsOtherId(Patient person) {
        PRPAMT201310UV02OtherIDs  otherIds = new PRPAMT201310UV02OtherIDs();
        
        otherIds.getClassCode().add(ConversionConstants.AS_OTHER_IDS_SSN_CLASS_CODE);
        
        // Set the SSN
        II ssn = new II();
        ssn.setExtension(person.getSsn());
        ssn.setRoot(ConversionConstants.SSN_ID_ROOT);
        otherIds.getId().add(ssn);
        
        COCTMT150002UV01Organization scopingOrg = new COCTMT150002UV01Organization();
        scopingOrg.setClassCode(ConversionConstants.CLASS_CODE_ORG);
        scopingOrg.setDeterminerCode(ConversionConstants.DETERMINER_CODE_INSTANCE);
        II orgId = new II();
        orgId.setRoot(ssn.getRoot());
        scopingOrg.getId().add(orgId);
        otherIds.setScopingOrganization(scopingOrg);
        
        return otherIds;
	}

	private static II generateIdFromPersonIdentifier(PatientIdentifier identifier) {
		II id = new II();
		id.setExtension(identifier.getId());
		
		String idRoot = null;
		if (identifier.getAssigningAuthority() != null &&
				identifier.getAssigningAuthority().getUniversalId() != null) {
			idRoot = identifier.getAssigningAuthority().getUniversalId();
		}
		
		if (idRoot != null) {
			id.setRoot(idRoot);
		}		
/*
  		String assigningAuthority = null;
		if (identifier.getAssigningAuthority() != null ) {
			assigningAuthority = identifier.getAssigningAuthority().getNamespaceId();
		}
		
		if (assigningAuthority != null) {
			id.setAssigningAuthorityName(assigningAuthority);
		}
*/		
		return id;
	}
	
	private static PRPAMT201310UV02Subject generateSubjectOf1() {
		PRPAMT201310UV02Subject subjectOf1 = new PRPAMT201310UV02Subject();
		PRPAMT201310UV02QueryMatchObservation Observation = new PRPAMT201310UV02QueryMatchObservation();
		Observation.getClassCode().add("COND");
		Observation.getMoodCode().add("EVN");
		Observation.setCode(Utilities.generateCd("IHE_PDQ", null));		
			INT value = new INT();
			value.setValue(BigInteger.valueOf(100));
		Observation.setValue(value);
		
		subjectOf1.setQueryMatchObservation(Observation);
		return subjectOf1;
		
	}
	
	private static PRPAIN201306UV02MFMIMT700711UV01Subject1 createSubjectFromPerson(Patient person, PatientIdentifier identifier, PRPAIN201305UV02 request) {
		PRPAIN201306UV02MFMIMT700711UV01Subject1 subject = new PRPAIN201306UV02MFMIMT700711UV01Subject1();
		subject.getTypeCode().add(ConversionConstants.ACT_RELATIONSHIP_TYPE);
		PRPAIN201306UV02MFMIMT700711UV01RegistrationEvent regEvent = new PRPAIN201306UV02MFMIMT700711UV01RegistrationEvent();
		regEvent.getClassCode().add(ConversionConstants.CLASS_CODE_REGISTRATION_EVENT);
		regEvent.getMoodCode().add(ConversionConstants.MOOD_CODE_EVENT);
		regEvent.getId().add(Utilities.generateHl7Id(null, null, ConversionConstants.NULL_FLAVOR));
		regEvent.setStatusCode(Utilities.generateCs(ConversionConstants.STATUS_CODE_ACTIVE));
		regEvent.setSubject1(createSubject2FromPerson(person, identifier, request));
		regEvent.setCustodian(createCustodian(person, identifier));
		subject.setRegistrationEvent(regEvent);
		return subject;
	}
	
	private static MFMIMT700711UV01Custodian createCustodian(Patient person, PatientIdentifier identifier) {
        MFMIMT700711UV01Custodian result = new MFMIMT700711UV01Custodian();
        result.getTypeCode().add(ConversionConstants.PARTICIPATION_CUSTODIAN);
        
        result.setAssignedEntity(createAssignedEntity(identifier));
        
        return result;
	}
    
    private static COCTMT090003UV01AssignedEntity createAssignedEntity (PatientIdentifier identifier) {
        COCTMT090003UV01AssignedEntity  assignedEntity = new COCTMT090003UV01AssignedEntity();
        
        II id = new II();
		String idRoot = null;
		if (identifier.getAssigningAuthority() != null &&
				identifier.getAssigningAuthority().getNamespaceId() != null) {
			idRoot = identifier.getAssigningAuthority().getNamespaceId();
		} else if (identifier.getAssigningAuthority() != null &&
				identifier.getAssigningAuthority().getUniversalId() != null) {
			idRoot = identifier.getAssigningAuthority().getUniversalId();
		}
		
		if (idRoot != null) {
			id.setRoot(idRoot);
		}
		assignedEntity.setCode(Utilities.generateCe(ConversionConstants.NO_HEALTH_DATA_LOCATOR_CODE, ConversionConstants.NO_HEALTH_DATA_LOCATOR_CODE_SYSTEM));
		assignedEntity.setClassCode(ConversionConstants.ROLE_CLASS_ASSIGNED);
        assignedEntity.getId().add(id);
        
        return assignedEntity;
    }
    
	public static boolean isAllowSocialSecurityNumberQueryAttribute() {
		return allowSocialSecurityNumberQueryAttribute;
	}

	public void setAllowSocialSecurityNumberQueryAttribute(
			boolean allowSocialSecurityNumberQueryAttribute) {
		Hl7ConversionHelper.allowSocialSecurityNumberQueryAttribute = allowSocialSecurityNumberQueryAttribute;
	}
	
	public static boolean isAllowAddressQueryAttribute() {
		return allowAddressQueryAttribute;
	}

	public void setAllowAddressQueryAttribute(boolean allowAddressQueryAttribute) {
		Hl7ConversionHelper.allowAddressQueryAttribute = allowAddressQueryAttribute;
	}

	public static boolean isAllowTelecomQueryAttribute() {
		return allowTelecomQueryAttribute;
	}

	public void setAllowTelecomQueryAttribute(boolean allowTelecomQueryAttribute) {
		Hl7ConversionHelper.allowTelecomQueryAttribute = allowTelecomQueryAttribute;
	}
	
	public static String getLocalHomeCommunityId() {
		return localHomeCommunityId;
	}

	public void setLocalHomeCommunityId(String localHomeCommunityId) {
		Hl7ConversionHelper.localHomeCommunityId = localHomeCommunityId;
	}	
	
	public static boolean isAllowTelecomResponseAttribute() {
		return allowTelecomResponseAttribute;
	}

	public void setAllowTelecomResponseAttribute(
			boolean allowTelecomResponseAttribute) {
		Hl7ConversionHelper.allowTelecomResponseAttribute = allowTelecomResponseAttribute;
	}

	public static boolean isAllowSocialSecurityNumberResponseAttribute() {
		return allowSocialSecurityNumberResponseAttribute;
	}

	public void setAllowSocialSecurityNumberResponseAttribute(
			boolean allowSocialSecurityNumberResponseAttribute) {
		Hl7ConversionHelper.allowSocialSecurityNumberResponseAttribute = allowSocialSecurityNumberResponseAttribute;
	}
}
