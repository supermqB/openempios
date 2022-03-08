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
import java.util.Set;

import org.openhie.openempi.model.Gender;
import org.openhie.openempi.model.IdentifierDomain;
import org.openhie.openempi.model.IdentifierDomainAttribute;
import org.openhie.openempi.model.Nationality;
import org.openhie.openempi.model.Person;
import org.openhie.openempi.model.PersonIdentifier;
import org.openhie.openempi.model.Race;
import org.openhie.openempi.model.Record;

public interface PersonDao extends UniversalDao
{
	void addPerson(Person person);

	void saveIdentifierDomain(IdentifierDomain identifierDomain);
		
	void updatePerson(Person person);
	
	void addPersonIdentifier(PersonIdentifier identifier);

	void addPersonIdentifiers(Set<PersonIdentifier> identifiers);
	
	void updatePersons(List<Person> persons);
	
	List<Integer> getAllPersons();
	
	Person getPersonById(PersonIdentifier personIdentifier);
	
	List<Person> getPersonsByIdentifier(final PersonIdentifier personIdentifier);
	
	List<Person> getPersons(final org.openhie.openempi.model.Criteria criteria);
	
	int getRecordCount();
	
	List<Person> getPersonsPaged(final int firstResult, final int maxResults);
	
	List<Record> getRecordsPaged(final int firstResult, final int maxResults);
	
	List<Record> getRecordsPaged(final org.openhie.openempi.model.Criteria criteria, final int firstResult, final int maxResults);
	
	List<Person> getPersonsPaged(final org.openhie.openempi.model.Criteria criteria, final int firstResult, final int maxResults);
			
	List<Person> getPersons(final org.openhie.openempi.model.Criteria personCriteria, final Set<PersonIdentifier> personIdentifier);
	
	List<Person> getPersonsPaged(final org.openhie.openempi.model.Criteria personCriteria,
                                 final Set<PersonIdentifier> personIdentifiers, final int firstResult, final int maxResults,
                                 final boolean hydrate);
	
	Person loadPerson(Integer personId);
	
	Person loadPersonComplete(Integer personId);
	
	List<Person> loadPersons(List<Integer> personIds);
	
	Record loadRecord(Long recordId);
	
	List<Record> loadRecords(List<Long> recordIs);
	
	List<IdentifierDomain> getIdentifierDomains();
	
	List<String> getIdentifierDomainTypeCodes();
	
	IdentifierDomain findIdentifierDomain(final IdentifierDomain identifierDomain);
	
	IdentifierDomain findIdentifierDomainByName(final String identifierDomainName);
	
	void addIdentifierDomain(IdentifierDomain identifierDomain);
	
	void removeIdentifierDomain(IdentifierDomain identifierDomain);
	
	void removePerson(Integer personId);
	
	List<Integer> getPersonsWithoutIdentifierInDomain(IdentifierDomain identifierDomain, boolean haveLinks);
	
	boolean isKnownUniversalIdentifierTypeCode(String universalIdentifierTypeCode);
	
	IdentifierDomainAttribute addIdentifierDomainAttribute(IdentifierDomain identifierDomain, String attributeName, String attributeValue);
	
	IdentifierDomainAttribute getIdentifierDomainAttribute(IdentifierDomain identifierDomain, String attributeName);
	
	List<IdentifierDomainAttribute> getIdentifierDomainAttributes(IdentifierDomain identifierDomain);
	
	void updateIdentifierDomainAttribute(IdentifierDomainAttribute identifierDomainAttribute);
	
	void removeIdentifierDomainAttribute(IdentifierDomainAttribute identifierDomainAttribute);
	
	void clearCustomFields();
	
	Race findRaceByName(String raceName);
	
	Race findRaceByCode(String raceCode);
	
	Gender findGenderByName(String genderName);

	Gender findGenderByCode(String genderCode);
    
    Nationality findNationalityByName(String genderName);

    Nationality findNationalityByCode(String genderCode);
}
