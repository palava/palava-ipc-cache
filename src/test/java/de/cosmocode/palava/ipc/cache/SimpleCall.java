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

/**
 * 
 */
package de.cosmocode.palava.ipc.cache;

import java.util.Map;

import com.google.common.collect.Maps;

import de.cosmocode.collections.utility.Utility;
import de.cosmocode.collections.utility.UtilityMap;
import de.cosmocode.collections.utility.UtilitySet;
import de.cosmocode.palava.ipc.AbstractIpcArguments;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcConnection;
import de.cosmocode.palava.scope.AbstractScopeContext;

/**
 * A simple {@link IpcCall} that saves the given command, request
 * and the Strings as arguments,
 * taking every other argument as the value to the previous argument.
 * 
 * @author Oliver Lorenz
 */
final class SimpleCall extends AbstractScopeContext implements IpcCall {
    
    private final IpcArguments arguments;
    private final IpcConnection request;
    private final Map<Object, Object> context = Maps.newHashMap();
    
    /**
     * Saves the given connection as HttpRequest.
     * The Strings are taken as arguments,
     * treating every other argument as the value to the previous argument.
     * @param connection the connection this call lives in
     * @param arguments the ipc arguments for this call, given as: key, value, key, value, ...
     */
    public SimpleCall(final IpcConnection connection, final String... arguments) {
        this.request = connection;
        if (arguments.length > 0) {
            this.arguments = new SimpleArguments(arguments);
        } else {
            this.arguments = new SimpleArguments();
        }
    }

    @Override
    public IpcArguments getArguments() {
        return arguments;
    }
    
    @Override
    public IpcConnection getConnection() {
        return request;
    }

    @Override
    protected Map<Object, Object> context() {
        return context;
    }
    
    /**
     * The backing Arguments class for the SimpleCall.
     * 
     * @author Oliver Lorenz
     */
    private static final class SimpleArguments extends AbstractIpcArguments {
        
        private final UtilityMap<String, Object> forwarded;
        
        public SimpleArguments(final String... arguments) {
            final Map<String, Object> map = Maps.newHashMap();
            String prevArg = null;
            for (final String arg : arguments) {
                if (prevArg != null) {
                    map.put(prevArg, arg);
                    prevArg = null;
                } else {
                    prevArg = arg;
                }
            }
            this.forwarded = Utility.asUtilityMap(map);
        }

        @Override
        public UtilitySet<Entry<String, Object>> entrySet() {
            return forwarded.entrySet();
        }

        @Override
        public Object put(String key, Object value) {
            return forwarded.put(key, value);
        }
        
    }
    
}
