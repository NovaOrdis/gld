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

package com.novaordis.gld.sampler;

import com.novaordis.gld.Operation;

/**
 * A fixed-interval sampler. Once started, the sampler will continuously and regularly produce samples each
 * <b>samplingIntervalMs</b>milliseconds and send them to all registered SampleConsumers.
 *
 * Usage pattern:
 *
 * 1. Instantiate the sampler instance.
 *
 *      Sampler s = new SamplerImpl();
 *
 * 2. Register all operation types this sampler is supposed to sample.
 *
 *      s.registerOperation(Send.class);
 *      s.registerOperation(Receive.class);
 *
 * 3. Register all sample consumers.
 *
 *      s.registerConsumer(...);
 *
 * 4. Configure the sampling interval:
 *
 *      s.setSamplingIntervalMs(...);
 *
 * 5. Start the sampler.
 *
 *      s.start();
 *
 * 6. Send operations into it:
 *
 *      s.record(...)
 *
 * Note that if the sampler is asked to record an operation it was not configured with, it will thrown an
 * IllegalArgumentException.
 *
 * 7. Stop the sampler.
 *
 *      s.stop();
 *
 * After stopping the sampler, all operation registrations are lost.
 */
public interface Sampler
{
    /**
     * Starts the sampler. Once started, the sampler will continuously and regularly produce samples each
     * <b>samplingIntervalMs</b>milliseconds and send them to all registered SampleConsumers. It's usually a good
     * idea to register all consumers before calling start.
     *
     * The operation should be idempotent - once started, subsequent invocations should be noops.
     *
     * @exception java.lang.IllegalStateException if the start() is invoked without any operation registered.
     */
    void start();

    boolean isStarted();

    /**
     * Stops the sampler. Once stopped, record() will throw IllegalStateException and registered consumers will stop
     * receiving samples. It is up to the implementation whether will accept re-starting or not, the implementation
     * should make that clear.
     *
     * After stopping the sampler, all operation registrations are lost.
     *
     * stop() is guaranteed to allow for one more full sampling run after it was called and to trigger generation of
     * a final sampling interval that will contain all events recorded from the same thread that called stop().
     *
     * @see Sampler#record(long, long, long, Operation, java.lang.Throwable...)
     */
    void stop();

    /**
     * Sets the sampling interval. Implementations may accept or not changing the sampling interval after the
     * sampler was started. The documentation should describe the behavior.
     *
     * Note that the sampling task run interval *must* be smaller than the sampling interval, otherwise we cannot
     * generate samples at the required resolution.
     *
     * @see Sampler#setSamplingTaskRunIntervalMs(long);
     */
    void setSamplingIntervalMs(long ms);

    long getSamplingIntervalMs();

    /**
     * Sets the sampling task run interval. Implementations may accept or not changing the sampling interval after the
     * sampler was started. The documentation should describe the behavior.
     *
     * Note that the sampling task run interval *must* be smaller than the sampling interval, otherwise we cannot
     * generate samples at the required resolution.
     *
     * @see Sampler#setSamplingIntervalMs(long);
     */
    void setSamplingTaskRunIntervalMs(long ms);

    long getSamplingTaskRunIntervalMs();

    /**
     * Registers an operation to sample. Operations cannot be registered after the sampler was started. The idea
     * behind registering operations in advance (and not collecting operation types as they show up) is that we
     * speed up the recording code by not having to synchronize on the counter storage to extend it, and also makes
     * the samples generated by the sampler more predictable.
     *
     * @exception IllegalStateException if attempting to register an operation after the sampler was started.
     * @exception IllegalArgumentException on illegal operation type.
     */
    Counter registerOperation(Class<? extends Operation> operationType);

    /**
     * May return null.
     */
    Counter getCounter(Class<? extends Operation> operationType);

    /**
     * @return whether consumer was successfully added or not.
     */
    boolean registerConsumer(SamplingConsumer consumer);

    /**
     * Presents the operation to the sampler, giving it a chance to update its internal statistics.
     *
     * Important: this method will be presumably called concurrently from a large number of threads so it is important
     * to be very fast and not contend on shared resources unnecessarily.
     *
     * @param t0Ms - the time (in milliseconds) when the operation that is being recorded started.
     * @param t0Nano - the time (in nanoseconds) when the operation that is being recorded started. Logically, it
     *        should be the same as t0Ms, but Java documentation advises against using nano-second precision time
     *        to get absolute time information, so we are only using it to calculate delta in conjunction with 't1Nano'.
     * @param t1Nano - the time (in nanoseconds) when the operation that is being recorded ended. Java documentation
     *        advises against using nano-second precision time to get absolute time information, so we are only using
     *        this value to calculate delta in conjunction with 't0Nano'.
     * @param t - optionally a Throwable associated with the operation. Actually we only expect one or none exception
     *          instances to be passed, the rest will be ignored.
     *
     * @exception IllegalStateException if called upon a stopped sampler.
     * @exception IllegalArgumentException for operations of unknown type.
     *
     * @see Sampler#registerOperation(Class)
     */
    void record(long t0Ms, long t0Nano, long t1Nano, Operation op, Throwable... t);


    /**
     * Annotate the statistics, using the current time stamp.
     */
    void annotate(String line);

}