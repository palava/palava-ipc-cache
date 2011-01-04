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
