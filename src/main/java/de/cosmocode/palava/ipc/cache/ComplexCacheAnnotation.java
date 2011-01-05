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

/**
 * Used to connect cache annotations with their analyzer.
 *
 * <p>
 * Example:
 * <pre>
 * &#64;Retention(RetentionPolicy.RUNTIME)
 * &#64;ComplexCacheAnnotation(analyzer = ExampleCachedAnalyzerImpl.class)
 * public &#64;interface ExampleCached {
 *
 * }
 * </pre>
 * </p>
 *
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 * @see CacheAnalyzer
 * @see AbstractCacheModule
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ComplexCacheAnnotation {

    Class<? extends CacheAnalyzer> analyzer();

}
