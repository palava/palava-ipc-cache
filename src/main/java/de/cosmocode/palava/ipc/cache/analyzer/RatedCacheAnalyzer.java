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
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.cache.AbstractCacheAnalyzer;
import de.cosmocode.palava.ipc.cache.CacheDecision;

import java.util.concurrent.TimeUnit;

/**
 * {@link de.cosmocode.palava.ipc.cache.CacheAnalyzer} implementation for {@link RatedCached}.
 *
 * @since 3.0
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @see RatedCached
 */
final class RatedCacheAnalyzer extends AbstractCacheAnalyzer<RatedCached> {

    private final Injector injector;

    @Inject
    RatedCacheAnalyzer(Injector injector) {
        this.injector = Preconditions.checkNotNull(injector, "Injector");
    }

    @Override
    protected CacheDecision decide(final RatedCached annotation, IpcCall call, IpcCommand command) {

        final CacheRatingAnalyzer analyzer = injector.getInstance(annotation.analyzer());
        final Rating rating = analyzer.rate(call, command);
        final long range = rating.max() - rating.min();
        final long realValue = rating.value() - rating.min();
        final long ratio = realValue / range;

        final long lifeTime;
        final TimeUnit lifeTimeUnit;
        final long idleTime;
        final TimeUnit idleTimeUnit;
        final boolean shouldCache;

        if (annotation.target() == RatedCached.RatingTarget.LIFE_TIME) {
            Preconditions.checkArgument(annotation.lifeTime() > 0, "lifeTime must be greater than 0");

            idleTime = annotation.idleTime();
            idleTimeUnit = annotation.idleTimeUnit();

            lifeTimeUnit = TimeUnit.SECONDS;
            lifeTime = annotation.lifeTimeUnit().toSeconds(annotation.lifeTime()) * ratio;

            shouldCache = lifeTime >= annotation.minTimeUnit().toSeconds(annotation.minTime());

        } else if (annotation.target() == RatedCached.RatingTarget.IDLE_TIME) {
            Preconditions.checkArgument(annotation.idleTime() > 0, "idleTime must be greater than 0");

            lifeTime = annotation.lifeTime();
            lifeTimeUnit = annotation.lifeTimeUnit();

            idleTimeUnit = TimeUnit.SECONDS;
            idleTime = annotation.idleTimeUnit().toSeconds(annotation.idleTime()) * ratio;

            shouldCache = idleTime >= annotation.minTimeUnit().toSeconds(annotation.minTime());

        } else {
            throw new IllegalStateException("Unknown rating target " + annotation.target());
        }


        return new CacheDecision() {

            @Override
            public boolean shouldCache() {
                return shouldCache;
            }

            @Override
            public long getLifeTime() {
                return lifeTime;
            }

            @Override
            public TimeUnit getLifeTimeUnit() {
                return lifeTimeUnit;
            }

            @Override
            public long getIdleTime() {
                return idleTime;
            }

            @Override
            public TimeUnit getIdleTimeUnit() {
                return idleTimeUnit;
            }

        };
    }

}
