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

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;

import java.lang.annotation.Annotation;

/**
 * Defines the implementation of an analyzer which calculates a decision about the cache behaviour
 * of the given command call.
 *
 * @author Tobias Sarnowski
 * @author Oliver Lorenz
 * @since 3.0
 * @see AbstractCacheAnalyzer
 * @see CacheDecision
 * @see ComplexCacheAnnotation
 */
public interface CacheAnalyzer {

    /**
     * Will be called to get a decision about how to handle a result. The method will get
     * cache annotation which triggered the analyzer.
     *
     * @param annotation the triggering cache annotation (your own)
     * @param call the IPC call
     * @param command the called command
     * @return the calculated decision
     */
    CacheDecision analyze(Annotation annotation, IpcCall call, IpcCommand command);

}
