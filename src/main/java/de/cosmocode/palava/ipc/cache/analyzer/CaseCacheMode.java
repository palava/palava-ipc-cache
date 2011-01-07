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

import com.google.common.annotations.Beta;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * <p>
 * CaseCacheMode used by {@link de.cosmocode.palava.ipc.cache.analyzer.CaseCached}.
 * It can be used to specify how the {@link CachePredicate}s should be combined.
 * Each enum value gives detailed information what it does.
 * </p>
 * <p>
 * A previous version of this enum was first introduced in 2.3 as FilterCacheMode,
 * but since the name and the method signature of its only method changed it is now considered new.
 * </p>
 *
 * @since 3.0
 * @author Oliver Lorenz
 */
@Beta
public enum CaseCacheMode {

    /**
     * All predicates must apply, i.e. every {@link CachePredicate} must return true on its apply method.
     */
    ALL {

        @Override
        public boolean apply(Iterable<CachePredicate> filters, IpcCall call, IpcCommand command) {
            for (CachePredicate filter : filters) {
                if (filter.apply(call, command)) {
                    continue;
                } else {
                    return false;
                }
            }
            // the loop did not exit, so every filter applied: return true
            return true;
        }

    },

    /**
     * Any filter must apply, i.e. at least one {@link CachePredicate} must return true on its apply method.
     */
    ANY {

        @Override
        public boolean apply(Iterable<CachePredicate> filters, IpcCall call, IpcCommand command) {
            for (final CachePredicate filter : filters) {
                if (filter.apply(call, command)) {
                    return true;
                }
            }
            return false;
        }

    },

    /**
     * None of the predicates must apply, i.e. every {@link CachePredicate} must return false on its apply method.
     */
    NONE {

        @Override
        public boolean apply(Iterable<CachePredicate> filters, IpcCall call, IpcCommand command) {
            for (final CachePredicate filter : filters) {
                if (filter.apply(call, command)) {
                    return false;
                }
            }
            return true;
        }

    };

    /**
     * Returns true if the predicates apply to the call in this filter mode.
     *
     * @param filters the predicates to apply
     * @param call the call to apply each of the predicates on
     * @param command the command to apply each of the predicates on
     * @return true if this mode applies to the predicates, false otherwise
     */
    public abstract boolean apply(Iterable<CachePredicate> filters, IpcCall call, IpcCommand command);

}
