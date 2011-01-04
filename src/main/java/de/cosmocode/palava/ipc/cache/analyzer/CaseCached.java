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

import com.google.common.base.Predicate;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.cache.ComplexCacheAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: olor
 * Date: 04.01.11
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ComplexCacheAnnotation(analyzer = CaseCacheAnalyzer.class)
public @interface CaseCached {

    /**
     * <p>
     * List of predicate classes that are instantiated by guice.
     * Each time a call comes in that should be cached it is run through the predicates.
     * Depending on the filter mode either all, at least one or none of the predicates must apply
     * to continue caching, otherwise the call is not cached.
     * </p>
     * <p>
     * <strong>Important:</strong> If no filters are specified then no filtering happens
     * and all calls are cached.
     * </p>
     *
     * @since 2.3
     * @return a list of predicate filter classes, default is an empty list
     */
    Class<? extends Predicate<IpcCall>>[] filters() default { };

    /**
     * The filter mode that determines how many filters must match to continue caching.
     * Default is {@link de.cosmocode.palava.ipc.cache.analyzer.CaseCacheMode#ANY}.
     * <ul>
     *   <li> ALL: all predicates must apply (i.e. return true on apply(call)) </li>
     *   <li> ANY: at least one of the predicates must apply (i.e. return true on apply(call) </li>
     *   <li> NONE: none of the predicates must apply, or: all predicates must return true on apply(call) </li>
     * </ul>
     *
     * @since 2.3
     * @return the filter mode in which to apply the filters on the call
     */
    CaseCacheMode filterMode() default CaseCacheMode.ANY;

    long lifeTime() default 0;

    TimeUnit lifeTimeUnit() default TimeUnit.MINUTES;

    long idleTime() default 0;

    TimeUnit idleTimeUnit() default TimeUnit.MINUTES;

}
