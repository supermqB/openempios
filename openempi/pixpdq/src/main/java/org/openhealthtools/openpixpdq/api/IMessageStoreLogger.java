/**
 *  Copyright (c) 2009-2010 Misys Open Source Solutions (MOSS) and others
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
package org.openhealthtools.openpixpdq.api;


/**
 * This interface logger logs a message to be persistent.
 * 
 * @author Wenzhi Li
 * @version 1.0, Dec 15, 2008
 */
public interface IMessageStoreLogger {
	/**
	 * Method to save the MessageStore object.
	 *  
	 * @param messageLog a {@link MessageStore}
	 */
    public void saveLog(MessageStore messageLog);

}
