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

import com.google.common.base.Predicate;
import de.cosmocode.palava.ipc.IpcCall;

import javax.annotation.Nullable;

/**
 * <p></p>
 * <p>
 * Created on: 04.01.11
 * </p>
 *
 * @author Oliver Lorenz
 */
final class AccountIdIsFiveFilter implements Predicate<IpcCall> {

    @Override
    public boolean apply(@Nullable IpcCall input) {
        if (input == null) {
            return false;
        } else {
            final Object accountId = input.getArguments().get("account_id");
            return accountId instanceof Integer && Integer.class.cast(accountId) == 5;
        }
    }

}
