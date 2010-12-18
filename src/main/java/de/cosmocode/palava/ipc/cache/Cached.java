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

import com.google.common.base.Predicate;
import de.cosmocode.palava.ipc.IpcCall;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Annotation to declare a cachable state.
 *
 * @author Willi Schoenborn
 * @author Oliver Lorenz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cached {

    /**
     * Instance of {@link CachePolicy} defining the caching algorithm.
     * 
     * @since 2.0
     * @return the policy
     */
    CachePolicy policy() default CachePolicy.SMART;

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
     * @return a list of predicate filter classes, default is an empty list
     */
    Class<Predicate<IpcCall>>[] filters() default { };

    /**
     * The filter mode that determines how many filters must match to continue caching.
     * Default is {@link FilterMode#ANY}.
     * <ul>
     *   <li> ALL: all predicates must apply (i.e. return true on apply(call)) </li>
     *   <li> ANY: at least one of the predicates must apply (i.e. return true on apply(call) </li>
     *   <li> NONE: none of the predicates must apply, or: all predicates must return true on apply(call) </li>
     * </ul>
     * @return the filter mode in which to apply the filters on the call
     */
    FilterMode filterMode() default FilterMode.ANY;
    
    /**
     * <p> The maximum age of a cached command.
     * The time unit is given by {@link #maxAgeUnit()}.
     * </p>
     * <p> A non-positive value (i.e. <= 0) means that every command is cached eternally.
     * The default is 0, which means that every command is cached eternally by default.
     * </p>
     * @return the maximum age of every command in {@link #maxAgeUnit()}
     */
    long maxAge() default 0;
    
    /**
     * <p> The time unit for {@link #maxAge()}.
     * Defaults to {@link TimeUnit#MINUTES}.
     * </p>
     * 
     * @return the time unit for {@link #maxAge()}
     */
    TimeUnit maxAgeUnit() default TimeUnit.MINUTES;
    
}
