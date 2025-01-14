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
package org.openempi.webapp.client.mvc.configuration;

import java.util.ArrayList;
import java.util.List;

import org.openempi.webapp.client.AppEvents;
import org.openempi.webapp.client.Constants;
import org.openempi.webapp.client.model.CustomFieldWeb;
import org.openempi.webapp.client.model.EntityWeb;
import org.openempi.webapp.client.model.ModelPropertyWeb;
import org.openempi.webapp.client.ui.util.InputFormat;
import org.openempi.webapp.client.ui.util.Utility;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;

public class CustomFieldsConfigurationView extends View
{
	private final String[] SubstringNames = { "start", "end" };
	private final String[] DateTransformationNames = { "dateFormat" };

	private Grid<CustomFieldWeb> grid;
	private final ListStore<CustomFieldWeb> store = new ListStore<CustomFieldWeb>();

	private Dialog addEditCustomFieldDialog = null;
		private Boolean addOrEditFieldMode = true;
		private int editedFieldIndex = 0;
		private CustomFieldWeb editedField;
		private List<ModelPropertyWeb> attributeNames;
//		private ListStore<ModelPropertyWeb> customFieldNameStore = new ListStore<ModelPropertyWeb>();
		private final ListStore<ModelPropertyWeb> attributeNameStore = new ListStore<ModelPropertyWeb>();
		private final ListStore<ModelPropertyWeb> trafoFuncNameStore = new ListStore<ModelPropertyWeb>();

//		private ComboBox<ModelPropertyWeb> customFieldNameCombo = new ComboBox<ModelPropertyWeb>();
		private TextField<String> customFieldNameText;
		private TextField<String> customFieldNameDescriptionText;
		private final ComboBox<ModelPropertyWeb> attributeNameCombo = new ComboBox<ModelPropertyWeb>();
		private final ComboBox<ModelPropertyWeb> trafoFuncNameCombo = new ComboBox<ModelPropertyWeb>();

	// Function Parameters
		private ContentPanel functionParameterPanel = null;
		private ComboBox<ModelPropertyWeb> parameterNameCombo = new ComboBox<ModelPropertyWeb>();
		private final ListStore<ModelPropertyWeb> parameterNameStore = new GroupingStore<ModelPropertyWeb>();

		private Grid<ModelPropertyWeb> parametersGrid;
		private final ListStore<ModelPropertyWeb> parameterStore = new GroupingStore<ModelPropertyWeb>();

	private LayoutContainer container;

	private EntityWeb currentEntity;

