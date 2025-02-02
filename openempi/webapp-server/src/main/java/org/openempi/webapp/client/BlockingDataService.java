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
package org.openempi.webapp.client;

import java.util.List;

import org.openempi.webapp.client.model.BlockingEntryListWeb;
import org.openempi.webapp.client.model.SortedNeighborhoodConfigurationWeb;
import org.openempi.webapp.client.model.SuffixArrayBlockingConfigurationWeb;

import com.google.gwt.user.client.rpc.RemoteService;

public interface BlockingDataService extends RemoteService
{
	BlockingEntryListWeb loadTraditionalBlockingConfigurationData(String entityName) throws Exception;
	
	SortedNeighborhoodConfigurationWeb loadSortedNeighborhoodBlockingConfigurationData(String entityName) throws Exception;
	
	SuffixArrayBlockingConfigurationWeb loadSuffixArrayBlockingConfigurationData(String entityName) throws Exception;
	
	String saveTraditionalBlockingConfigurationData(BlockingEntryListWeb configuration) throws Exception;
	
	String saveSortedNeighborhoodBlockingConfigurationData(SortedNeighborhoodConfigurationWeb configuration) throws Exception;
	
	String saveSuffixArrayBlockingConfigurationData(SuffixArrayBlockingConfigurationWeb configuration) throws Exception;
}
