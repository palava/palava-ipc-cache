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

import java.lang.annotation.Annotation;

import de.cosmocode.palava.ipc.Commands;
import de.cosmocode.palava.ipc.FilterModule;

/**
 * Provides {@link AbstractCacheModule#use(Class)} to register a cache annotation.
 *
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 * @see ComplexCacheAnnotation
 */
public abstract class AbstractCacheModule extends FilterModule {

    /**
     * Registers the given annotation with the cache filter.
     *
     * @param annotation the cache annotation
     */
    public void use(Class<? extends Annotation> annotation) {
        filter(Commands.annotatedWith(annotation)).through(CacheFilter.class);
    }

}