	@SuppressWarnings("unchecked")
	public CustomFieldsConfigurationView(Controller controller) {
		super(controller);

		List<ModelPropertyWeb> trafoFuncNames = Registry.get(Constants.TRANSFORMATION_FUNCTION_NAMES);
		try {
			trafoFuncNameStore.add(trafoFuncNames);
		} catch (Exception e) {
			Info.display("Message", e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleEvent(AppEvent event) {
		if (event.getType() == AppEvents.CustomFieldsConfigurationView) {
			initUI();
		} else if (event.getType() == AppEvents.CustomFieldsConfigurationReceived) {

			currentEntity = Registry.get(Constants.ENTITY_ATTRIBUTE_MODEL);

			store.removeAll();
			List<CustomFieldWeb> customFields = event.getData();

			for (CustomFieldWeb customField : customFields) {
				customField.setFieldDescription(Utility.convertToDescription(customField.getFieldName()));
				customField.setSourceFieldNameDescription(Utility.convertToDescription(customField.getSourceFieldName()));
				customField.setTransformationFunctionNameDescription(Utility.convertToDescription(customField.getTransformationFunctionName()));
			}

			store.add(customFields);

			grid.getSelectionModel().select(0, true);
			grid.getSelectionModel().deselect(0);

		} else if (event.getType() == AppEvents.CustomFieldsConfigurationSaveComplete) {
			// String message = event.getData();
	        MessageBox.alert("Information", "Custom Field Configuration was successfully saved", null);

		} else if (event.getType() == AppEvents.Error) {
			String message = event.getData();
	        MessageBox.alert("Information", "Failure: " + message, null);
		}
	}

	@SuppressWarnings("unchecked")
	private void initUI() {
		long time = new java.util.Date().getTime();
		GWT.log("Initializing the UI ", null);

		attributeNameStore.removeAll();
		attributeNames = Registry.get(Constants.PERSON_MODEL_ATTRIBUTE_NAMES);
		if (attributeNames != null) {
			attributeNameStore.add(attributeNames);
		}

		controller.handleEvent(new AppEvent(AppEvents.CustomFieldsConfigurationRequest));

		buildAddCustomFieldDialog();
		container = new LayoutContainer();
		container.setLayout(new CenterLayout());

		ColumnConfig entityName = new ColumnConfig("entityName", "Entity Name", 0);
		entityName.setHidden(true);
		ColumnConfig customFieldName = new ColumnConfig("fieldName", "Custom Field Name", 130);
		ColumnConfig customFieldNameDescription = new ColumnConfig("fieldDescription", "Custom Field Name Description", 180);
		ColumnConfig sourceFieldName = new ColumnConfig("sourceFieldNameDescription", "Source Field Name", 130);
		ColumnConfig trafoFuncName = new ColumnConfig("transformationFunctionNameDescription", "Transformation Function Name", 190);
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		config.add(customFieldName);
		config.add(customFieldNameDescription);
		config.add(sourceFieldName);
		config.add(trafoFuncName);
		config.add(entityName);

		final ColumnModel cm = new ColumnModel(config);

		grid = new Grid<CustomFieldWeb>(store, cm);
		grid.setBorders(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		ContentPanel cp = new ContentPanel();
		cp.setHeading("Custom Field Configuration");
		cp.setFrame(true);
		cp.setIcon(IconHelper.create("images/folder.png"));
		cp.setLayout(new FillLayout());
		cp.setSize(650, 370);

		ToolBar toolBar = new ToolBar();
		final Button addFieldButton =
			new Button("Add Field", IconHelper.create("images/folder_go.png"), new SelectionListener<ButtonEvent>() {
		        @Override
		        public void componentSelected(ButtonEvent ce) {
		        	addOrEditFieldMode = true;
		        	addEditCustomFieldDialog.show();
		        	editedFieldIndex = 0;
		        	editedField = null;

		    		customFieldNameText.enable();
		        	customFieldNameText.clear();
		        	customFieldNameDescriptionText.clear();
		        	attributeNameCombo.clearSelections();
		        	trafoFuncNameCombo.clearSelections();


		        	parameterNameCombo.clear();
		        	parameterNameStore.removeAll();
		        	parameterStore.removeAll();
	        		functionParameterPanel.disable();
		        }
		    });
	    toolBar.add(addFieldButton);
	    final Button editFieldButton =
			new Button("Edit Field", IconHelper.create("images/folder_edit.png"), new SelectionListener<ButtonEvent>() {
		        @Override
		        public void componentSelected(ButtonEvent ce) {
		        	addOrEditFieldMode = false;
		  			CustomFieldWeb editField = grid.getSelectionModel().getSelectedItem();
					if (editField == null) {
						Info.display("Information", "You must first select a field to be edited before pressing the \"Edit\" button.");
						return;
					}

		        	addEditCustomFieldDialog.show();

		        	editedFieldIndex = grid.getStore().indexOf(editField);
		        	editedField = editField;

//		        	customFieldNameCombo.setValue(new ModelPropertyWeb(editField.getFieldName(), editField.getFieldDescription()));
		    		customFieldNameText.disable();
		        	customFieldNameText.setValue(editField.getFieldName());
		        	customFieldNameDescriptionText.setValue(editField.getFieldDescription());
		        	attributeNameCombo.setValue(new ModelPropertyWeb(editField.getSourceFieldName(), editField.getSourceFieldNameDescription()));
		        	trafoFuncNameCombo.setValue(new ModelPropertyWeb(editField.getTransformationFunctionName(), editField.getTransformationFunctionNameDescription()));

		        	parameterStore.removeAll();
					for (String key : editField.getConfigurationParameters().keySet())  {
						String value = editField.getConfigurationParameters().get(key);
						ModelPropertyWeb  parameter = new ModelPropertyWeb(key, value);
						parameterStore.add(parameter);
					}
		        }
		    });
	    toolBar.add(editFieldButton);
	    final Button removeFieldButton =
			new Button("Remove Field", IconHelper.create("images/folder_delete.png"), new SelectionListener<ButtonEvent>() {
		        @Override
		        public void componentSelected(ButtonEvent ce) {
		        	CustomFieldWeb removeField = grid.getSelectionModel().getSelectedItem();
		        	if (removeField == null) {
		        		Info.display("Information","You must first select a field in the round to be deleted before pressing the \"Remove Round\" button.");
		        		return;
		        	}
					grid.getStore().remove(removeField);

		        }
		    });
	    toolBar.add(removeFieldButton);
		toolBar.add(new SeparatorToolItem());
		final Button moveUpFieldButton =
			new Button("Move Up", IconHelper.create("images/arrow_up.png"), new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					if (grid.getStore().getCount() > 1) {
						CustomFieldWeb field = grid.getSelectionModel().getSelectedItem();
						if (field == null) {
							Info.display("Information", "You must first select a field before pressing the \"Move Up\" button.");
							return;
						}
						grid.getSelectionModel().selectPrevious(false);
					}
				}
		    });
		toolBar.add(moveUpFieldButton);
		final Button moveDownFieldButton =
			new Button("Move Down", IconHelper.create("images/arrow_down.png"), new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					if (grid.getStore().getCount() > 1) {
						CustomFieldWeb field = grid.getSelectionModel().getSelectedItem();
						if (field == null) {
							Info.display("Information", "You must first select a field before pressing the \"Move Down\" button.");
							return;
						}
						grid.getSelectionModel().selectNext(false);
					}
				}
		    });


		toolBar.add(moveDownFieldButton);
		cp.setTopComponent(toolBar);

		grid.getSelectionModel().addListener(Events.SelectionChange,
			new Listener<SelectionChangedEvent<CustomFieldWeb>>() {
				public void handleEvent(SelectionChangedEvent<CustomFieldWeb> be) {
					CustomFieldWeb selectionItem = be.getSelectedItem();

					List<ModelPropertyWeb> allAttributeNames = Registry.get(Constants.PERSON_MODEL_ALL_ATTRIBUTE_NAMES);

					Boolean editFieldEnabled = true;
					Boolean removeFieldEnabled = true;
					Boolean moveUpEnabled = true;
					Boolean moveDownEnabled = true;

						if (selectionItem == null) {
							editFieldEnabled = false;
							removeFieldEnabled = false;
							moveUpEnabled = false;
							moveDownEnabled = false;
						} else {
							if (attributeNames != null) {
								editFieldEnabled = true;

			                    // check with allAttributeNames not allowed to remove already existing custom field
			                    boolean findInallAttributeNames = false;
			                    for (ModelPropertyWeb field : allAttributeNames) {
			                          if (field.getName().equals( selectionItem.getFieldName())) {
			                              findInallAttributeNames = true;
			                          }
			                    }
								if (findInallAttributeNames) {
								    removeFieldEnabled = false;
								}

							} else {
								editFieldEnabled = false;
								removeFieldEnabled = false;
							}
							int selectionIndex = grid.getStore().indexOf(selectionItem);
							moveUpEnabled = (selectionIndex > 0);
							moveDownEnabled = (selectionIndex < grid.getStore().getCount() - 1);
						}

					editFieldButton.setEnabled(editFieldEnabled);
					removeFieldButton.setEnabled(removeFieldEnabled);
					moveUpFieldButton.setEnabled(moveUpEnabled);
					moveDownFieldButton.setEnabled(moveDownEnabled);
				}
			});

		grid.addListener(Events.SortChange, new Listener<GridEvent>() {
			public void handleEvent(GridEvent be) {
				// Info.display("Information", "SortChange.");
				CustomFieldWeb selectField = grid.getSelectionModel().getSelectedItem();

				int selectionIndex = grid.getStore().indexOf(selectField);
				Boolean moveUpEnabled = (selectionIndex > 0);
				Boolean moveDownEnabled = (selectionIndex < grid.getStore().getCount() - 1);
				moveUpFieldButton.setEnabled(moveUpEnabled);
				moveDownFieldButton.setEnabled(moveDownEnabled);
			}
		});

		LayoutContainer c = new LayoutContainer();
		HBoxLayout layout = new HBoxLayout();
		layout.setPadding(new Padding(5));
		layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		layout.setPack(BoxLayoutPack.CENTER);
		c.setLayout(layout);

		HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));
		c.add(new Button("Save Settings", IconHelper.create("images/folder_go.png"), new SelectionListener<ButtonEvent>() {
	        @Override
	        public void componentSelected(ButtonEvent ce) {
	        	List<CustomFieldWeb> configuration = grid.getStore().getModels();
	        	// controller.handleEvent(new AppEvent(AppEvents.CustomFieldsConfigurationSave, configuration));
				AppEvent event = new AppEvent(AppEvents.CustomFieldsConfigurationSave, configuration);
			    event.setData("entityModel", currentEntity);
			    controller.handleEvent(event);
	        }
	    }), layoutData);
		cp.setBottomComponent(c);
		cp.add(grid);

		container.add(cp);

		LayoutContainer wrapper = Registry.get(Constants.CENTER_PANEL);
		wrapper.removeAll();
		wrapper.add(container);
		wrapper.layout();
		GWT.log("Done Initializing the UI in " + (new java.util.Date().getTime() - time), null);
	}

	private void buildAddCustomFieldDialog() {
		if (addEditCustomFieldDialog != null) {
			return;
		}

		addEditCustomFieldDialog = new Dialog();
		addEditCustomFieldDialog.setBodyBorder(false);
		addEditCustomFieldDialog.setIcon(IconHelper.create("images/folder_go.png"));
		addEditCustomFieldDialog.setHeading("Add/Edit Custom Field ");
		addEditCustomFieldDialog.setWidth(445);
		addEditCustomFieldDialog.setHeight(330);
		addEditCustomFieldDialog.setButtons(Dialog.OKCANCEL);
//		addEditCustomFieldDialog.setHideOnButtonClick(true);
		addEditCustomFieldDialog.setModal(true);
		addEditCustomFieldDialog.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {
	        @Override
	        public void componentSelected(ButtonEvent ce) {
	        	String customName = customFieldNameText.getValue();
	        	List<ModelPropertyWeb> attributeNameSelection = attributeNameCombo.getSelection();
	        	List<ModelPropertyWeb> trafoFuncNameSelection = trafoFuncNameCombo.getSelection();

				if (customFieldNameText.isValid() && !customName.isEmpty() && attributeNameSelection.size() > 0 && trafoFuncNameSelection.size() > 0)
				{
						ModelPropertyWeb attributeName = attributeNameSelection.get(0);
						ModelPropertyWeb trafoFuncName = trafoFuncNameSelection.get(0);

						CustomFieldWeb customFieldWeb = new CustomFieldWeb();
						if (currentEntity != null) {
							customFieldWeb.setEntityName(currentEntity.getName());
						}
						customFieldWeb.setFieldName(customName);
						customFieldWeb.setFieldDescription(Utility.convertToDescription(customName));
						customFieldWeb.setSourceFieldName(attributeName.getName());
						customFieldWeb.setSourceFieldNameDescription(attributeName.getDescription());
						customFieldWeb.setTransformationFunctionName(trafoFuncName.getName());
						customFieldWeb.setTransformationFunctionNameDescription(trafoFuncName.getDescription());

						// set parameter for Transformation Function
						java.util.HashMap<String,String> customFieldsWebMap = new java.util.HashMap<String, String>();
						for (ModelPropertyWeb parameter : parameterStore.getModels())  {
							// Info.display("function parameter: ", parameter.getName()+":"+parameter.getDescription());
							customFieldsWebMap.put(parameter.getName(), parameter.getDescription());
						}
						if (customFieldsWebMap.size() > 0) {
							customFieldWeb.setConfigurationParameters(customFieldsWebMap);
						}

						if (addOrEditFieldMode) { // Add

				        	// check duplicate custom Field
				        	for (CustomFieldWeb field : grid.getStore().getModels()) {
				        		  if( field.getFieldName().equals( customFieldWeb.getFieldName() ) ) {
					        	      MessageBox.alert("Information", "There is a duplicate custom field in Custom Field Configuration", null);
					        	      return;
				        		  }
				        	}

				        	// check with attributeNames
				        	for (ModelPropertyWeb field : attributeNames) {
				        		  if (field.getName().equals( customFieldWeb.getFieldName())) {
					        	      MessageBox.alert("Information", "There is a duplicate attribute name in Entity Model", null);
					        	      return;
				        		  }
				        	}

				        	// check duplicate source field and transformation function
/*				        	for (CustomFieldWeb field : grid.getStore().getModels()) {
				        		  if( field.getSourceFieldName().equals( customFieldWeb.getSourceFieldName() ) && 
				        			  field.getTransformationFunctionName().equals( customFieldWeb.getTransformationFunctionName() ) ) {
					        	      MessageBox.alert("Information", "There is a duplicate source field and transformation function in Custom Field Configuration", null);  		
					        	      return;
				        		  }
				        	}
*/
							grid.getStore().add(customFieldWeb);

						} else { // Edit

				        	// check duplicate custom Field
				        	for (CustomFieldWeb field : grid.getStore().getModels()) {
				        		if (field.getFieldName() != editedField.getFieldName() ) { // Not same record
					        		if (field.getFieldName().equals( customFieldWeb.getFieldName())) {
						        	    MessageBox.alert("Information", "There is a duplicate custom field in Custom Field Configuration", null);
						        	    return;
					        		}
				        		}
				        	}

				        	// check with attributeNames
				        	for (ModelPropertyWeb field : attributeNames) {
				        		  if (field.getName().equals( customFieldWeb.getFieldName())) {
					        	      MessageBox.alert("Information", "There is a duplicate attribute name in Entity Model", null);
					        	      return;
				        		  }
				        	}

				        	// check duplicate source field and transformation function
/*				        	for (CustomFieldWeb field : grid.getStore().getModels()) {
				        		if( field.getFieldName() != editedField.getFieldName() ) { // Not same record
					        		if( field.getSourceFieldName().equals( customFieldWeb.getSourceFieldName() ) &&
					        			field.getTransformationFunctionName().equals( customFieldWeb.getTransformationFunctionName() ) ) {
						        	    MessageBox.alert("Information", "There is a duplicate source field and transformation function in Custom Field Configuration", null);
						        	    return;
					        		}
				        		}
				        	}
*/
							grid.getStore().remove(editedField);
							grid.getStore().insert(customFieldWeb, editedFieldIndex);
						}

						addEditCustomFieldDialog.close();

				} else {
					if (customName.isEmpty()) {
						MessageBox.alert("Information", "Please type Custom Field Name", null);
						return;
					}

					if (!customFieldNameText.isValid()) {
		        	    MessageBox.alert("Information", "Invalid Custom Field Name", null);
						return;
					}

					if (attributeNameSelection.size() == 0) {
		        	    MessageBox.alert("Information", "Please select Source Field Name", null);
						return;
					}
					if (trafoFuncNameSelection.size() == 0) {
		        	    MessageBox.alert("Information", "Please select Transformation Function Name", null);
						return;
					}
				}
	        }
	    });

		addEditCustomFieldDialog.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {
	          @Override
	          public void componentSelected(ButtonEvent ce) {

	        	  addEditCustomFieldDialog.close();
	          }
	    });

		ContentPanel cp = new ContentPanel();
		cp.setHeading("Custom Field");
		cp.setFrame(true);
		cp.setIcon(IconHelper.create("images/folder.png"));
			FormLayout formLayout = new FormLayout();
			formLayout.setLabelWidth(180);
			formLayout.setDefaultWidth(220);
		cp.setLayout(formLayout);
		cp.setSize(435, 300);

		customFieldNameText = new TextField<String>();
		customFieldNameText.setFieldLabel("Custom Field Name");
		customFieldNameText.setRegex(InputFormat.ONE_WORD);
		customFieldNameText.getMessages().setRegexText("Should be one word");
		customFieldNameText.addListener(Events.KeyUp, new Listener<FieldEvent>() {
            public void handleEvent(FieldEvent fe) {
               	// int code = fe.getKeyCode();
               	// Info.display("Key Code:", ""+code);
            	TextField<String> t = (TextField<String>) fe.getField();
                String customFieldName = t.getValue();
                customFieldNameDescriptionText.setValue(Utility.convertToDescription(customFieldName));
            }
		});

		customFieldNameText.addListener(Events.Change, new Listener<FieldEvent>() {
            public void handleEvent(FieldEvent fe) {
            	TextField<String> t = (TextField<String>) fe.getField();
                String customFieldName = t.getValue();
                customFieldNameDescriptionText.setValue(Utility.convertToDescription(customFieldName));
            }
		});

		customFieldNameDescriptionText = new TextField<String>();
		customFieldNameDescriptionText.setFieldLabel("Custom Field Name Description");
		customFieldNameDescriptionText.setReadOnly(true);
		customFieldNameDescriptionText.disable();

		cp.add(customFieldNameText);
		cp.add(customFieldNameDescriptionText);

    	attributeNameCombo.setEmptyText("Select source field...");
    	attributeNameCombo.setForceSelection(true);
