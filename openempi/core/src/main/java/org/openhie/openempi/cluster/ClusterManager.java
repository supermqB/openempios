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
package org.openhie.openempi.cluster;

import java.util.Map;

import org.openhie.openempi.ApplicationException;

public interface ClusterManager
{
	String getClusterNodeId();
	
	CommandRequest executeRemoteRequest(CommandRequest request);
	
	Map<String, Object> getConfigurationRegistry();
	
	void processRequest(CommandRequest request);
	
	void nodeIsReady();
	
	void nodeIsGoingDown();
	
	void start() throws ApplicationException;
	
	void stop() throws ApplicationException;
}
