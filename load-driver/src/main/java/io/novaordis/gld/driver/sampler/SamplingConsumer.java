/*
 * Copyright (c) 2015 Nova Ordis LLC
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

package io.novaordis.gld.driver.sampler;

public interface SamplingConsumer
{
    /**
     * Consumers must be fast and don't block, otherwise they'll be holding the sampling thread.
     *
     * @param samplingIntervals if multiple intervals are sent, they are guaranteed to be successive identical
     *                          intervals. The implementation should throw IllegalArgumentException if they are not.
     *
     * @exception  if something goes wrong consuming. The sampler will most likely ignore the failure and push the
     * sampling interval to other registered consumers.
     */
    void consume(SamplingInterval... samplingIntervals) throws Exception;

    /**
     * Invoked by the associated sampler. Gives the consumer a chance to stop and free up resources.
     */
    void stop();

}