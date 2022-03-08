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
package org.openhie.openempi.entity.dao;

import java.util.List;

import org.openhie.openempi.DaoException;
import org.openhie.openempi.model.Entity;
import org.openhie.openempi.model.EntityAttribute;
import org.openhie.openempi.model.EntityAttributeDatatype;
import org.openhie.openempi.model.EntityAttributeGroup;
import org.openhie.openempi.model.EntityAttributeValidation;

public interface EntityDefinitionDao
{
	Entity addEntity(Entity entity) throws DaoException;
	
	Entity updateEntity(Entity entity) throws DaoException;

	Entity loadEntity(Integer id);
	
	List<Entity> findEntitiesByName(String name);
	
	List<Entity> loadEntities();
	
	List<Entity> findEntityVersions(Integer entityId);

	List<EntityAttributeDatatype> getEntityAttributeDatatypes();
	
	EntityAttributeGroup addEntityAttributeGroup(EntityAttributeGroup entityAttributeGroup) throws DaoException;
	
	EntityAttributeGroup updateEntityAttributeGroup(EntityAttributeGroup entityAttributeGroup) throws DaoException;
	
	EntityAttributeGroup loadEntityAttributeGroup(Integer id);
	
	List<EntityAttributeGroup> loadEntityAttributeGroups(Entity entity);
	
	void deleteEntityAttributeGroup(EntityAttributeGroup entityAttributeGroup) throws DaoException;
	
	EntityAttributeValidation addEntityAttributeValidation(EntityAttributeValidation validation) throws DaoException;
	
	EntityAttributeValidation updateEntityAttributeValidation(EntityAttributeValidation validation) throws DaoException;
	
	void deleteEntityAttributeValidation(EntityAttributeValidation validation) throws DaoException;
	
	EntityAttributeValidation loadEntityAttributeValidation(Integer id);
	
	List<EntityAttributeValidation> loadEntityAttributeValidations(EntityAttribute entityAttribute);

	List<EntityAttribute> loadCustomFields(Entity entity);

	EntityAttribute addCustomField(Entity entity, EntityAttribute field) throws DaoException;
	
	EntityAttribute findCustomField(Entity entity, String fieldName);
}
