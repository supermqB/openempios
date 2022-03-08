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

import org.openhie.openempi.model.Gender;
import org.openhie.openempi.model.IdentifierDomain;
import org.openhie.openempi.model.IdentifierDomainAttribute;
import org.openhie.openempi.model.Person;
import org.openhie.openempi.model.PersonIdentifier;
import org.openhie.openempi.model.PersonLink;
import org.openhie.openempi.model.Race;
import org.openhie.openempi.model.ReviewRecordPair;

public interface PersonQueryResourceService
{
    Gender findGenderByCode(String genderCode);

    Gender findGenderByName(String genderName);
    
    Race findRaceByCode(String raceCode);
    
    Race findRaceByName(String raceName);
    
    List<String> getIdentifierDomainTypeCodes();
 
    IdentifierDomain findIdentifierDomain(IdentifierDomain identifierDomain);

    List<IdentifierDomain> getIdentifierDomains();
    
    List<IdentifierDomainAttribute> getIdentifierDomainAttributes(IdentifierDomain identifierDomain);
    
    IdentifierDomainAttribute getIdentifierDomainAttribute(IdentifierDomain domain, String attributeName);

    List<String> getPersonModelAllAttributeNames();
    
    List<String> getPersonModelAttributeNames();
    
    List<String> getPersonModelCustomAttributeNames();
    
    Person findPersonById(PersonIdentifier identifier);

    PersonIdentifier getGlobalIdentifierById(PersonIdentifier identifier);

    Person getSingleBestRecord(Integer personId);
    
    List<Person> getSingleBestRecords(List<Integer> personIds);
    
    List<Person> findLinkedPersons(PersonIdentifier identifier);

    List<PersonLink> getPersonLinks(Person person);
    
    List<Person> findPersonsByAttributes(Person person);
    
    List<Person> findPersonsByAttributesPaged(Person person, int first, int last);
    
    List<Person> findMatchingPersonsByAttributes(Person person);
    
    Person loadPerson(Integer personId);
    
    List<Person> loadAllPersonsPaged(Integer firstRecord, Integer maxRecords);
    
    List<Person> loadPersons(List<Integer> personIds);
    
    List<ReviewRecordPair> loadAllUnreviewedPersonLinks();
    
    List<ReviewRecordPair> loadUnreviewedPersonLinks(Integer maxRecords);
    
    ReviewRecordPair loadReviewRecordPair(Integer personLinkReviewId);
}