//    	attributeNameCombo.setDisplayField("name");
    	attributeNameCombo.setDisplayField("description");
    	attributeNameCombo.setStore(attributeNameStore);
    	attributeNameCombo.setTypeAhead(true);
    	attributeNameCombo.setTriggerAction(TriggerAction.ALL);

    	attributeNameCombo.setFieldLabel("Source Field Name");
    	cp.add(attributeNameCombo);

		trafoFuncNameCombo.setEmptyText("Select trafo function...");
		trafoFuncNameCombo.setForceSelection(true);
//		trafoFuncNameCombo.setDisplayField("name");
		trafoFuncNameCombo.setDisplayField("description");
		trafoFuncNameCombo.setStore(trafoFuncNameStore);
		trafoFuncNameCombo.setTypeAhead(true);
		trafoFuncNameCombo.setTriggerAction(TriggerAction.ALL);

		trafoFuncNameCombo.setFieldLabel("Transformation Function Name");

		// Select change
		trafoFuncNameCombo.addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<ModelPropertyWeb>>(){
			public void handleEvent(SelectionChangedEvent<ModelPropertyWeb> sce)
			{
    				ModelPropertyWeb item = sce.getSelectedItem();
    				// Info.display("function Name:", item.getName());

    				// Fill the parameter names to the combo
    	        	String functionNmae = item.getName();

		        	parameterNameCombo.clear();
		        	parameterNameStore.removeAll();

	        		parameterStore.removeAll();

		        	if (functionNmae.equals("Substring")) {
		        		functionParameterPanel.enable();

						for (String name: SubstringNames) {
							ModelPropertyWeb parameterName = new ModelPropertyWeb(name);
							parameterNameStore.add(parameterName);
						}

		        	} else if (functionNmae.equals("DateTransformationFunction")) {
		        		functionParameterPanel.enable();

						for (String name: DateTransformationNames) {
							ModelPropertyWeb parameterName = new ModelPropertyWeb(name);
							parameterNameStore.add(parameterName);
						}
		        	} else {
		        		functionParameterPanel.disable();
		        	}
			} });

		cp.add(trafoFuncNameCombo);

		functionParameterPanel = setupParameterPanel("");
		cp.add(functionParameterPanel);

		addEditCustomFieldDialog.add(cp);
	}

	//	Add/Edit/Delete View Parameter Panel
	private ContentPanel setupParameterPanel(String title) {
		ContentPanel cp = new ContentPanel();

		cp.setFrame(true);
		cp.setHeaderVisible(false);
		cp.setLayout(new FillLayout());
		cp.setSize(405, 150);

		ToolBar toolBar = new ToolBar();

		parameterNameCombo = new ComboBox<ModelPropertyWeb>();
		parameterNameCombo.setEmptyText("Select a field...");
		parameterNameCombo.setForceSelection(true);
		parameterNameCombo.setDisplayField("name");
		parameterNameCombo.setWidth(150);
		parameterNameCombo.setStore(parameterNameStore);
		parameterNameCombo.setTypeAhead(true);
		parameterNameCombo.setTriggerAction(TriggerAction.ALL);

		toolBar.add(parameterNameCombo);

		toolBar.add(new Button("Add Parameter", IconHelper.create("images/database_add.png"), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
	        	  List<ModelPropertyWeb> selection = parameterNameCombo.getSelection();
	        	  if (selection == null || selection.size() == 0) {
	        		  Info.display("Information", "Please select a field before pressing the \"Add Parameter\" button.");
	        		  return;
	        	  }

	        	  ModelPropertyWeb field = selection.get(0);

	        	  // check duplicate report parameter
	        	  if (!fieldInList(field, parametersGrid.getStore())) {
	        		  ModelPropertyWeb functionParameter = new ModelPropertyWeb();
	        		  functionParameter.setName(field.getName());
	        		  functionParameter.setDescription("");

	        		  parametersGrid.getStore().add(functionParameter);
	        	  } else {
		        	  Info.display("Add Function Parameter:", "Selected parameter is already added to the list");
	        	  }
			}

			private boolean fieldInList(ModelPropertyWeb field, ListStore<ModelPropertyWeb> listStore) {
				for (ModelPropertyWeb item : listStore.getModels()) {
					if (item.getName().equalsIgnoreCase(field.getName())) {
						return true;
					}
				}
				return false;
			}

	    }));
		toolBar.add(new SeparatorToolItem());
		toolBar.add(new Button("Remove Parameter", IconHelper.create("images/database_delete.png"), new SelectionListener<ButtonEvent>() {
	          @Override
	          public void componentSelected(ButtonEvent ce) {
	        	  ModelPropertyWeb field = parametersGrid.getSelectionModel().getSelectedItem();
	        	  if (field == null) {
	        		  Info.display("Information","You must first select a field before pressing the \"Remove Parameter\" button.");
	        		  return;
	        	  }
	        	  parametersGrid.getStore().remove(field);
	          }
	    }));
		cp.setTopComponent(toolBar);

		ColumnConfig pName = new ColumnConfig("name", "Parameter Name", 180);
		ColumnConfig pValue = new ColumnConfig("description", "Parameter Value", 200);
		TextField<String> parameterValueText = new TextField<String>();
		parameterValueText.setValidator(new Validator() {
				public String validate(Field<?> field, String value) {
					   return null;
				}
	    });
		pValue.setEditor(new CellEditor(parameterValueText));


		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		config.add(pName);
		config.add(pValue);
		ColumnModel cm = new ColumnModel(config);
	    RowEditor<ModelPropertyWeb> rowEditor = new RowEditor<ModelPropertyWeb>();
	    rowEditor.setClicksToEdit(ClicksToEdit.TWO);

		parametersGrid = new Grid<ModelPropertyWeb>(parameterStore, cm);
		parametersGrid.setStyleAttribute("borderTop", "none");
		parametersGrid.setBorders(true);
		parametersGrid.setStripeRows(true);
		parametersGrid.addPlugin(rowEditor);
		cp.add(parametersGrid);

		return cp;
	}
}
