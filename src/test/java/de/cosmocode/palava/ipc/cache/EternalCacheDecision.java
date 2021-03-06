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

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.cache.analyzer.AbstractCacheDecision;

/**
 * A {@link CacheDecision} that caches everything eternally.
 *
 * @since 3.0
 * @author Oliver Lorenz
 */
public final class EternalCacheDecision extends AbstractCacheDecision {

    @Override
    public boolean shouldCache() {
        return true;
    }

    @Override
    public boolean isEternal() {
        return true;
    }

    @Override
    public long getIdleTime() {
        return 0;
    }

    @Override
    public TimeUnit getIdleTimeUnit() {
        return TimeUnit.MINUTES;
    }

    @Override
    public long getLifeTime() {
        return 0;
    }

    @Override
    public TimeUnit getLifeTimeUnit() {
        return TimeUnit.MINUTES;
    }

    @Override
    public CacheKey computeKey(IpcCall call, IpcCommand command) {
        return DefaultCacheKeyFactory.INSTANCE.create(call, command);
    }

}
