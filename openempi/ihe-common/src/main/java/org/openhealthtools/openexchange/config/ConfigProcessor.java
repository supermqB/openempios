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

package org.openhealthtools.openexchange.config;

import java.util.Collection;

import org.openhealthtools.openexchange.actorconfig.IActorDescription;


/**
 * The interface for configureation processor to handle configuration
 * preprocess and postprocess. 
 * 
 * @author <a href="mailto:wenzhi.li@misys.com">Wenzhi Li</a>
 */
public interface ConfigProcessor {

	/**
	 * The logics to invoke before the configuration is loaded.
	 */
    void preProcess();
	
	/**
	 * The logics to invoke after the configuration is loaded.
	 */
    void postProcess(Collection<IActorDescription> actors);
}
