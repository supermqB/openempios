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
package org.openhie.openempi.entity;

import java.util.List;

import org.openhie.openempi.ApplicationException;
import org.openhie.openempi.configuration.CustomField;
import org.openhie.openempi.model.Entity;
import org.openhie.openempi.model.EntityAttribute;
import org.openhie.openempi.model.EntityAttributeDatatype;
import org.openhie.openempi.model.EntityAttributeGroup;
import org.openhie.openempi.model.EntityAttributeValidation;

public interface EntityDefinitionManagerService
{
	List<Entity> loadEntities();
	
	Entity loadEntity(Integer id);
	
    Entity loadEntityByName(String name);

    Entity getEntityByName(String name);
    
    void createEntityIndexes(Integer entityVersionId) throws ApplicationException;

    void dropEntityIndexes(Integer entityVersionId) throws ApplicationException;

    List<Entity> findEntitiesByName(String name);
	
	List<EntityAttributeDatatype> getEntityAttributeDatatypes();
	
	Entity addEntity(Entity entity) throws ApplicationException;
	
	Entity updateEntity(Entity entity) throws ApplicationException;
	
	void deleteEntity(Entity entity) throws ApplicationException;

	String exportEntity(Entity entity, String filename) throws ApplicationException;
	
	String exportEntity(Integer entityVersionId) throws ApplicationException;
	
	void importEntity(String filename) throws ApplicationException;

	EntityAttributeGroup addEntityAttributeGroup(EntityAttributeGroup entityAttributeGroup) throws ApplicationException;
	
	EntityAttributeGroup updateEntityAttributeGroup(EntityAttributeGroup entityAttributeGroup) throws ApplicationException;
	
	void updateEntityAttributeGroups(List<EntityAttributeGroup> groups, Entity updatedEntity) throws ApplicationException;
	
	void deleteEntityAttributeGroup(EntityAttributeGroup entityAttributeGroup) throws ApplicationException;
	
	EntityAttributeGroup loadEntityAttributeGroup(Integer id);
	
	List<EntityAttributeGroup> loadEntityAttributeGroups(Entity entity);
	
	EntityAttributeValidation addEntityAttributeValidation(EntityAttributeValidation validation) throws ApplicationException;
	
	EntityAttributeValidation updateEntityAttributeValidation(EntityAttributeValidation validation) throws ApplicationException;
	
	void updateEntityAttributeValidations(List<EntityAttributeValidation> validations, Entity updatedEntity, String attributeName) throws ApplicationException;
	
	void deleteEntityAttributeValidation(EntityAttributeValidation validation) throws ApplicationException;
	
	EntityAttributeValidation loadEntityAttributeValidation(Integer id);
	
	List<EntityAttributeValidation> loadEntityAttributeValidations(EntityAttribute entityAttribute);
	
	List<CustomField> loadCustomFields(String entityName);
	
	CustomField findCustomField(String entityName, String fieldName);
	
	CustomField addCustomField(CustomField field) throws ApplicationException;

	void updateCustomField(CustomField field) throws ApplicationException;
	
	void deleteCustomField(CustomField field) throws ApplicationException;
}
