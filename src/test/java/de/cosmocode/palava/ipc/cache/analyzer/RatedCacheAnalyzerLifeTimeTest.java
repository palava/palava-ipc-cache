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

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.MapIpcArguments;
import de.cosmocode.palava.ipc.cache.CacheDecision;

/**
 * <p>
 * Tests {@link RatedCacheAnalyzer} with calculated life time.
 * </p>
 * <p>
 * Created on: 05.01.11
 * </p>
 *
 * @author Oliver Lorenz
 */
public final class RatedCacheAnalyzerLifeTimeTest extends AbstractRatedCacheAnalyzerTest {

    private static final long TIME_PER_ARGUMENT =
        TimeUnit.MINUTES.toSeconds(RatedCachedLifeTimeTestCommand.LIFE_TIME / CountArgumentsCacheRatingAnalyzer.MAX);

    private IpcCommand command;
    private RatedCached annotation;

    /**
     * Instantiates the test command and its annotation.
     */
    @Before
    public void createTestCommand() {
        command = new RatedCachedLifeTimeTestCommand();
        annotation = command.getClass().getAnnotation(RatedCached.class);
    }

    /**
     * Tests the setup with no arguments.
     */
    @Test
    public void noArguments() {
        final IpcArguments arguments = new MapIpcArguments();
        final IpcCall call = createCallMock(arguments);
        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(false, decision.shouldCache());
        Assert.assertEquals(0L, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.SECONDS, decision.getLifeTimeUnit());
        Assert.assertEquals(RatedCachedLifeTimeTestCommand.IDLE_TIME, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getIdleTimeUnit());
    }

    /**
     * Tests the setup with less than minimum arguments required for caching.
     */
    @Test
    public void underMinimum() {
        final IpcArguments arguments = new MapIpcArguments();
        arguments.put("test", "argument");
        final IpcCall call = createCallMock(arguments);

        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(false, decision.shouldCache());
        Assert.assertEquals(TIME_PER_ARGUMENT, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.SECONDS, decision.getLifeTimeUnit());
        Assert.assertEquals(RatedCachedLifeTimeTestCommand.IDLE_TIME, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getIdleTimeUnit());
    }

    /**
     * Tests the setup with the exact minimum arguments required for caching.
     */
    @Test
    public void atMinimum() {
        final IpcArguments arguments = new MapIpcArguments();
        arguments.put("test", "argument");
        arguments.put("arg2", "foobar");
        final IpcCall call = createCallMock(arguments);

        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(true, decision.shouldCache());
        Assert.assertEquals(2 * TIME_PER_ARGUMENT, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.SECONDS, decision.getLifeTimeUnit());
        Assert.assertEquals(RatedCachedLifeTimeTestCommand.IDLE_TIME, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getIdleTimeUnit());
    }

    /**
     * Tests the setup with more than the minimum arguments required for caching.
     */
    @Test
    public void overMinimum() {
        final IpcArguments arguments = new MapIpcArguments();
        arguments.put("key1", "value1");
        arguments.put("key2", "value2");
        arguments.put("key3", "value3");
        arguments.put("key4", "value4");
        final IpcCall call = createCallMock(arguments);

        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(true, decision.shouldCache());
        Assert.assertEquals(4 * TIME_PER_ARGUMENT, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.SECONDS, decision.getLifeTimeUnit());
        Assert.assertEquals(RatedCachedLifeTimeTestCommand.IDLE_TIME, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getIdleTimeUnit());
    }

    /**
     * Tests the setup with the maximum number of possible arguments.
     */
    @Test
    public void maxArguments() {
        final IpcArguments arguments = new MapIpcArguments();
        for (int i = 0; i < CountArgumentsCacheRatingAnalyzer.MAX; i++) {
            arguments.put("key" + i, "value" + i);
        }
        final IpcCall call = createCallMock(arguments);

        final long lifeTimeInSeconds = TimeUnit.MINUTES.toSeconds(RatedCachedLifeTimeTestCommand.LIFE_TIME);

        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(true, decision.shouldCache());
        Assert.assertEquals(lifeTimeInSeconds, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.SECONDS, decision.getLifeTimeUnit());
        Assert.assertEquals(RatedCachedLifeTimeTestCommand.IDLE_TIME, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getIdleTimeUnit());
    }

}
