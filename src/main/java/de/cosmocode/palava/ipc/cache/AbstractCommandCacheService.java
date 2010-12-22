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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

/**
 * Abstract skeleton {@link CommandCacheService} implementation.
 *
 * @since 2.21
 * @author Willi Schoenborn
 */
public abstract class AbstractCommandCacheService implements CommandCacheService {

    @Override
    public Map<String, Object> cache(IpcCall call, IpcCommand command, IpcCallFilterChain chain, 
            CachePolicy policy, long maxAge, TimeUnit maxAgeUnit, 
            Collection<Predicate<IpcCall>> filters, FilterMode filterMode) throws IpcCommandExecutionException {

        Preconditions.checkNotNull(call, "Call");
        Preconditions.checkNotNull(filters, "Filters");
        Preconditions.checkNotNull(filterMode, "FilterMode");

        // check if we have filters defined, otherwise fall back on normal behaviour
        if (filters.isEmpty()) {
            // no filters specified: don't check them, fallback to old behaviour
            return cache(call, command, chain, policy, maxAge, maxAgeUnit);
        } else {
            if (filterMode.apply(call, filters)) {
                // filters apply: normal caching
                return cache(call, command, chain, policy, maxAge, maxAgeUnit);
            } else {
                // filters don't apply: normally proceed in call chain
                Preconditions.checkNotNull(command, "Command");
                Preconditions.checkNotNull(chain, "Chain");
                return chain.filter(call, command);
            }
        }
    }
    
}
