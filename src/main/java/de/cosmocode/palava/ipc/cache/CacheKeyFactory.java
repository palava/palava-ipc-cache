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

import java.io.Serializable;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * <p> Creates the cache keys for the {@link CacheFilter}
 * for all commands annotated with {@code @Cache}
 * and the policy set to {@link CachePolicy#SMART}.
 * </p>
 * <p> The default implementation takes the class name of the command
 * and the arguments of the call.
 * </p>
 *
 * @author Oliver Lorenz
 */
public interface CacheKeyFactory {
    
    /**
     * <p> Creates the cache key for the {@link CacheFilter}.
     * Must not return null.
     * </p>
     * 
     * @param call the current call
     * @param command the current command
     * @return a Serializable that uniquely identifies the cache key
     */
    Serializable create(IpcCall call, IpcCommand command);

}
