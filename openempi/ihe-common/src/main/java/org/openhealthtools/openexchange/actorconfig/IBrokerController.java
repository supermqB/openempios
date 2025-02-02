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

package org.openhealthtools.openexchange.actorconfig;

/**
 * This interface is used in objects that are passed to Broker
 * reset methods.
 * 
 * @author Jim Firby
 */
public interface IBrokerController {
	
	/**
	 * Called by the Broker to see if the supplied actor should
	 * be removed and stopped.
	 * 
	 * @return 'true' if the supplied actor should be removed and stopped
	 */
    boolean shouldUnregister(Object actor);

}
