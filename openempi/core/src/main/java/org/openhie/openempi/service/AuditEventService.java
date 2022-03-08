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
package org.openhie.openempi.service;

import java.util.Date;
import java.util.List;

import org.openhie.openempi.model.AuditEvent;
import org.openhie.openempi.model.AuditEventEntry;
import org.openhie.openempi.model.AuditEventType;
import org.openhie.openempi.model.LoggedLink;
import org.openhie.openempi.model.MessageLogEntry;
import org.openhie.openempi.model.MessageType;
import org.openhie.openempi.model.Record;
import org.openhie.openempi.model.Person;

public interface AuditEventService
{
	AuditEventType getAuditEventTypeByCode(String eventTypeCode);
	
	AuditEvent saveAuditEvent(AuditEvent auditEvent);
	
	AuditEvent saveAuditEvent(String auditEventType, String auditEventDescription);

	AuditEvent saveAuditEvent(String auditEventType, String auditEventDescription, Person refPerson);
	
	AuditEvent saveAuditEvent(String auditEventType, String auditEventDescription, Person refPerson, Person altRefPerson);
	
	List<AuditEvent> getAllAuditEvents();
	
	List<AuditEventType> getAllAuditEventTypes();
	
	int getAuditEventCount(Date startDate, Date endDate, List<Integer> auditEventTypeCodes);
	
	List<AuditEvent> filterAuditEvents(Date startDate, Date endDate, List<Integer> auditEventTypeCodes);
	
	List<AuditEvent> filterAuditEventsPaged(Date startDate, Date endDate, List<Integer> auditEventTypeCodes, int firstResult, int maxResults);

	
	// Audit event entry		
    AuditEventEntry saveAuditEventEntry(AuditEventEntry auditEventEntry);
	
	AuditEventEntry saveAuditEventEntry(String auditEventType, String auditEventDescription, String entityName);

	AuditEventEntry saveAuditEventEntry(String auditEventType, String auditEventDescription, String entityName, Record refRecord);
	
	AuditEventEntry saveAuditEventEntry(String auditEventType, String auditEventDescription, String entityName, Record refRecord, Record altRefRecord);
	
	List<AuditEventEntry> getAllAuditEventEntries();
		
	int getAuditEventEntryCount(Date startDate, Date endDate, List<Integer> auditEventTypeCodes);
	
	List<AuditEventEntry> filterAuditEventEntries(Date startDate, Date endDate, List<Integer> auditEventTypeCodes);
	
	List<AuditEventEntry> filterAuditEventEntriesPaged(Date startDate, Date endDate, List<Integer> auditEventTypeCodes, int firstResult, int maxResults);
	
	List<MessageType> getMessageTypes();
	
	MessageType getMessageTypeByCode(String messageTypeCode);

	MessageLogEntry getMessageLogEntry(Integer messageLogId);

	int getMessageLogEntryCount(Date startDate, Date endDate, List<Integer> messageTypeCodes);

	MessageLogEntry saveMessageLogEntry(MessageLogEntry messageLogEntry);
	
	List<MessageLogEntry> filterMessageLogEntries(Date startDate, Date endDate, List<Integer> messageType, int firstResult, int maxResults);
	
	// Link logs
    void clearLoggedLinks(int entityVersionId);
	
    int getLoggedLinksCount(int entityVersionId, int vectorValue);
    
    List<LoggedLink> getLoggedLinks(final int entityVersionId, final int vectorValue, final int start, final int maxResults);

}
