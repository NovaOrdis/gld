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
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.driver.console.CommandLineConsole;
import io.novaordis.gld.api.sampler.Sampler;
import io.novaordis.utilities.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultiThreadedRunnerImpl implements MultiThreadedRunner {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MultiThreadedRunnerImpl.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int threadCount;
    private boolean background;
    private long singleThreadedRunnerSleepMs;
    private Duration duration;

    private List<SingleThreadedRunner> singleThreadedRunners;

    private Service service;
    private LoadStrategy loadStrategy;

    private Sampler sampler;

    private ExitGuard exitGuard;

    private CommandLineConsole commandLineConsole;

    private volatile boolean running;

    // may be null
    private KeyStore keyStore;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param sampler the (already configured) sampler that samples operations and asynchronously pushes statistics
     *                to its consumers. May be null, which means we only want to generate load, but we're not interested
     *                in statistics.
     * @param keyStore may be null, if the configuration did not specify one.
     * @param background if true, the load driver runs in the background, detached from the controlling terminal,
     *                   so the runner must avoid interacting with stdout, stderr and stdin.
     *
     */
    public MultiThreadedRunnerImpl(Service service, Sampler sampler, KeyStore keyStore,
                                   int threadCount, boolean background, long singleThreadedRunnerSleepMs) {

        this.service = service;
        this.loadStrategy = service.getLoadStrategy();
        this.threadCount = threadCount;
        this.sampler = sampler;
        this.background = background;
        this.singleThreadedRunnerSleepMs = singleThreadedRunnerSleepMs;
        this.keyStore = keyStore;

        this.singleThreadedRunners = new ArrayList<>(threadCount);
        this.exitGuard = new ExitGuard();
        this.running = false;

        this.duration = null; // run indefinitiely
    }

    // MultiThreadRunner implementation --------------------------------------------------------------------------------

    @Override
    public boolean isRunning()
    {
        return running;
    }

    @Override
    public void run() throws BrokenBarrierException, InterruptedException {

        running = true;

        try {

            checkPreconditions();

            if (background) {

                //
                // unlatch the exit guard, exit when the threads are done
                //

                exitGuard.allowExit();
            } else {

                //
                // not in background, we need the console
                //
                commandLineConsole = new CommandLineConsole(this, sampler);
                commandLineConsole.start();
            }

            // threadCount + the main thread that runs this code
            CyclicBarrier barrier = new CyclicBarrier(threadCount + 1);

            //
            // end of initialization
            //


            //
            // if this run has a limited duration, start a high priority timer that will stop the run after the time
            // has passed. If the run is not time-limited, "durationExpired" will never become "true".
            //

            final AtomicBoolean durationExpired = new AtomicBoolean(false);

            if (getDuration() != null) {

                Duration d = getDuration();
                Timer durationTimer = new Timer("Multi-threaded runner " + d + " stop thread");
                durationTimer.schedule(new DurationTimerTask(d, durationExpired), d.getMilliseconds());
                log.debug("duration timer task scheduled, it will fire after " + d);
            }

            //
            // start the threads
            //

            for (int i = 0; i < threadCount; i++) {

                String name = "GLD Runner " + i;

                SingleThreadedRunner r = new SingleThreadedRunner(
                        name, service, loadStrategy, sampler, barrier, durationExpired,
                        singleThreadedRunnerSleepMs, keyStore);

                singleThreadedRunners.add(r);

                r.start();
            }

            log.debug("waiting for " + singleThreadedRunners.size() + " SingleThreadedRunner(s) to finish ...");

            //
            // this will throw InterruptedException if the thread executing the runner is interrupted
            //
            barrier.await();

            log.debug(singleThreadedRunners.size() + " SingleThreadedRunner(s) have finished");

            if (commandLineConsole != null) {

                if (isWaitForConsoleQuit()) {

                    log.debug("waiting for console to issue quit ...");
                    commandLineConsole.waitForExplicitQuit();
                    log.debug("console issued quit");
                } else {

                    commandLineConsole.stop(); // no more input needed from the console so dispose of it
                }
            }

            exitGuard.waitUntilExitIsAllowed();

        }
        finally {

            //
            // stopping the lifecycle components is the caller's responsibility, but we need to mark this runner
            // as "not running", irrespective of how we leave the function
            //

            running = false;
        }
    }

    @Override
    public void stop() {

        singleThreadedRunners.forEach(io.novaordis.gld.driver.SingleThreadedRunner::stop);
    }

    @Override
    public ExitGuard getExitGuard() {

        return exitGuard;
    }

    @Override
    public Duration getDuration() {

        return duration;
    }

    @Override
    public void setDuration(Duration d) {

        this.duration = d;
    }

    @Override
    public boolean isWaitForConsoleQuit() {

        throw new RuntimeException("isWaitForConsoleQuit() NOT YET IMPLEMENTED");
    }

    @Override
    public void setWaitForConsoleQuit(boolean b) {

        throw new RuntimeException("setWaitForConsoleQuit() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return "MultiThreadedRunner[" + Integer.toHexString(System.identityHashCode(this)) + "](" + threadCount + ")";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void checkPreconditions() throws IllegalStateException {

        //
        // check preconditions
        //

        if (service == null) {

            throw new IllegalStateException("null service");
        }

        if (!service.isStarted()) {

            throw new IllegalStateException("service " + service + " not started");
        }

        if (sampler == null) {

            log.debug("there is no sampler, which means the runner will generate load but not statistics");
        }
        else {

            if (!sampler.isStarted()) {

                throw new IllegalStateException("sampler " + sampler + " not started");
            }
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

    /**
     * A class that stops the multi-threaded runner after the specified duration.
     */
    private class DurationTimerTask extends TimerTask {

        private AtomicBoolean durationExpired;
        private Duration duration;

        private DurationTimerTask(Duration duration, AtomicBoolean durationExpired) {

            this.duration = duration;
            this.durationExpired = durationExpired;
        }

        @Override
        public void run() {

            log.debug("shutting down the runner after " + duration);
            durationExpired.set(true);
        }
    }
}
