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
package org.openempi.webapp.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openhie.openempi.ApplicationException;
import org.openhie.openempi.context.Context;
import org.openhie.openempi.model.IdentifierDomain;
import org.openhie.openempi.model.Nationality;
import org.openhie.openempi.model.Person;
import org.openhie.openempi.model.PersonIdentifier;
import org.openhie.openempi.service.BaseManagerTestCase;

public abstract class BaseServiceTestCase extends BaseManagerTestCase
{	
	@Override
	protected void onSetUp() throws Exception {
		log.debug("Application context is: " + getApplicationContext());
		super.onSetUp();
		Context.startup();
		Context.authenticate("admin", "admin");
	}

	@Override
	protected void onTearDown() throws Exception {
		System.out.println("Before tearDown Application context is: " + getApplicationContext());
		super.onTearDown();
		System.out.println("After tearDown Application context is: " + getApplicationContext());
		Context.shutdown();
	}
	
	protected void deletePerson(Person person) throws ApplicationException {
		List<Person> persons = Context.getPersonQueryService().findPersonsByAttributes(person);
		Map<PersonIdentifier,PersonIdentifier> idsDeleted=new HashMap<PersonIdentifier,PersonIdentifier>();
		for (Person personFound : persons) {
			PersonIdentifier id = personFound.getPersonIdentifiers().iterator().next();
			if (id.getDateVoided() == null && idsDeleted.get(id) == null) {
				Context.getPersonManagerService().deletePerson(id);
				for (PersonIdentifier idDeleted : personFound.getPersonIdentifiers()) {
					idsDeleted.put(idDeleted, idDeleted);
				}
			}
		}
	}

	protected static PersonIdentifier buildTestPersonIdentifier(String ssn) {
		PersonIdentifier pi = new PersonIdentifier();
		pi.setIdentifier(ssn);
		IdentifierDomain id = new IdentifierDomain();
		id.setNamespaceIdentifier("SSN");
		pi.setIdentifierDomain(id);
		return pi;
	}

	protected static Person buildTestPerson(String ssn) {
		Person person = new Person();
		person.setGivenName("Master");
		person.setMiddleName("Patient");
		person.setFamilyName("Index");
		person.setAddress1("1000 Openempi Drive");
		person.setCity("SYSNET");
		person.setState("Virginia");
		person.setProvince("Northeast");
		person.setDistrict("AA");
		person.setVillage("BB");
		person.setSsn(ssn);
	
		PersonIdentifier pi = new PersonIdentifier();
		pi.setIdentifier(ssn);
		IdentifierDomain id = new IdentifierDomain();
		id.setIdentifierDomainName("TEMP"); // Not Null
		id.setNamespaceIdentifier("IHENA");		
		pi.setIdentifierDomain(id);
		pi.setPerson(person);
		person.addPersonIdentifier(pi);
		
		Nationality nation = new Nationality();
		nation.setNationalityCode("USA");
		person.setNationality(nation);
		return person;
	}
}
