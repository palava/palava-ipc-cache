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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import de.cosmocode.palava.ipc.cache.ComplexCacheAnnotation;

/**
 * Caches the result for the given amount of time.
 *
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 * @see TimeCachedModule
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ComplexCacheAnnotation(analyzer = TimeCacheAnalyzer.class)
public @interface TimeCached {

    /**
     * The amount of maximum lifetime.
     *
     * @return lifetime amount
     * @see de.cosmocode.palava.ipc.cache.CacheDecision#getLifeTime()
     */
    long lifeTime() default 0;

    /**
     * The unit of maximum lifetime.
     *
     * @return lifetime unit
     * @see de.cosmocode.palava.ipc.cache.CacheDecision#getLifeTimeUnit()
     */
    TimeUnit lifeTimeUnit() default TimeUnit.MINUTES;

    /**
     * The amount of maximum idletime.
     *
     * @return idletime amount
     * @see de.cosmocode.palava.ipc.cache.CacheDecision#getIdleTime()
     */
    long idleTime() default 0;

    /**
     * The unit of maximum idletime.
     *
     * @return idletime unit
     * @see de.cosmocode.palava.ipc.cache.CacheDecision#getIdleTimeUnit()
     */
    TimeUnit idleTimeUnit() default TimeUnit.MINUTES;

}
