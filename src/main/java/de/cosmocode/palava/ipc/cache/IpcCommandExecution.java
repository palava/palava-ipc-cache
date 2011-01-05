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
import java.util.concurrent.Callable;

import de.cosmocode.palava.ipc.IpcCommandExecutionException;

/**
 * A sub-interface of {@link Callable} to narrow down the return value and
 * exception clause of {@link #call()}.
 *
 * @since 3.0
 * @author Willi Schoenborn
 */
public interface IpcCommandExecution extends Callable<Map<String, Object>> {

    @Override
    Map<String, Object> call() throws IpcCommandExecutionException;
    
}
