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
package org.openempi.webapp.client.mvc;

import java.util.HashMap;

import org.openempi.webapp.client.AdminServiceAsync;
import org.openempi.webapp.client.BlockingDataServiceAsync;
import org.openempi.webapp.client.ConfigurationDataServiceAsync;
import org.openempi.webapp.client.Constants;
import org.openempi.webapp.client.EventNotificationServiceAsync;
import org.openempi.webapp.client.IdentifierDomainDataServiceAsync;
import org.openempi.webapp.client.JobQueueDataServiceAsync;
import org.openempi.webapp.client.ProfileDataServiceAsync;
import org.openempi.webapp.client.ReportDataServiceAsync;
import org.openempi.webapp.client.UserDataServiceAsync;
import org.openempi.webapp.client.ReferenceDataServiceAsync;
import org.openempi.webapp.client.AuditEventDataServiceAsync;
import org.openempi.webapp.client.EntityDefinitionDataServiceAsync;
import org.openempi.webapp.client.EntityInstanceDataServiceAsync;
import org.openempi.webapp.client.UserFileDataServiceAsync;

import com.extjs.gxt.ui.client.Registry;

public abstract class Controller extends com.extjs.gxt.ui.client.mvc.Controller
{
    private AdminServiceAsync adminService;
    private ConfigurationDataServiceAsync configurationDataService;
    private EventNotificationServiceAsync eventNotificationService;
    private IdentifierDomainDataServiceAsync identifierDomainDataService;
    private UserFileDataServiceAsync userFileDataService;
    private ReportDataServiceAsync reportDataService;
    private ProfileDataServiceAsync profileDataService;
    private UserDataServiceAsync userDataService;
    private BlockingDataServiceAsync blockingService;
    private ReferenceDataServiceAsync referenceService;
    private AuditEventDataServiceAsync auditEventDataService;
    private EntityDefinitionDataServiceAsync entityDefinitionDataService;
    private EntityInstanceDataServiceAsync entityInstanceDataService;
    private JobQueueDataServiceAsync jobQueueDataService;
    
    private final HashMap<String, Object> cacheMap;

    public Controller() {
        cacheMap = new HashMap<String, Object>();
    }

    public Object getFromCache(String key) {
        if (cacheMap.containsKey(key)) {
            return cacheMap.get(key);
        }
        return null;
    }

    public void setToCache(String key, Object obj) {
        cacheMap.put(key, obj);
    }

    public void cleanRegistry() {
        Registry.register(Constants.LOGIN_USER, null);

        Registry.register(Constants.BASIC_SEARCH_CRITERIA, null);
        Registry.register(Constants.BASIC_SEARCH_LIST, null);
        Registry.register(Constants.ADVANCED_SEARCH_CRITERIA, null);
        Registry.register(Constants.ADVANCED_SEARCH_LIST, null);
        Registry.register(Constants.ADVANCED_SEARCH_FUZZY_MATCH, null);
        Registry.register(Constants.MAX_RECORD_DISPLAING, null);
        Registry.register(Constants.ENTITY_SEARCH_ADVANCED, null);
        Registry.register(Constants.ENTITY_SEARCH_BASIC, null);
        Registry.register(Constants.ENTITY_ATTRIBUTE_MODEL, null);
    }

    protected synchronized IdentifierDomainDataServiceAsync getIdentifierDomainDataService() {
        if (identifierDomainDataService == null) {
            identifierDomainDataService = Registry
                    .get(Constants.IDENTIFIER_DOMAIN_DATA_SERVICE);
        }
        return identifierDomainDataService;
    }

    protected synchronized UserFileDataServiceAsync getUserFileDataService() {
        if (userFileDataService == null) {
            userFileDataService = Registry.get(Constants.USER_FILE_DATA_SERVICE);
        }
        return userFileDataService;
    }

    protected synchronized ReportDataServiceAsync getReportDataService() {
        if (reportDataService == null) {
            reportDataService = Registry.get(Constants.REPORT_DATA_SERVICE);
        }
        return reportDataService;
    }

    public synchronized AuditEventDataServiceAsync getAuditEventDataService() {
        if (auditEventDataService == null) {
            auditEventDataService = Registry.get(Constants.AUDIT_EVENT_SERVICE);
        }
        return auditEventDataService;
    }

    protected synchronized ProfileDataServiceAsync getProfileDataService() {
        if (profileDataService == null) {
            profileDataService = Registry.get(Constants.PROFILE_DATA_SERVICE);
        }
        return profileDataService;
    }

    protected synchronized UserDataServiceAsync getUserDataService() {
        if (userDataService == null) {
            userDataService = Registry.get(Constants.USER_DATA_SERVICE);
        }
        return userDataService;
    }

    protected synchronized AdminServiceAsync getAdminService() {
        if (adminService == null) {
            adminService = Registry.get(Constants.ADMIN_SERVICE);
        }
        return adminService;
    }

    protected synchronized EventNotificationServiceAsync getEventNotificationService() {
        if (eventNotificationService == null) {
            eventNotificationService = Registry
                    .get(Constants.EVENT_NOTIFICATION_SERVICE);
        }
        return eventNotificationService;
    }

    protected synchronized ConfigurationDataServiceAsync getConfigurationDataService() {
        if (configurationDataService == null) {
            configurationDataService = Registry
                    .get(Constants.CONFIGURATION_DATA_SERVICE);
        }
        return configurationDataService;
    }

    protected synchronized BlockingDataServiceAsync getBlockingDataService() {
        if (blockingService == null) {
            blockingService = Registry.get(Constants.BLOCKING_DATA_SERVICE);
        }
        return blockingService;
    }

    protected synchronized ReferenceDataServiceAsync getReferenceDataService() {
        if (referenceService == null) {
            referenceService = Registry.get(Constants.REF_DATA_SERVICE);
        }
        return referenceService;
    }

    public synchronized EntityDefinitionDataServiceAsync getEntityDefinitionDataService() {
        if (entityDefinitionDataService == null) {
            entityDefinitionDataService = Registry
                    .get(Constants.ENTITY_DEFINITION_DATA_SERVICE);
        }
        return entityDefinitionDataService;
    }

    public synchronized EntityInstanceDataServiceAsync getEntityInstanceDataService() {
        if (entityInstanceDataService == null) {
            entityInstanceDataService = Registry
                    .get(Constants.ENTITY_INSTANCE_DATA_SERVICE);
        }
        return entityInstanceDataService;
    }
    
    public synchronized JobQueueDataServiceAsync getJobQueueDataService() {
        if (jobQueueDataService == null) {
            jobQueueDataService = Registry
                    .get(Constants.JOB_QUEUE_DATA_SERVICE);
        }
        return jobQueueDataService;
    }
}
