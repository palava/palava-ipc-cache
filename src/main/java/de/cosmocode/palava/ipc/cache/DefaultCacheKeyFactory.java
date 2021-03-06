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
 * A common utility to generate a serializable and unique cache key from a command call.
 *
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 */
public enum DefaultCacheKeyFactory implements CacheKeyFactory {

    INSTANCE;

    @Override
    public CacheKey create(IpcCall call, IpcCommand command) {
        return new DefaultCacheKey(command.getClass(), call.getArguments());
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    
}
