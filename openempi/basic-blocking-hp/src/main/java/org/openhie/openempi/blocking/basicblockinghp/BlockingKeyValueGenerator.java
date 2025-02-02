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
package org.openhie.openempi.blocking.basicblockinghp;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.openhie.openempi.configuration.BaseField;
import org.openhie.openempi.model.Record;

public class BlockingKeyValueGenerator
{
	private static final Logger log = Logger.getLogger(BlockingKeyValueGenerator.class);
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
	
	public static String generateBlockingKeyValue(List<BaseField> fields, Record record) {
		Object[] attributes = new Object[fields.size()];
		int index=0;
		for (BaseField field : fields) {
			if (record.get(field.getFieldName()) != null) {
				attributes[index] = harmonizeValue(record, field.getFieldName());
			} else {
				attributes[index] = "<null>";
			}
			index++;
		}
		String blockingKeyValue = generateBlockingKeyValue(attributes);
		if (log.isTraceEnabled()) {
			log.trace("Generated BKV: " + blockingKeyValue + " using fields: " + fields + " and record: " + record);
		}
		return blockingKeyValue;
	}
	
	private static Object harmonizeValue(Record record, String fieldName) {
	    Object objValue = record.get(fieldName);
	    String strValue = null;
	    if (objValue instanceof java.util.Date) {
	        strValue = dateFormat.format(objValue);
	    } else {
	        strValue = objValue.toString();
	    }
	    String value = strValue
	            .replace('\'', '#')
	            .replace('%','#');
        return value;
    }

    public static String generateBlockingKeyValue(Object[] fields) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i < fields.length; i++) {
			sb.append(fields[i].toString());
			if (i < fields.length-1) {
				sb.append(".");
			}
		}
		return sb.toString();
	}
}
