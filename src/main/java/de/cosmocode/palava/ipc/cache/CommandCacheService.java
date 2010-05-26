package de.cosmocode.palava.ipc.cache;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

/**
 * A service which encapsulates caching {@link IpcCommand} results
 * produced by {@link IpcCommand#execute(IpcCall, Map)}.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
interface CommandCacheService {

    /**
     * Sets the {@link CacheKeyFactory} of this service.
     * 
     * @since 2.0
     * @param factory the new factory
     * @throws NullPointerException if factory is null
     */
    void setFactory(CacheKeyFactory factory);
    
    /**
     * Eventually caches the content produces by the given chain or returns an already
     * cached version.
     * 
     * @since 2.0
     * @param call the incoming call
     * @param command the command being used to execute
     * @param chain the filter chain
     * @param policy the cache policy
     * @return the cached content
     * @throws IpcCommandExecutionException if chain execution failed
     */
    Map<String, Object> cache(IpcCall call, IpcCommand command, IpcCallFilterChain chain,
        CachePolicy policy) throws IpcCommandExecutionException;
    
    /**
     * Eventually caches the content produces by the given chain or returns an already
     * cached version.
     * 
     * @since 2.0
     * @param call the incoming call
     * @param command the command being used to execute
     * @param chain the filter chain
     * @param policy the cache policy
     * @param maxAge the max age of the content's cache period
     * @param maxAgeUnit the unit of maxAge
     * @return the cached content
     * @throws IpcCommandExecutionException if chain execution failed
     */
    Map<String, Object> cache(IpcCall call, IpcCommand command, IpcCallFilterChain chain,
        CachePolicy policy, long maxAge, TimeUnit maxAgeUnit) throws IpcCommandExecutionException;
    
}
