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
package org.openhie.openempi.loader;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class DsgenTrainingDataGenerator implements TrainingDataExtractor
{
	private final Logger log = Logger.getLogger(getClass());

	public Serializable extractKey(String line) {
		String[] fields = line.split(",");
		String id = fields[0].trim();
		int firstIndex = id.indexOf('-');
		int secIndex = id.indexOf('-', firstIndex+1);
		String key = id.substring(0, secIndex);
		log.debug("Extracted key of " + key);
		return key;
	}
}
