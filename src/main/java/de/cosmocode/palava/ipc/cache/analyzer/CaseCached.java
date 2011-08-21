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

import de.cosmocode.palava.ipc.cache.CacheKeyFactory;
import de.cosmocode.palava.ipc.cache.ComplexCacheAnnotation;
import de.cosmocode.palava.ipc.cache.DefaultCacheKeyFactory;

/**
 * <p>
 * Caches the results only if it passes a predefined set of predicates, which must implement {@link CachePredicate}.
 * The mode in which these predicates are combined is defined by {@link #mode()}.
 * </p>
 * <p>
 * Example for a CachePredicate:
 * <pre>
 * final class HasDatePredicate implements CachePredicate {
 *     &#64;Override
 *     public boolean apply(IpcCall call, IpcCommand command) {
 *         return call.getArguments().get("date") instanceof Date;
 *     }
 * }
 * </pre>
 * </p>
 *
 * @since 3.0
 * @author Oliver Lorenz
 * @author Tobias Sarnowski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ComplexCacheAnnotation(analyzer = CaseCacheAnalyzer.class)
public @interface CaseCached {

    /**
     * <p>
     * List of predicate classes that are instantiated by guice.
     * Each time a command is called that is annotated with this annotation
     * then that command and its call are run through the predicates.
     * Depending on the {@link #mode()} either all, at least one or none of the predicates must apply
     * to continue caching, otherwise the call is not cached.
     * </p>
     * <p>
     * <strong>Important:</strong> If no predicates are specified (i.e. predicates is an empty array),
     * then a RuntimeException will be thrown.
     * If you want to have it always cached then use {@link TimeCached} instead.
     * </p>
     *
     * @since 3.0
     * @return a list of predicate classes
     */
    Class<? extends CachePredicate>[] predicates();

    /**
     * <p>
     * The mode that determines how many predicates must match to continue caching.
     * Default is {@link CaseCacheMode#ANY}.
     * </p>
     * <p>
     * A predicate applies if it returns true on its
     * {@link CachePredicate#apply(de.cosmocode.palava.ipc.IpcCall, de.cosmocode.palava.ipc.IpcCommand)}
     * method.
     * </p>
     * <p>
     * Possible modes:
     * </p>
     * <ul>
     *   <li> ALL: all predicates must apply </li>
     *   <li> ANY: at least one of the predicates must apply </li>
     *   <li> NONE: none of the predicates must apply </li>
     * </ul>
     *
     * @since 3.0
     * @return the filter mode in which to apply the predicates on the call
     */
    CaseCacheMode mode() default CaseCacheMode.ANY;

    /**
     * The amount of maximum lifetime.
     *
     * @since 3.0
     * @return lifetime amount
     * @see de.cosmocode.palava.ipc.cache.CacheDecision#getLifeTime()
     */
    long lifeTime() default 0;

    /**
     * The unit of maximum lifetime.
     *
     * @since 3.0
     * @return lifetime unit
     * @see de.cosmocode.palava.ipc.cache.CacheDecision#getLifeTimeUnit()
     */
    TimeUnit lifeTimeUnit() default TimeUnit.MINUTES;

    /**
     * The amount of maximum idletime.
     *
     * @since 3.0
     * @return idletime amount
     * @see de.cosmocode.palava.ipc.cache.CacheDecision#getIdleTime()
     */
    long idleTime() default 0;

    /**
     * The unit of maximum idletime.
     *
     * @since 3.0
     * @return idletime unit
     * @see de.cosmocode.palava.ipc.cache.CacheDecision#getIdleTimeUnit()
     */
    TimeUnit idleTimeUnit() default TimeUnit.MINUTES;

    Class<? extends CacheKeyFactory> keyFactory() default DefaultCacheKeyFactory.class;

}
