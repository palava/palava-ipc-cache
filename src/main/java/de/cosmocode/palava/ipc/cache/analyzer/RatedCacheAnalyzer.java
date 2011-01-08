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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Injector;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.cache.AbstractCacheAnalyzer;
import de.cosmocode.palava.ipc.cache.CacheDecision;

/**
 * {@link de.cosmocode.palava.ipc.cache.CacheAnalyzer} implementation for {@link RatedCached}.
 *
 * @since 3.0
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @see RatedCached
 */
final class RatedCacheAnalyzer extends AbstractCacheAnalyzer<RatedCached> {

    private static final Logger LOG = LoggerFactory.getLogger(RatedCacheAnalyzer.class);

    private final Injector injector;

    @Inject
    RatedCacheAnalyzer(Injector injector) {
        this.injector = Preconditions.checkNotNull(injector, "Injector");
    }

    @Override
    protected CacheDecision decide(RatedCached annotation, IpcCall call, IpcCommand command) {

        final CacheRatingAnalyzer analyzer = injector.getInstance(annotation.analyzer());
        final Rating rating = analyzer.rate(call, command);
        final long range = rating.max() - rating.min();
        final long realValue = rating.value() - rating.min();

        final long lifeTime;
        final TimeUnit lifeTimeUnit;
        final long idleTime;
        final TimeUnit idleTimeUnit;
        final boolean shouldCache;

        if (annotation.target() == RatingTarget.LIFE_TIME) {
            Preconditions.checkArgument(annotation.lifeTime() > 0, "lifeTime must be greater than 0");

            idleTime = annotation.idleTime();
            idleTimeUnit = annotation.idleTimeUnit();

            lifeTimeUnit = TimeUnit.SECONDS;
            lifeTime = annotation.lifeTimeUnit().toSeconds(annotation.lifeTime()) * realValue / range;

            if (LOG.isTraceEnabled()) {
                LOG.trace("Calculated lifeTime of {} seconds from {}, {}, {}",
                    new Object[] {lifeTime, annotation, call, command.getClass()});
            }

            shouldCache = lifeTime >= annotation.minTimeUnit().toSeconds(annotation.minTime());

        } else if (annotation.target() == RatingTarget.IDLE_TIME) {
            Preconditions.checkArgument(annotation.idleTime() > 0, "idleTime must be greater than 0");

            lifeTime = annotation.lifeTime();
            lifeTimeUnit = annotation.lifeTimeUnit();

            idleTimeUnit = TimeUnit.SECONDS;
            idleTime = annotation.idleTimeUnit().toSeconds(annotation.idleTime()) * realValue / range;

            if (LOG.isTraceEnabled()) {
                LOG.trace("Calculated idleTime of {} seconds from {}, {}, {}",
                    new Object[] {idleTime, annotation, call, command.getClass()});
            }

            shouldCache = idleTime >= annotation.minTimeUnit().toSeconds(annotation.minTime());

        } else {
            throw new IllegalStateException("Unknown rating target " + annotation.target());
        }

        return new RatedCacheDecision(shouldCache, lifeTime, lifeTimeUnit, idleTime, idleTimeUnit);
    }

}
