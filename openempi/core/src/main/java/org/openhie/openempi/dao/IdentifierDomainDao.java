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
package org.openhie.openempi.dao;

import java.util.Date;
import java.util.List;

import org.openhie.openempi.model.IdentifierDomain;
import org.openhie.openempi.model.IdentifierDomainAttribute;
import org.openhie.openempi.model.IdentifierUpdateEvent;
import org.openhie.openempi.model.User;

public interface IdentifierDomainDao extends UniversalDao
{

	List<IdentifierDomain> getIdentifierDomains();

	List<String> getIdentifierDomainTypeCodes();

	IdentifierDomain findIdentifierDomain(final IdentifierDomain identifierDomain);

	IdentifierDomain findIdentifierDomainByName(final String identifierDomainName);

	IdentifierDomain findIdentifierDomainById(final Integer id);

	void addIdentifierDomain(IdentifierDomain identifierDomain);

	void removeIdentifierDomain(IdentifierDomain identifierDomain);

	void saveIdentifierDomain(IdentifierDomain identifierDomain);

	boolean isKnownUniversalIdentifierTypeCode(String universalIdentifierTypeCode);

	IdentifierDomainAttribute addIdentifierDomainAttribute(IdentifierDomain identifierDomain, String attributeName, String attributeValue);

	IdentifierDomainAttribute getIdentifierDomainAttribute(IdentifierDomain identifierDomain, String attributeName);

	List<IdentifierDomainAttribute> getIdentifierDomainAttributes(IdentifierDomain identifierDomain);

	void updateIdentifierDomainAttribute(IdentifierDomainAttribute identifierDomainAttribute);

	void removeIdentifierDomainAttribute(IdentifierDomainAttribute identifierDomainAttribute);

    int getIdentifierUpdateEventCount(User eventRecipient);

    IdentifierUpdateEvent findIdentifierUpdateEvent(long identifierUpdateEventId);

    IdentifierUpdateEvent addIdentifierUpdateEvent(IdentifierUpdateEvent identifierUpdateEvent);

    void removeIdentifierUpdateEvent(IdentifierUpdateEvent identifierUpdateEvent);

    List<IdentifierUpdateEvent> getIdentifierUpdateEvents(int startIndex, int maxEvents, User eventRecipient);

    List<IdentifierUpdateEvent> getIdentifierUpdateEvents(User eventRecipient);

    List<IdentifierUpdateEvent> getIdentifierUpdateEventsByDate(final Date startDate, final User eventRecipient);

    List<IdentifierUpdateEvent> getIdentifierUpdateEventsBeforeDate(final Date startDate, final User eventRecipient);
}
