package de.cosmocode.palava.ipc.cache.analyzer;

import com.google.common.base.Predicate;
import de.cosmocode.palava.ipc.IpcCall;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * <p>
 * Simple predicate that tests whether a call has a Date argument.
 * </p>
 * <p>
 * Created on: 04.01.11
 * </p>
 *
 * @author Oliver Lorenz
 */
final class HasDateFilter implements Predicate<IpcCall> {

    @Override
    public boolean apply(@Nullable IpcCall input) {
        return input != null && input.getArguments().get("date") instanceof Date;
    }

}
