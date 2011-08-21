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

import de.cosmocode.palava.cache.CacheExpiration;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * Represents the decision of a {@link CacheAnalyzer} how to cache a {@link de.cosmocode.palava.ipc.IpcCommand}s result.
 *
 * @since 3.0
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @see CacheAnalyzer
 */
public interface CacheDecision extends CacheExpiration {

    /**
     * Whether the result should be cached or not.
     *
     * @return true if the result should be cached, false otherwise
     */
    boolean shouldCache();

    /**
     * Computes the cache key.
     *
     * @param call the current call
     * @param command the executing command
     * @return the cache key
     */
    CacheKey computeKey(IpcCall call, IpcCommand command);

}
