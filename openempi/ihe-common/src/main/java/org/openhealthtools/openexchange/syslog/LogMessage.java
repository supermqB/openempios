/**
 *  Copyright (c) 2009-2011 Misys Open Source Solutions (MOSS) and others
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  Contributors:
 *    Misys Open Source Solutions - initial API and implementation
 *    -
 */

package org.openhealthtools.openexchange.syslog;


import java.sql.Timestamp;

/**
*
* @author <a href="mailto:venkata.pragallapati@misys.com">Venkat</a>
* 
*/

public interface LogMessage {
	void setTimeStamp(Timestamp timestamp);
	void setSecure(boolean isSecure);
	void setTestMessage(String testMessage);
	void setPass(boolean pass);
	void setIP(String ip) throws LoggerException;
	void setCompany(String companyName) throws LoggerException;
	void addParam(String messageType, String name, String value) throws LoggerException;
	void addHTTPParam(String name, String value) throws LoggerException;
	void addSoapParam(String name, String value) throws LoggerException;
	void addErrorParam(String name, String value) throws LoggerException;
	void addOtherParam(String name, String value) throws LoggerException;
	String getMessageID();
}
