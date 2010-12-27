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

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;

import de.cosmocode.palava.ipc.IpcCall;

/**
 * FilterMode used by {@link Cached}.
 *
 * @since 2.3
 * @author Oliver Lorenz
 */
@Beta
public enum FilterMode {

    /**
     * All filters must apply, i.e. every filter must return true on its apply(call) method.
     */
    ALL {

        @Override
        public boolean apply(IpcCall call, Iterable<Predicate<IpcCall>> filters) {
            for (final Predicate<IpcCall> filter : filters) {
                // if any filter does NOT apply to the call then we can exit here already (lazy evaluation)
                if (!filter.apply(call)) {
                    return false;
                }
            }
            // the loop did not exit, so every filter applied: return true
            return true;
        }

    },

    /**
     * Any filter must apply, i.e. at least one filter must return true on its apply(call) method.
     */
    ANY {

        @Override
        public boolean apply(IpcCall call, Iterable<Predicate<IpcCall>> filters) {
            for (final Predicate<IpcCall> filter : filters) {
                if (filter.apply(call)) {
                    return true;
                }
            }
            return false;
        }

    },

    /**
     * None of the filters must apply, i.e. every filter must return false on its apply(call) method.
     */
    NONE {

        @Override
        public boolean apply(IpcCall call, Iterable<Predicate<IpcCall>> filters) {
            for (final Predicate<IpcCall> filter : filters) {
                if (filter.apply(call)) {
                    return false;
                }
            }
            return true;
        }

    };

    /**
     * Returns true if the filters apply to the call in this filter mode.
     *
     * @param call the call to apply each of the filters on
     * @param filters the filters to apply
     * @return true if this mode applies to the filters, false otherwise
     */
    public abstract boolean apply(IpcCall call, Iterable<Predicate<IpcCall>> filters);

}
