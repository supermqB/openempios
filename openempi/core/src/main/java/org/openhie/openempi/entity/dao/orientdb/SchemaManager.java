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
package org.openhie.openempi.entity.dao.orientdb;

import org.openhie.openempi.entity.Constants;
import org.openhie.openempi.model.Entity;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;

public interface SchemaManager
{
    InternalAttribute DATE_CHANGED_PROPERTY = new InternalAttribute(
            Constants.DATE_CHANGED_PROPERTY, OType.DATETIME, false);
    InternalAttribute DATE_CREATED_PROPERTY = new InternalAttribute(
            Constants.DATE_CREATED_PROPERTY, OType.DATETIME, false);
    InternalAttribute DATE_REVIEWED_PROPERTY = new InternalAttribute(
            Constants.DATE_REVIEWED_PROPERTY, OType.DATETIME, false);
    InternalAttribute DATE_VOIDED_PROPERTY = new InternalAttribute(Constants.DATE_VOIDED_PROPERTY,
            OType.DATETIME, false);
    InternalAttribute DIRTY_RECORD_PROPERTY = new InternalAttribute(Constants.DIRTY_RECORD_PROPERTY,
            OType.BOOLEAN, true);
    InternalAttribute ENTITY_VERSION_ID_PROPERTY = new InternalAttribute(
            Constants.ENTITY_VERSION_ID_PROPERTY, OType.LONG, false);
    InternalAttribute IDENTIFIER_PROPERTY = new InternalAttribute(Constants.IDENTIFIER_PROPERTY,
            OType.STRING, true);
//    static final InternalAttribute IDENTIFIER_SET_PROPERTY = new InternalAttribute(
//            Constants.IDENTIFIER_EDGE_TYPE, OType.LINKSET, false);
InternalAttribute IDENTIFIER_DOMAIN_ID_PROPERTY = new InternalAttribute(
            Constants.IDENTIFIER_DOMAIN_ID_PROPERTY, OType.INTEGER, false);
    InternalAttribute LINK_SOURCE_PROPERTY = new InternalAttribute(Constants.LINK_SOURCE_PROPERTY,
            OType.INTEGER, false);
    InternalAttribute LINK_STATE_PROPERTY = new InternalAttribute(Constants.LINK_STATE_PROPERTY,
            OType.STRING, true);
    InternalAttribute LINK_VECTOR_PROPERTY = new InternalAttribute(Constants.LINK_VECTOR_PROPERTY,
            OType.INTEGER, false);
    InternalAttribute LINK_WEIGHT_PROPERTY = new InternalAttribute(Constants.LINK_WEIGHT_PROPERTY,
            OType.DOUBLE, true);
    InternalAttribute USER_CHANGED_BY_PROPERTY = new InternalAttribute(
            Constants.USER_CHANGED_BY_PROPERTY, OType.LONG, false);
    InternalAttribute USER_CREATED_BY_PROPERTY = new InternalAttribute(
            Constants.USER_CREATED_BY_PROPERTY, OType.LONG, false);
    InternalAttribute USER_REVIEWED_BY_PROPERTY = new InternalAttribute(
            Constants.USER_REVIEWED_BY_PROPERTY, OType.LONG, false);
    InternalAttribute USER_VOIDED_BY_PROPERTY = new InternalAttribute(
            Constants.USER_VOIDED_BY_PROPERTY, OType.LONG, false);

    InternalAttribute[] INTERNAL_ATTRIBUTES = { DATE_CHANGED_PROPERTY, DATE_CREATED_PROPERTY,
            DATE_VOIDED_PROPERTY, DIRTY_RECORD_PROPERTY, ENTITY_VERSION_ID_PROPERTY, // IDENTIFIER_SET_PROPERTY,
            USER_CHANGED_BY_PROPERTY, USER_CREATED_BY_PROPERTY, USER_VOIDED_BY_PROPERTY, 
            };

    InternalAttribute[] IDENTIFIER_ATTRIBUTES = { IDENTIFIER_PROPERTY,
            IDENTIFIER_DOMAIN_ID_PROPERTY, USER_CREATED_BY_PROPERTY, USER_VOIDED_BY_PROPERTY,
            DATE_CREATED_PROPERTY, DATE_VOIDED_PROPERTY };

    InternalAttribute[] LINK_ATTRIBUTES = { DATE_CREATED_PROPERTY, DATE_REVIEWED_PROPERTY,
            LINK_STATE_PROPERTY, LINK_VECTOR_PROPERTY, LINK_WEIGHT_PROPERTY, LINK_SOURCE_PROPERTY,
            USER_CREATED_BY_PROPERTY, USER_REVIEWED_BY_PROPERTY };
    
    void createDatabase(EntityStore store, OrientBaseGraph db);
    
    void dropDatabase(EntityStore store, OrientBaseGraph db);
    
    void createIndexes(Entity entity, OrientBaseGraph db);
    
    ConnectionManager getConnectionManager();
    
    EntityStore getEntityStoreByName(String entityName);
    
    void createClass(OrientBaseGraph db, Entity entity, String baseClassName);

    void dropClass(OrientBaseGraph db, String className);
    
    boolean isClassDefined(OrientBaseGraph db, String className);
    
    Object getParameter(String key);

    void initializeSchema(Entity entity, EntityStore store);
    
    void dropSchema(Entity entity, EntityStore store);

    boolean isInternalAttribute(String fieldName);
    
    void removeIndexes(Entity entity, OrientBaseGraph db);
    
    void setParameter(String key, Object value);
    
    void shutdownStore(Entity entity);
}
