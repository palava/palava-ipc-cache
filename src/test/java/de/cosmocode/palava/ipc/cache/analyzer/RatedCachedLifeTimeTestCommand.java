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

package de.cosmocode.palava.ipc.cache.analyzer;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * A test command that has the {@link RatedCached} annotation with target set to
 * {@link de.cosmocode.palava.ipc.cache.analyzer.RatedCached.RatingTarget#LIFE_TIME}.
 * </p>
 * <p>
 * Created on: 05.01.11
 * </p>
 *
 * @author Oliver Lorenz
 */
@RatedCached(
    analyzer = CountArgumentsCacheRatingAnalyzer.class,
    minTime = 6, minTimeUnit = TimeUnit.MINUTES,
    lifeTime = RatedCachedLifeTimeTestCommand.LIFE_TIME, lifeTimeUnit = TimeUnit.MINUTES,
    idleTime = RatedCachedLifeTimeTestCommand.IDLE_TIME, idleTimeUnit = TimeUnit.MINUTES,
    target = RatedCached.RatingTarget.LIFE_TIME
)
final class RatedCachedLifeTimeTestCommand implements IpcCommand {

    public static final long LIFE_TIME = 60L;
    public static final long IDLE_TIME = 5L;

    @Override
    public void execute(IpcCall call, Map<String, Object> result) throws IpcCommandExecutionException {
        // not executed, nothing to do
    }

}
