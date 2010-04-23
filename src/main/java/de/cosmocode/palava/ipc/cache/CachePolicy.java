/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.ipc.cache;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * A CachePolicy decides how to cache an {@link IpcCommand}.
 * 
 * @author Willi Schoenborn
 * @author Oliver Lorenz
 */
public enum CachePolicy {

    /**
     * TODO update
     * 
     * <p> A CachePolicy where the fully qualified name of the command-class,
     * the {@linkplain IpcCall#getArguments() Arguments} of a Call
     * and the {@linkplain HttpSession#getLocale() Locale} of the Session of a Call
     * are taken into consideration for caching.
     * </p>
     * <h4>This policy behaves differently if nulls occur: </h4>
     * <ul>
     *   <li> If the locale is null, then no caching occurs. </li> 
     *   <li> If the arguments are null, then they are ignored 
     *        and only the locale and the fully qualified name are taken for caching </li>
     * </ul>
     */
    SMART,
    
    /**
     * <p> A CachePolicy where only the fully qualified name of the command-class
     * is used for caching.
     * TODO + arguments
     * </p>
     * <p> This means that the Command annotated with this CachePolicy
     * always returns the same Content, independent of the call.
     * </p>
     */
    STATIC;
    
}
