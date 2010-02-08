/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * 
 */
package de.cosmocode.palava.services.cache.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;

import de.cosmocode.collections.utility.ForwardingUtilityMap;
import de.cosmocode.collections.utility.Utility;
import de.cosmocode.collections.utility.UtilityMap;
import de.cosmocode.palava.bridge.ConnectionLostException;
import de.cosmocode.palava.bridge.Header;
import de.cosmocode.palava.bridge.call.Arguments;
import de.cosmocode.palava.bridge.call.Call;
import de.cosmocode.palava.bridge.call.MissingArgumentException;
import de.cosmocode.palava.bridge.command.Command;
import de.cosmocode.palava.bridge.request.HttpRequest;
import de.cosmocode.palava.ipc.IpcConnection;
import de.cosmocode.palava.ipc.IpcSession;

/**
 * A simple {@link Call} that saves the given command, request
 * and the Strings as arguments,
 * taking every other argument as the value to the previous argument.
 * 
 * @author Oliver Lorenz
 */
final class SimpleCall implements Call {
    
    private final Arguments arguments;
    private final Command command;
    private final HttpRequest request;
    
    /**
     * Saves the given command and request.
     * The Strings are taken as arguments,
     * treating every other argument as the value to the previous argument.
     * @param command
     * @param request
     * @param arguments
     */
    public SimpleCall(final Command command, final HttpRequest request, final String... arguments) {
        this.command = command;
        this.request = request;
        if (arguments.length > 0) {
            this.arguments = new SimpleArguments(arguments);
        } else {
            this.arguments = null;
        }
    }

    @Override
    public Arguments getArguments() {
        return arguments;
    }

    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public Header getHeader() {
        return null;
    }

    @Override
    public HttpRequest getHttpRequest() {
        return request;
    }
    
    @Override
    public IpcConnection getConnection() {
        return getHttpRequest();
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }
    
    @Override
    public <K> boolean contains(K key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <K, V> V get(K key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UnmodifiableIterator<Entry<Object, Object>> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K, V> void putAll(Map<? extends K, ? extends V> map) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public <K, V> V remove(K key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K, V> void set(K key, V value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void discard() throws ConnectionLostException, IOException {
    }
    
    /**
     * The backing Arguments class for the SimpleCall.
     * 
     * @author Oliver Lorenz
     */
    private static final class SimpleArguments extends ForwardingUtilityMap<String, Object> implements Arguments {
        
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
        public void require(final String... keys) throws MissingArgumentException {
            for (final String key : keys) {
                if (forwarded.containsKey(key)) continue;
                throw new MissingArgumentException(key);
            }
        }
        
        @Override
        protected UtilityMap<String, Object> delegate() {
            return forwarded;
        }
        
    }
    
}
