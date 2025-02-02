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
package org.openhie.openempi.entity.dao.orientdb;

import com.orientechnologies.orient.core.metadata.schema.OType;

public class InternalAttribute
{
    private final String name;
    private final OType type;
    private final boolean indexed;

    public InternalAttribute(String name, OType type, boolean indexed) {
        this.name = name;
        this.type = type;
        this.indexed = indexed;
    }

    public String getName() {
        return name;
    }

    public OType getType() {
        return type;
    }

    public boolean getIndexed() {
        return indexed;
    }

    public String toString() {
        return "InternalAttribute [name=" + name + ", type=" + type + ", indexed=" + indexed + "]";
    }
}
