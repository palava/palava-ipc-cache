package de.cosmocode.palava.ipc.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.cache.CacheService;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

/**
 * Default {@link CommandCacheService} implementation.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class DefaultCommandCacheService implements CommandCacheService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCommandCacheService.class);

    private final CacheService service;

    private CacheKeyFactory factory;
    
    @Inject
    public DefaultCommandCacheService(@CommandCache CacheService service) {
        this.service = Preconditions.checkNotNull(service, "Service");
    }
    
    @Inject(optional = true)
    @Override
    public void setFactory(CacheKeyFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }
    
    @Override
    public Map<String, Object> cache(IpcCall call, IpcCommand command, IpcCallFilterChain chain, CachePolicy policy)
        throws IpcCommandExecutionException {
        Preconditions.checkNotNull(call, "Call");
        Preconditions.checkNotNull(command, "Command");
        Preconditions.checkNotNull(chain, "Chain");
        Preconditions.checkNotNull(policy, "Policy");
        
        final Serializable key = create(call, command, policy);
        final Map<String, Object> cached = service.read(key);
        
        if (cached == null) {
            final Map<String, Object> content = chain.filter(call, command);
            LOG.trace("Caching content for {} using policy {}", command, policy);
            service.store(key, content);
            return content;
        } else {
            return cached;
        }
    }

    @Override
    public Map<String, Object> cache(IpcCall call, IpcCommand command, IpcCallFilterChain chain, CachePolicy policy,
        long maxAge, TimeUnit maxAgeUnit) throws IpcCommandExecutionException {
        Preconditions.checkNotNull(call, "Call");
        Preconditions.checkNotNull(command, "Command");
        Preconditions.checkNotNull(chain, "Chain");
        Preconditions.checkNotNull(policy, "Policy");
        Preconditions.checkArgument(maxAge >= 0, "MaxAge must not be negative");
        Preconditions.checkNotNull(maxAgeUnit, "MaxAgeUnit");

        if (maxAge == 0) {
            return cache(call, command, chain, policy);
        } else {
            final Serializable key = create(call, command, policy);
            final Map<String, Object> cached = service.read(key);
            
            if (cached == null) {
                final Map<String, Object> content = chain.filter(call, command);
                LOG.trace("Caching content for {} using policy {}", command, policy);
                service.store(key, content, maxAge, maxAgeUnit);
                return content;
            } else {
                return cached;
            }
        }
    }
    
    private Serializable create(IpcCall call, IpcCommand command, CachePolicy policy) {
        if (policy == CachePolicy.SMART && factory != null) {
            return factory.create(call, command);
        } else {
            return policy.create(call, command);
        }
    }

}
