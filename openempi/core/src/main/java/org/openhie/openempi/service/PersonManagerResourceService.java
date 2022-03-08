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

import org.openhie.openempi.ApplicationException;
import org.openhie.openempi.model.IdentifierDomain;
import org.openhie.openempi.model.IdentifierDomainAttribute;
import org.openhie.openempi.model.Person;
import org.openhie.openempi.model.PersonIdentifier;
import org.openhie.openempi.model.PersonLink;
import org.openhie.openempi.model.ReviewRecordPair;

public interface PersonManagerResourceService
{
    Person addPerson(Person person) throws ApplicationException;

    void updatePerson(Person person) throws ApplicationException;

    Person updatePersonById(Person person) throws ApplicationException;

    void deletePerson(PersonIdentifier personIdentifier) throws ApplicationException;
    
    void removePersonById(Integer personId) throws ApplicationException;
    
    void mergePersons(PersonIdentifier retiredIdentifier, PersonIdentifier survivingIdentier) throws ApplicationException;
    
    void deletePersonById(Person person) throws ApplicationException;
    
    Person importPerson(Person person) throws ApplicationException;
    
    IdentifierDomain addIdentifierDomain(IdentifierDomain identifierDomain) throws ApplicationException;

    IdentifierDomain updateIdentifierDomain(IdentifierDomain identifierDomain) throws ApplicationException;
    
    void deleteIdentifierDomain(IdentifierDomain identifierDomain) throws ApplicationException;
    
    IdentifierDomain obtainUniqueIdentifierDomain(String universalIdentifierTypeCode);
    
    IdentifierDomainAttribute addIdentifierDomainAttribute(IdentifierDomain identifierDomain,
                                                           String attributeName, String attributeValue);

    void updateIdentifierDomainAttribute(IdentifierDomainAttribute identifierDomainAttribute);
    
    void removeIdentifierDomainAttribute(IdentifierDomainAttribute identifierDomainAttribute);
    
    void addReviewRecordPair(ReviewRecordPair recordPair) throws ApplicationException;

    void matchReviewRecordPair(ReviewRecordPair recordPair) throws ApplicationException;

    void deleteReviewRecordPair(ReviewRecordPair recordPair);

    void deleteReviewRecordPairs();
    
    void linkPersons(PersonLink personLink) throws ApplicationException;

    void unlinkPersons(PersonLink personLink) throws ApplicationException;
}
