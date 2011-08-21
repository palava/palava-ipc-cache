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
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.cache.AbstractCacheAnalyzer;
import de.cosmocode.palava.ipc.cache.CacheDecision;
import de.cosmocode.palava.ipc.cache.CacheKeyFactory;
import de.cosmocode.palava.ipc.cache.DefaultCacheKeyFactory;

import java.util.List;

/**
 * {@link de.cosmocode.palava.ipc.cache.CacheAnalyzer} implementation for {@link CaseCached}.
 *
 * @since 3.0
 * @author Oliver Lorenz
 * @author Tobias Sarnowski
 */
final class CaseCacheAnalyzer extends AbstractCacheAnalyzer<CaseCached> {

    private final Injector injector;

    @Inject
    CaseCacheAnalyzer(Injector injector) {
        this.injector = Preconditions.checkNotNull(injector, "Injector");
    }

    @Override
    protected CacheDecision decide(CaseCached annotation, IpcCall call, IpcCommand command) {
        final Class<? extends CachePredicate>[] predicates = annotation.predicates();
        final List<CachePredicate> filters = Lists.newArrayListWithCapacity(predicates.length);

        for (Class<? extends CachePredicate> predicateClass : predicates) {
            filters.add(injector.getInstance(predicateClass));
        }

        final boolean shouldCache = annotation.mode().apply(filters, call, command);
        final CacheKeyFactory keyFactory;

        if (annotation.keyFactory() == DefaultCacheKeyFactory.class) {
            keyFactory = DefaultCacheKeyFactory.INSTANCE;
        } else {
            keyFactory = injector.getInstance(annotation.keyFactory());
        }

        return new CaseCacheDecision(shouldCache, annotation, keyFactory);
    }

}
