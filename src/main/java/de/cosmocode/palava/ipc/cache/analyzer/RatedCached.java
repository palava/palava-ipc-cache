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

import de.cosmocode.palava.ipc.cache.ComplexCacheAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Caches the command call result depending of the
 * {@link de.cosmocode.palava.ipc.cache.analyzer.RatedCached#analyzer()}'s rating.
 *
 * <p>
 * The annotation can operate in two modi:
 * <ul>
 * <li>{@link RatedCached.RatingTarget#LIFE_TIME}: the lifetime will be shorten depending of the rating.</li>
 * <li>{@link RatedCached.RatingTarget#IDLE_TIME}: the idletime will be shorten depending of the rating.</li>
 * </ul>
 * </p>
 * <p>
 * Configure lifetime and idletime to their maximum amount and provide a minimum amount of time a result should be given
 * to the cache.
  * </p>
 * <p>
 * <i>The higher the rating, the longer the result will be cached.</i>
 * </p>
 *
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 * @see CacheRatingAnalyzer
 * @see RatedCachedModule
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ComplexCacheAnnotation(analyzer = RatedCacheAnalyzer.class)
public @interface RatedCached {

    Class<? extends CacheRatingAnalyzer> analyzer();

    int minTime() default 1;

    TimeUnit minTimeUnit() default TimeUnit.MINUTES;

    long lifeTime() default 0;

    TimeUnit lifeTimeUnit() default TimeUnit.MINUTES;

    long idleTime() default 0;

    TimeUnit idleTimeUnit() default TimeUnit.MINUTES;

    /**
     * <p>
     * The rating target specifies which of the two gets rated: life time or idle time.
     * </p>
     * <p>
     * If the target is set to {@link RatingTarget#LIFE_TIME}
     * then the life time is rated by the analyzer while the idle time remains the same.
     * If the target is set to {@link RatingTarget#IDLE_TIME},
     * then the life time remains the same and the idle time is rated by the analyzer.
     * </p>
     *
     * @return the rating target, default is {@link RatingTarget#LIFE_TIME}
     */
    RatingTarget target() default RatedCached.RatingTarget.LIFE_TIME;

    /**
     * The rating target enum for {@link RatedCached#target()}.
     */
    enum RatingTarget {
        LIFE_TIME,
        IDLE_TIME
    }

}
