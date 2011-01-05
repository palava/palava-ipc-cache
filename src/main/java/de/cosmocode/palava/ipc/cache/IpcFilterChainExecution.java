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

package de.cosmocode.palava.ipc.cache;

import java.util.Map;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

/**
 * A simple {@link IpcCommandExecution} which delegates to
 * {@link IpcCallFilterChain#filter(IpcCall, IpcCommand)} of the given chain.
 *
 * @since 3.0
 * @author Willi Schoenborn
 */
final class IpcFilterChainExecution implements IpcCommandExecution {
    
    private final IpcCommand command;
    private final IpcCallFilterChain chain;
    private final IpcCall call;

    IpcFilterChainExecution(IpcCall call, IpcCommand command, IpcCallFilterChain chain) {
        this.command = command;
        this.chain = chain;
        this.call = call;
    }

    @Override
    public Map<String, Object> call() throws IpcCommandExecutionException {
        return chain.filter(call, command);
    }
    
}
