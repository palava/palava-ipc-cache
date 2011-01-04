package de.cosmocode.palava.ipc.cache.analyzer;

import com.google.common.base.Predicate;
import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.core.Framework;
import de.cosmocode.palava.core.Palava;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.cache.CacheDecision;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Simple test for {@link de.cosmocode.palava.ipc.cache.analyzer.TimeCacheAnalyzer}.
 * </p>
 * <p>
 * Created on: 04.01.11
 * </p>
 *
 * @author Oliver Lorenz
 */
public class CaseCacheAnalyzerNoFiltersTest implements UnitProvider<CaseCacheAnalyzer> {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Predicate<IpcCall>>[] EMPTY_FILTERS = new Class[] {};

    private final Framework framework = Palava.newFramework();

    @Before
    public final void startPalava() {
        framework.start();
    }

    @After
    public final void stopPalava() {
        framework.stop();
    }

    @Override
    public final CaseCacheAnalyzer unit() {
        return framework.getInstance(CaseCacheAnalyzer.class);
    }

    @Test
    public void analyze() {
        // create dummy command and call (should not be parsed by analyzer)
        final IpcCommand command = EasyMock.createMock("command", IpcCommand.class);
        final IpcCall call = EasyMock.createMock("call", IpcCall.class);
        EasyMock.replay(command, call);

        // annotation
        final CaseCached annotation = EasyMock.createMock(CaseCached.class);
        EasyMock.expect(annotation.filterMode()).andStubReturn(CaseCacheMode.ANY);
        EasyMock.expect(annotation.filters()).andStubReturn(EMPTY_FILTERS);

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

        EasyMock.verify(command, call, annotation);
    }

}
