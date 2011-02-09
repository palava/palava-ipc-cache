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

import java.lang.annotation.Annotation;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;

/**
 * Provides a "type-safe" implementation to analyze the command call with your cache annotation.
 *
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 * @param <T> generic annotation type
 */
public abstract class AbstractCacheAnalyzer<T extends Annotation> implements CacheAnalyzer {

    /**
     * Will be called to get a decision about how to handle a result.
     * The method will get the cache annotation which triggered the analyzer.
     *
     * @param annotation your annotation
     * @param call the IPC call
     * @param command the command
     * @return your decision
     * @see CacheAnalyzer#analyze(Annotation, IpcCall, IpcCommand)
     */
    protected abstract CacheDecision decide(T annotation, IpcCall call, IpcCommand command);

    @Override
    @SuppressWarnings("unchecked")
    public CacheDecision analyze(Annotation annotation, IpcCall call, IpcCommand command) {
        return decide((T) annotation, call, command);
    }

}
