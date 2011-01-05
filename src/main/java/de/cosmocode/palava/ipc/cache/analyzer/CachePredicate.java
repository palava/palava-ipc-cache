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

import javax.annotation.Nonnull;

/**
 * <p>
 * Denotes a CachePredicate, usable as a filter in {@link CaseCached}.
 * The naming convention is derived from {@link com.google.common.base.Predicate},
 * but the method signature is specifically modified for ipc purposes.
 * </p>
 * <p>
 * Created on: 05.01.11
 * </p>
 *
 * @since 3.0
 * @author Oliver Lorenz
 */
public interface CachePredicate {

    /**
     * Returns true if this predicate applies to the given call and command.
     * The given parameters can be assumed to never be null.
     *
     * @param call the current call
     * @param command the current command
     * @return true if this CachePredicate applies to the given call and command, false otherwise
     */
    boolean apply(@Nonnull IpcCall call, @Nonnull IpcCommand command);

}
