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
import de.cosmocode.palava.core.Framework;
import de.cosmocode.palava.core.Palava;
import de.cosmocode.palava.core.lifecycle.Startable;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.cache.CacheDecision;
import de.cosmocode.palava.ipc.cache.CacheKeyFactory;
import de.cosmocode.palava.ipc.cache.DefaultCacheKeyFactory;
import de.cosmocode.palava.ipc.cache.IpcCacheTestModule;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Simple test for {@link de.cosmocode.palava.ipc.cache.analyzer.CaseCacheAnalyzer}
 * that only tests the time related methods.
 * </p>
 * <p>
 * Created on: 04.01.11
 * </p>
 *
 * @since 3.0
 * @author Oliver Lorenz
 */
public final class CaseCacheAnalyzerTimeTest implements UnitProvider<CaseCacheAnalyzer>, Startable {

    @SuppressWarnings("unchecked")
    private static final Class<? extends CachePredicate>[] ALWAYS_TRUE_FILTERS =
        new Class[] {AlwaysTruePredicate.class};

    private final Framework framework = Palava.newFramework(new IpcCacheTestModule(), new Properties());

    @Before
    @Override
    public void start() {
        framework.start();
    }

    @After
    @Override
    public void stop() {
        framework.stop();
    }

    @Override
    public CaseCacheAnalyzer unit() {
        return framework.getInstance(CaseCacheAnalyzer.class);
    }

    /**
     * Tests {@link CaseCacheAnalyzer#analyze(java.lang.annotation.Annotation, IpcCall, IpcCommand)}.
     */
    @Test
    public void analyze() {
        // create dummy command and call (should not be parsed by analyzer)
        final IpcCommand command = EasyMock.createMock("command", IpcCommand.class);
        final IpcCall call = EasyMock.createMock("call", IpcCall.class);
        EasyMock.replay(command, call);

        // annotation
        final CaseCached annotation = new CaseCached() {

            @Override
            public Class<? extends CachePredicate>[] predicates() {
                return ALWAYS_TRUE_FILTERS;
            }

            @Override
            public CaseCacheMode mode() {
                return CaseCacheMode.ANY;
            }

            @Override
            public long lifeTime() {
                return 2;
            }

            @Override
            public TimeUnit lifeTimeUnit() {
                return TimeUnit.HOURS;
            }

            @Override
            public long idleTime() {
                return 20;
            }

            @Override
            public TimeUnit idleTimeUnit() {
                return TimeUnit.MINUTES;
            }

            @Override
            public Class<? extends CacheKeyFactory> keyFactory() {
                return DefaultCacheKeyFactory.class;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return CaseCached.class;
            }

        };

        final CacheDecision decision = unit().analyze(annotation, call, command);

        Assert.assertEquals(true, decision.shouldCache());
        Assert.assertEquals(2L, decision.getLifeTime());
        Assert.assertEquals(TimeUnit.HOURS, decision.getLifeTimeUnit());
        Assert.assertEquals(20L, decision.getIdleTime());
        Assert.assertEquals(TimeUnit.MINUTES, decision.getIdleTimeUnit());

        EasyMock.verify(command, call);
    }

}
