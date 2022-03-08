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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openhie.openempi.model.LinkSource;
import org.openhie.openempi.model.LoggedLink;
import org.openhie.openempi.model.Person;
import org.openhie.openempi.model.PersonLink;
import org.openhie.openempi.model.ReviewRecordPair;

public interface PersonLinkDao extends UniversalDao
{
	void addPersonLink(PersonLink personLink);
	
	void addPersonLinks(List<PersonLink> personLinks);

	List<PersonLink> getPersonLinks(Person person);
	
	Map<Long,Integer> getClusterIdByRecordIdMap(Integer sourceId);
	
	Integer getClusterId(Long[] recordIds, Integer sourceId);
	
	Integer getClusterId(Set<Long> recordIds, Integer sourceId);
	
	void convertReviewLinkToLink(ReviewRecordPair recordPair);
	
	List<PersonLink> getPersonLinksByLinkSource(Integer linkSourceId);
	
	List<PersonLink> getPersonLinks(Integer clusterId);
	
	List<PersonLink> getPersonLinks(Set<Long> recordIds);
	
	PersonLink getPersonLink(Person leftPerson, Person rightPerson);
	
	void removeLink(PersonLink personLink);
	
	int removeAllLinks();
	
	int removeLinksBySource(LinkSource linkSource);
	
	void addReviewRecordPair(ReviewRecordPair reviewRecordPair);
	
	void addReviewRecordPairs(List<ReviewRecordPair> reviewRecordPairs);
	
	List<ReviewRecordPair> getAllUnreviewedReviewRecordPairs();
	
	List<ReviewRecordPair> getUnreviewedReviewRecordPairs(int maxResults);
	
	ReviewRecordPair getReviewRecordPair(int reviewRecordPairId);
	
	ReviewRecordPair getReviewRecordPair(int leftPersonId, int rightPersonId);
	
	void updateReviewRecordPair(ReviewRecordPair reviewRecordPair);
	
	void removeReviewRecordPair(ReviewRecordPair reviewRecordPair);
	
	void removeAllReviewRecordPairs();
	
	int removeReviewLinksBySource(LinkSource linkSource);
	
	Integer getNextClusterId();
}
