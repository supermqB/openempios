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

public interface MatchingService
{
	/**
	 * The initializeRepository method first removes all the record associations that have been
	 * established in the database between records that belong to the same physical entity and
	 * performs matching of all the records from the beginning. This operation may be very
	 * time consuming and may be destructive in nature. It should only be performed when an
	 * instance of OpenEMPI is first created or when a different matching algorithm is used
	 * or the matching parameters of the matching algorithm are modified.
	 *   
	 * @throws ApplicationException
	 */
    void initializeRepository(String entityName) throws ApplicationException;

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
	
	/**
	 * This match method takes a record pair as a parameter and returns it back after
	 * deciding whether the two records in the pair should be linked to one another or not.
	 * 
	 * @param recordPair
	 * @return the record pair with an adjusted match outcome and match weight.
	 */
    RecordPair match(RecordPair recordPair) throws ApplicationException;
	
	/**
	 * This method returns the set of fields from a record that are used by the matching
	 * algorithm in some way to arrive at the classification decision. The method exists
	 * to allow other services to make informed decisions such as which fields to cache
	 * that may be allow the matching algorithm to operate more efficiently.
	 * 
	 * @param The name of the entity for which the matching fields are needed.
	 * @return returns the set of field names
	 */
    Set<String> getMatchFields(String entityName);
}
