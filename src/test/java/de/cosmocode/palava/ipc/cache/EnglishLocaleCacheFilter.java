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

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * A call predicate that returns true on apply if the call has the english locale set
 * in any of its scopes (call, connection or session).
 */
final class EnglishLocaleCacheFilter implements Predicate<IpcCall> {

    @Override
    public boolean apply(@Nullable IpcCall input) {
        // tests whether any of: call, connection or session has the language "english" set
        return input != null &&
            (input.get("lang") == Locale.ENGLISH ||
            input.getConnection().get("lang") == Locale.ENGLISH ||
            input.getConnection().getSession().get("lang") == Locale.ENGLISH);
    }

}
