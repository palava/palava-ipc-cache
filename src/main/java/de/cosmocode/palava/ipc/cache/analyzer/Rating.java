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

/**
 * Used by {@link CacheRatingAnalyzer} to define their rating.
 *
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 * @see CacheRatingAnalyzer
 */
public interface Rating {

    /**
     * The actual rating of the analyzed command call.
     *
     * @return the rating
     */
    int value();

    /**
     * The minimal possible rating.
     *
     * @return the minimal rating
     */
    int min();

    /**
     * the maximum possible rating.
     *
     * @return the maximum rating
     */
    int max();

}
