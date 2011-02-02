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

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.ipc.cache.analyzer.TimeCached;

/**
 * Simple command that calculates sum, product and exponentiation of two integers.
 *
 * @author Oliver Lorenz
 */

@Singleton
@TimeCached
@IpcCommand.Description("Calculates sum, product and exponentiation of two int values")
final class Calculate implements IpcCommand {

    private static final Logger LOG = LoggerFactory.getLogger(Calculate.class);

    private boolean calledOnce;

    @Override
    public void execute(final IpcCall call, final Map<String, Object> result) throws IpcCommandExecutionException {
        if (calledOnce) {
            Assert.fail("I was called before!");
        }

        // read arguments
        final IpcArguments arguments = call.getArguments();
        final int a = arguments.getInt("a");
        final int b = arguments.getInt("b");

        result.put("sum", a + b);
        result.put("product", a * b);
        result.put("exponentiation", (int) Math.pow(a, b));
        LOG.trace("Calculated sum, product and exponentiation of {} and {}", a, b);

        calledOnce = true;
    }
}
