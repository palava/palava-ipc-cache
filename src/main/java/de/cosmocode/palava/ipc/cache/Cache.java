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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Annotation to declare a cachable state.
 *
 * @author Willi Schoenborn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cache {

    CachePolicy policy() default CachePolicy.SMART;
    
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
