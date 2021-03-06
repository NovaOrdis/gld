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

package io.novaordis.gld.driver;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.sampler.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class SingleThreadedRunner implements Runnable {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(SingleThreadedRunner.class);
    private static final boolean debug = log.isDebugEnabled();

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long singleThreadedRunnerSleepMs;
    private String name;
    private KeyStore keyStore;
    final private Thread thread;
    final private Service service;
    final private Sampler sampler;
    final private LoadStrategy loadStrategy;
    final private CyclicBarrier allSingleThreadedRunnersBarrier;

    // we want to keep this package private because we want to be able to mark the runner as "running" without
    // actually starting the thread, for testing
    volatile boolean running;

    private AtomicBoolean durationExpired;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param sampler the (already configured) sampler that samples operations and asynchronously pushes statistics
     *                to its consumers. May be null, which means we only want to generate load, but we're not interested
     *                in statistics.
     * @param keyStore may be null, if the configuration did not specify one.
     * @param durationExpired an AtomicBoolean that will externally set to "true" if the overall time allocated to the
     *                        run expired. If the run is not time-limited, the boolean will never become "true".
     *                        Cannot be null.
     * @param singleThreadedRunnerSleepMs zero or negative means never sleep
     */
    public SingleThreadedRunner(String name, Service service, LoadStrategy loadStrategy,
                                Sampler sampler, CyclicBarrier barrier, AtomicBoolean durationExpired,
                                long singleThreadedRunnerSleepMs, KeyStore keyStore) {

        if (service == null) {
            throw new IllegalArgumentException("null service");
        }

        if (barrier == null) {
            throw new IllegalArgumentException("null barrier");
        }

        if (durationExpired == null)  {
            throw new IllegalArgumentException("null duration boolean");
        }

        this.name = name;
        this.sampler = sampler;
        this.singleThreadedRunnerSleepMs = singleThreadedRunnerSleepMs;
        this.loadStrategy = loadStrategy;
        this.service = service;
        this.keyStore = keyStore;
        this.allSingleThreadedRunnersBarrier = barrier;
        this.durationExpired = durationExpired;

        thread = new Thread(this, name + " Thread");
    }

    // Runnable implementation -----------------------------------------------------------------------------------------

    public void run() {

        try {

            loopUntilStoppedOrOutOfOperationsOrDurationExpired();
        }
        catch (Exception e) {

            log.error(this + " failed", e);
        }
        finally {

            try {

                allSingleThreadedRunnersBarrier.await();
            }
            catch(Exception e) {

                e.printStackTrace();
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    public void start() {

        log.debug(this + " starting ...");

        running = true;
        thread.start();
    }

    public void stop() {

        running = false;

        log.debug(this + " stopped");
    }

    @Override
    public String toString()
    {
        return "SingleThreadedRunner[" + name + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    Sampler getSampler() {

        return sampler;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private boolean runnerIsShuttingDown() {

        return durationExpired.get();
    }

    @SuppressWarnings("ConstantConditions")
    private void loopUntilStoppedOrOutOfOperationsOrDurationExpired() throws Exception {

        long operationCounter = 0L;
        String lastSuccessfullyHandledKey = null;
        Operation lastOperation = null;

        while (running) {

            //
            // if the runner is shutting down, let the strategy know but keep spinning, as we may need
            // multiple operations to clean up the state. Let the strategy return null when it decides it issued
            // enough cleanup operations
            //

            Operation op = loadStrategy.next(lastOperation, lastSuccessfullyHandledKey, runnerIsShuttingDown());

            if (op == null) {

                //
                // the strategy ran out of keys, it's time to finish
                //

                log.debug(Thread.currentThread().getName() + " ran out of operations, " + operationCounter +
                        " operations processed by this thread, exiting");

                return;
            }

            lastOperation = op;
            operationCounter ++;

            long t1 = -1L;
            Exception ex = null;
            long t0 = System.nanoTime();
            long t0Ms = System.currentTimeMillis();

            try  {

                op.perform(service);

                if (debug) {

                    log.debug(op + " executed against " + service);
                }

                t1 = System.nanoTime();

                if (keyStore != null) {

                    //
                    // the operation was successful, which means the key was sent successfully into the service;
                    // currently we store the key locally only if the operation was successful
                    //

                    lastSuccessfullyHandledKey = op.getKey();
                    keyStore.store(lastSuccessfullyHandledKey);
                }
            }
            catch (Exception e) {

                t1 = System.nanoTime();
                ex = e;

                log.info("operation failed: " + e.getMessage(), e);
            }
            finally {

                //
                // if there is a sampler, record the operation, otherwise it means we're not interested in statistics
                //

                if (sampler != null) {

                    sampler.record(t0Ms, t0, t1, op, ex);
                }

                if (singleThreadedRunnerSleepMs > 0) {

                    try {

                        Thread.sleep(singleThreadedRunnerSleepMs);
                    }
                    catch (InterruptedException e) {

                        log.warn("interrupted while sleeping");
                    }
                }
            }
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
