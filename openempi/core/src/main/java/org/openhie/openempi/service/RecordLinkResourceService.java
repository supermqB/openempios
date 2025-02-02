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

import java.util.List;

import org.openhie.openempi.BadRequestException;
import org.openhie.openempi.ConflictException;
import org.openhie.openempi.NotFoundException;
import org.openhie.openempi.model.RecordLink;

public interface RecordLinkResourceService
{
    List<RecordLink> getRecordLinks(String versionId, Integer entityId, String linkState, Integer firstResult,
                                    Integer maxResults) throws BadRequestException, NotFoundException;

    RecordLink loadByRecordLinkId(String versionId, Integer entityId, String recordLinkId)
            throws BadRequestException, NotFoundException;

    List<RecordLink> loadByRecordId(String versionId, Integer entityId, Long recordId)
            throws BadRequestException, NotFoundException;

    RecordLink addRecordLink(String versionId, Integer entityId, RecordLink recordLink)
            throws ConflictException, BadRequestException;

    RecordLink updateRecordLink(String versionId, Integer entityId, RecordLink recordLink)
            throws BadRequestException, ConflictException, NotFoundException;

    void removeRecordLink(String versionId, Integer entityId, Long recordLinkId)
            throws BadRequestException, ConflictException, NotFoundException;
}
