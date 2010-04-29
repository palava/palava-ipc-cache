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
import de.cosmocode.palava.ipc.IpcConnection;
import de.cosmocode.palava.ipc.IpcSession;

/**
 * A CachePolicy decides how to cache an {@link IpcCommand}.
 * 
 * @author Willi Schoenborn
 * @author Oliver Lorenz
 */
public enum CachePolicy {

    /**
     * <p> A CachePolicy where several things are considered for caching.
     * These are:
     * </p>
     * <ul>
     *   <li> the {@link Class#getName()} of the command-class, </li>
     *   <li> the {@linkplain IpcCall#getArguments() Arguments} of the {@link IpcCall} </li>
     *   <li> scope variables, as follows:
     *     <dl>
     *       <dt> in call scope
     *       <dd> if the property command.cache.keys.call=... is set and
     *            {@link IpcCall#get(Object)} returns a non-null value for at least one of the keys
     *       <dt> in connection scope
     *       <dd> if the property command.cache.keys.connection=... is set and
     *            {@link IpcConnection#get(Object)} returns a non-null value for at least one of the keys
     *       <dt> in session scope
     *       <dd> if the property command.cache.keys.session=... is set and
     *            {@link IpcSession#get(Object)} returns a non-null value for at least one of the keys
     *     </dl>
     *   </li>
     * </ul>
     * <p> If no arguments are provided then they are not considered for caching.
     * </p>
     * <p> If none of the scope properties are set or none of the scopes
     * has a value for the given scope keys then no scope is considered for caching.
     * </p>
     * <p> If neither arguments are provided nor any scope keys set
     * or present (see above paragraph) then only the name of the command class
     * is considered for caching. In this case CachePolicy.SMART behaves equivalent
     * to CachePolicy.STATIC.
     * </p>
     */
    SMART,
    
    /**
     * <p> A CachePolicy where only the fully qualified name of the command-class
     * is used for caching.
     * </p>
     * <p> This means that the Command annotated with this CachePolicy
     * always returns the same result after the first call.
     * It can only return a different result if a maxAge was set on
     * the {@code @Cache} annotation and that maxAge has passed since
     * the first call.
     * </p>
     * <p> <strong> Important: </strong>
     * If a command annotated with {@code @Cache} and the cache policy set to STATIC
     * is called with 1 or more arguments, then an IllegalStateException is thrown
     * by the cache filter. This implies that all commands that are annotated with
     * this CachePolicy <strong>must</strong> be called without any arguments.
     * </p>
     * <p> This CachePolicy is useful if a job does not require any arguments
     * and always returns the same result, for example a list of all
     * java enums (and their values) that the system knows about.
     * </p>
     */
    STATIC;
    
}
