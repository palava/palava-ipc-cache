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

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * <p>
 * An {@link CacheRatingAnalyzer} that rates by counting the number of arguments in the call.
 * </p>
 * <p>
 * Created on: 05.01.11
 * </p>
 *
 * @author Oliver Lorenz
 */
final class CountArgumentsCacheRatingAnalyzer implements CacheRatingAnalyzer {

    public static final int MAX = 20;

    @Override
    public Rating rate(final IpcCall call, IpcCommand command) {
        final int argumentCount = Math.min(call.getArguments().size(), MAX);

        return new Rating() {
            @Override
            public int value() {
                return argumentCount;
            }

            @Override
            public int min() {
                return 0;
            }

            @Override
            public int max() {
                return MAX;
            }
        };
    }

}
