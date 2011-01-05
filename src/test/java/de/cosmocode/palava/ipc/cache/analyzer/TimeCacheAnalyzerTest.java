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

import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.cache.CacheDecision;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Simple test for {@link TimeCacheAnalyzer}.
 * </p>
 * <p>
 * Created on: 04.01.11
 * </p>
 *
 * @since 3.0
 * @author Oliver Lorenz
 */
public final class TimeCacheAnalyzerTest implements UnitProvider<TimeCacheAnalyzer> {

    @Override
    public TimeCacheAnalyzer unit() {
        return new TimeCacheAnalyzer();
    }

    @Test
    public void analyze() {
        // create dummy call and command (should not be parsed by analyzer)
        final IpcCommand command = EasyMock.createMock("command", IpcCommand.class);
        final IpcCall call = EasyMock.createMock("call", IpcCall.class);
        EasyMock.replay(command, call);

        // annotation
        final TimeCached annotation = EasyMock.createMock(TimeCached.class);
        EasyMock.expect(annotation.lifeTime()).andReturn(2L);
        EasyMock.expect(annotation.lifeTimeUnit()).andReturn(TimeUnit.HOURS);
        EasyMock.expect(annotation.idleTime()).andReturn(20L);
        EasyMock.expect(annotation.idleTimeUnit()).andReturn(TimeUnit.MINUTES);
        EasyMock.replay(annotation);

        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(true, decision.shouldCache());
        Assert.assertEquals(2L, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.HOURS, decision.getLifeTimeUnit());
        Assert.assertEquals(20L, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getIdleTimeUnit());
        EasyMock.verify(annotation);
    }

}
