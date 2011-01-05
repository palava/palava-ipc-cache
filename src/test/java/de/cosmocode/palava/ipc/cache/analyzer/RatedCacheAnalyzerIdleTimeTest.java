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

import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.cache.CacheDecision;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Tests {@link RatedCacheAnalyzer} with calculated idle time.
 * </p>
 * <p>
 * Created on: 05.01.11
 * </p>
 *
 * @author Oliver Lorenz
 */
public final class RatedCacheAnalyzerIdleTimeTest extends AbstractRatedCacheAnalyzerTest {

    private static final long TIME_PER_ARGUMENT =
        TimeUnit.MINUTES.toSeconds(RatedCachedIdleTimeTestCommand.IDLE_TIME / CountArgumentsCacheRatingAnalyzer.MAX);

    private IpcCommand command;
    private RatedCached annotation;

    /**
     * Instantiates the test command and its annotation.
     */
    @Before
    public void createTestCommand() {
        command = new RatedCachedIdleTimeTestCommand();
        annotation = command.getClass().getAnnotation(RatedCached.class);
    }

    /**
     * Tests the setup with no arguments.
     */
    @Test
    public void noArguments() {
        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(false, decision.shouldCache());
        Assert.assertEquals(RatedCachedIdleTimeTestCommand.LIFE_TIME, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getLifeTimeUnit());
        Assert.assertEquals(0L, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.SECONDS, decision.getIdleTimeUnit());
    }

    /**
     * Tests the setup with less than minimum arguments required for caching.
     */
    @Test
    public void underMinimum() {
        arguments.put("test", "argument");
        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(false, decision.shouldCache());
        Assert.assertEquals(RatedCachedIdleTimeTestCommand.LIFE_TIME, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getLifeTimeUnit());
        Assert.assertEquals(TIME_PER_ARGUMENT, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.SECONDS, decision.getIdleTimeUnit());
    }

    /**
     * Tests the setup with the exact minimum arguments required for caching.
     */
    @Test
    public void atMinimum() {
        arguments.put("test", "argument");
        arguments.put("arg2", "foo");
        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(true, decision.shouldCache());
        Assert.assertEquals(RatedCachedIdleTimeTestCommand.LIFE_TIME, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getLifeTimeUnit());
        Assert.assertEquals(2 * TIME_PER_ARGUMENT, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.SECONDS, decision.getIdleTimeUnit());
    }

    /**
     * Tests the setup with more than the minimum arguments required for caching.
     */
    @Test
    public void overMinimum() {
        arguments.put("key1", "value1");
        arguments.put("key2", "value2");
        arguments.put("key3", "value3");
        arguments.put("key4", "value4");
        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(true, decision.shouldCache());
        Assert.assertEquals(RatedCachedIdleTimeTestCommand.LIFE_TIME, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getLifeTimeUnit());
        Assert.assertEquals(4 * TIME_PER_ARGUMENT, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.SECONDS, decision.getIdleTimeUnit());
    }

    /**
     * Tests the setup with the maximum number of possible arguments.
     */
    @Test
    public void maxArguments() {
        for (int i = 0; i < CountArgumentsCacheRatingAnalyzer.MAX; i++) {
            arguments.put("key" + i, "value" + i);
        }

        final long idleTimeInSeconds = TimeUnit.MINUTES.toSeconds(RatedCachedIdleTimeTestCommand.IDLE_TIME);

        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(true, decision.shouldCache());
        Assert.assertEquals(RatedCachedIdleTimeTestCommand.LIFE_TIME, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getLifeTimeUnit());
        Assert.assertEquals(idleTimeInSeconds, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.SECONDS, decision.getIdleTimeUnit());
    }

}