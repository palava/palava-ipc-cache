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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.cache.AbstractCacheAnalyzer;
import de.cosmocode.palava.ipc.cache.CacheDecision;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: olor
 * Date: 04.01.11
 * Time: 14:58
 * To change this template use File | Settings | File Templates.
 */
final class CaseCacheAnalyzer extends AbstractCacheAnalyzer<CaseCached> {

    private final Injector injector;

    @Inject
    CaseCacheAnalyzer(Injector injector) {
        this.injector = Preconditions.checkNotNull(injector, "Injector");
    }

    @Override
    protected CacheDecision decide(final CaseCached annotation, IpcCall call, IpcCommand command) {
        final boolean shouldCache;

        // filter handling
        if (annotation.filters().length == 0) {
            // no filters: should always cache
            shouldCache = true;
        } else {
            // we have filters specified: create them via injector
            final List<Predicate<IpcCall>> filters = Lists.newArrayListWithCapacity(annotation.filters().length);

            for (Class<? extends Predicate<IpcCall>> predicateClass : annotation.filters()) {
                filters.add(injector.getInstance(predicateClass));
            }

            shouldCache = annotation.filterMode().apply(call, filters);
        }

        return new CacheDecision() {

            @Override
            public boolean shouldCache() {
                return shouldCache;
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

        };
    }

}
