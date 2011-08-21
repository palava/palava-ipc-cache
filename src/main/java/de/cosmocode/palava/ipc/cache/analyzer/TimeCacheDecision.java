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

package de.cosmocode.palava.ipc.cache.analyzer;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.cache.CacheDecision;
import de.cosmocode.palava.ipc.cache.CacheKey;
import de.cosmocode.palava.ipc.cache.CacheKeyFactory;

import java.util.concurrent.TimeUnit;

/**
 * A {@link TimeCached} based {@link CacheDecision}.
 *
 * @since 3.0
 * @author Oliver Lorenz
 * @author Tobias Sarnowski
 */
final class TimeCacheDecision extends AbstractCacheDecision {

    private final TimeCached annotation;
    private final CacheKeyFactory keyFactory;

    public TimeCacheDecision(TimeCached annotation, CacheKeyFactory keyFactory) {
        this.annotation = annotation;
        this.keyFactory = keyFactory;
    }

    @Override
    public boolean shouldCache() {
        return true;
    }

    @Override
    public long getLifeTime() {
        return annotation.lifeTime();
    }

    @Override
    public TimeUnit getLifeTimeUnit() {
        return annotation.lifeTimeUnit();
    }

    @Override
    public long getIdleTime() {
        return annotation.idleTime();
    }

    @Override
    public TimeUnit getIdleTimeUnit() {
        return annotation.idleTimeUnit();
    }

    @Override
    public CacheKey computeKey(IpcCall call, IpcCommand command) {
        return keyFactory.create(call, command);
    }

}
