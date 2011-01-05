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

import java.util.concurrent.TimeUnit;

/**
 * Represents the decision of a {@link CacheAnalyzer} how to cache a {@link de.cosmocode.palava.ipc.IpcCommand}s result.
 *
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 * @see CacheAnalyzer
 */
public interface CacheDecision {

    /**
     * Whether the result should be cached or not.
     *
     * @return true if the result should be cached, false otherwise
     */
    boolean shouldCache();

    /**
     * How long the maximum amount of time is the result should be cached.
     *
     * @return amount of time or zero for infinite caching.
     * @see de.cosmocode.palava.ipc.cache.CacheDecision#getLifeTimeUnit()
     */
    long getLifeTime();

    /**
     * Timeunit of the amount of lifetime.
     *
     * @return unit of the lifetime.
     * @see de.cosmocode.palava.ipc.cache.CacheDecision#getLifeTime()
     */
    TimeUnit getLifeTimeUnit();

    /**
     * How long the maximum amount of time is the result should idle in the cache.
     *
     * @return amount of time or zero for infinite caching.
     * @see de.cosmocode.palava.ipc.cache.CacheDecision#getIdleTimeUnit()
     */
    long getIdleTime();

    /**
     * Timeunit of the amount of idletime.
     *
     * @return unit of the idletime.
     * @see de.cosmocode.palava.ipc.cache.CacheDecision#getIdleTime()
     */
    TimeUnit getIdleTimeUnit();

}
