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

import org.openhie.openempi.model.Report;
import org.openhie.openempi.model.ReportRequestEntry;

public interface ReportDao extends UniversalDao
{
	Report addReport(Report report);
	
	Report getReportByName(String reportName);
	
	Report getReportById(Integer reportId);
	
	List<ReportRequestEntry> getReportRequests();
	
	ReportRequestEntry addReportRequest(ReportRequestEntry reportRequest);
	
	ReportRequestEntry updateReportRequest(ReportRequestEntry reportRequestEntry);
	
	void reassignReportRequestsToReport(Integer oldReportId, Integer newReportId);
	
	List<Report> getReports();
	
	Report updateReport(Report report);
	
	void removeReport(Report report);
}
