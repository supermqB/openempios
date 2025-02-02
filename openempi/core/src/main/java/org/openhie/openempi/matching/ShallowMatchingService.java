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
package org.openhie.openempi.matching;

import java.util.Set;

import org.openhie.openempi.ApplicationException;
import org.openhie.openempi.model.Record;
import org.openhie.openempi.model.RecordPair;

public interface ShallowMatchingService
{
    /**
     * The getMatchingServiceId method returns a unique identifier for the matching algorithm.
     * The purpose of this identifier is to allow a site to identify which matching algorithm
     * was responsible for each of the links between records in the repository. 
     * 
     * @return The unique identifier of the particular Matching Algorithm
     */
    int getMatchingServiceId();

    /**
     * This match method takes a record as a parameter and returns all the records that the
     * given record is linked to by returning them in the form of record pairs. The first record
     * in each record pair returned is the record passed into the call.
     * 
     * @param record
     * @return
     */
    Set<RecordPair> match(Record record) throws ApplicationException;
}
