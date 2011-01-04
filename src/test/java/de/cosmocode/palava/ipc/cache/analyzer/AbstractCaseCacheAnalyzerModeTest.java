package de.cosmocode.palava.ipc.cache.analyzer;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.core.Framework;
import de.cosmocode.palava.core.Palava;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.MapIpcArguments;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Abstract test class for testing filtering with different modes on {@link CaseCacheAnalyzer}.
 * </p>
 * <p>
 * Created on: 04.01.11
 * </p>
 *
 * @author Oliver Lorenz
 */
public abstract class AbstractCaseCacheAnalyzerModeTest implements UnitProvider<CaseCacheAnalyzer> {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Predicate<IpcCall>>[] ALL_FILTERS =
        new Class[] {HasDateFilter.class, AccountIdIsFiveFilter.class};

    private final Framework framework = Palava.newFramework();

    protected CaseCached annotation;
    protected IpcArguments arguments = new MapIpcArguments(Maps.<String, Object>newHashMap());
    protected IpcCall call;
    protected IpcCommand command;

    /**
     * Returns the mode that is to be tested.
     * @return the mode to be tested, returned on annotation.filterMode()
     */
    protected abstract CaseCacheMode mode();

    @Before
    public final void startPalava() {
        framework.start();
    }

    @Before
    public void createAnnotationMock() {
        annotation = EasyMock.createMock(CaseCached.class);

        EasyMock.expect(annotation.filterMode()).andReturn(mode()).atLeastOnce();
        EasyMock.expect(annotation.filters()).andReturn(ALL_FILTERS).atLeastOnce();

        EasyMock.expect(annotation.lifeTime()).andStubReturn(0L);
        EasyMock.expect(annotation.lifeTimeUnit()).andStubReturn(TimeUnit.MINUTES);
        EasyMock.expect(annotation.idleTime()).andStubReturn(0L);
        EasyMock.expect(annotation.idleTimeUnit()).andStubReturn(TimeUnit.MINUTES);
        EasyMock.replay(annotation);
    }

    @Before
    public void mockCommand() {
        command = EasyMock.createMock(IpcCommand.class);
        EasyMock.replay(command);
    }

    @Before
    public void mockCall() {
        call = EasyMock.createMock("call", IpcCall.class);
        EasyMock.expect(call.getArguments()).andReturn(arguments).atLeastOnce();
        EasyMock.replay(call);
    }

    @After
    public void verifyMocks() {
        EasyMock.verify(annotation, call, command);
    }

    @After
    public final void stopPalava() {
        framework.stop();
    }

    @Override
    public final CaseCacheAnalyzer unit() {
        return framework.getInstance(CaseCacheAnalyzer.class);
    }

}
