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

import java.util.concurrent.TimeUnit;

import de.cosmocode.palava.ipc.cache.CacheDecision;

/**
 * Abstract {@link CacheDecision} implementation.
 *
 * @since 3.0
 * @author Willi Schoenborn
 */
public abstract class AbstractCacheDecision implements CacheDecision {

    @Override
    public boolean isEternal() {
        return getLifeTime() == 0L && getIdleTime() == 0L;
    }
    
    @Override
    public long getLifeTimeIn(TimeUnit unit) {
        return unit.convert(getLifeTime(), getLifeTimeUnit());
    }
    
    @Override
    public long getIdleTimeIn(TimeUnit unit) {
        return unit.convert(getIdleTime(), getIdleTimeUnit());
    }

    @Override
    public String toString() {
        return "CacheDecision [" +
                "shouldCache=" + shouldCache() + ", " +
                "lifeTime=" + getLifeTime() + ", " +
                "lifeTimeUnit=" + getLifeTimeUnit() + ", " +
                "idleTime=" + getIdleTime() + ", " +
                "idleTimeUnit=" + getIdleTimeUnit() + 
            "]";
    }
    
}
