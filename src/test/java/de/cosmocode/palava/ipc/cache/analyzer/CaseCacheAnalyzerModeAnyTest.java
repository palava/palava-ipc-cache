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

import java.util.Date;

import org.junit.Assert;

import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.MapIpcArguments;
import de.cosmocode.palava.ipc.cache.CacheDecision;

/**
 * <p>
 * Tests {@link CaseCacheMode#ANY} for {@link CaseCacheAnalyzer}.
 * </p>
 * <p>
 * Created on: 04.01.11
 * </p>
 *
 * @since 3.0
 * @author Oliver Lorenz
 */
public final class CaseCacheAnalyzerModeAnyTest extends AbstractCaseCacheAnalyzerModeTest {

    @Override
    protected CaseCacheMode mode() {
        return CaseCacheMode.ANY;
    }

    @Override
    public void dateMatches() {
        final IpcArguments arguments = new MapIpcArguments(); 
        arguments.put("date", new Date());
        final IpcCall call = createCallMock(arguments);
        final CacheDecision decision = unit().analyze(annotation(), call, command());
        Assert.assertTrue(decision.shouldCache());
    }

    @Override
    public void accountIdMatches() {
        final IpcArguments arguments = new MapIpcArguments(); 
        arguments.put("account_id", 5);
        final IpcCall call = createCallMock(arguments);
        final CacheDecision decision = unit().analyze(annotation(), call, command());
        Assert.assertTrue(decision.shouldCache());
    }

    @Override
    public void bothMatch() {
        final IpcArguments arguments = new MapIpcArguments(); 
        arguments.put("account_id", 5);
        arguments.put("date", new Date());
        final IpcCall call = createCallMock(arguments);
        final CacheDecision decision = unit().analyze(annotation(), call, command());
        Assert.assertTrue(decision.shouldCache());
    }

    @Override
    public void noneMatches() {
        final IpcArguments arguments = new MapIpcArguments(); 
        arguments.put("anything", "bla");
        final IpcCall call = createCallMock(arguments);
        final CacheDecision decision = unit().analyze(annotation(), call, command());
        Assert.assertFalse(decision.shouldCache());
    }

}
