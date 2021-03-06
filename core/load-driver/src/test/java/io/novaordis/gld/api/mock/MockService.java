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

package io.novaordis.gld.api.mock;

import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.ServiceBase;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * We extend ServiceBase because some of the tests depend on the start()/stop() built-in capacity to start/stop
 * dependencies.
 */
public class MockService extends ServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockService.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean verbose;
    private boolean wasStarted;

    private Map<Thread, Integer> perThreadInvocationCount;
    private List<Operation> executedOperations;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockService() {

        perThreadInvocationCount = new ConcurrentHashMap<>();
        executedOperations = new ArrayList<>();
    }

    // Service implementation ------------------------------------------------------------------------------------------

    //
    // we rely on the subclass' start()
    //

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        log.info(this + " was mock configured");
    }

    @Override
    public void start() throws Exception {

        wasStarted = true;
        super.start();
    }

    //
    // we rely on the subclass' stop()
    //

    //
    // we rely on the subclass' isStarted()
    //

    //
    // we rely on the subclass' getLoadDriver()
    //

    //
    // we rely on the subclass' setLoadDriver(...)
    //

    //
    // we rely on the subclass' getLoadStrategy()
    //

    //
    // we rely on the subclass' setLoadStrategy(...)
    //

    @Override
    public ServiceType getType() {

        return ServiceType.mock;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return true if start() method was called at least once
     */
    public boolean wasStarted()
    {
        return wasStarted;
    }

    public Map<Thread, Integer> getPerThreadInvocationCountMap() {
        return perThreadInvocationCount;
    }

    /**
     * We need to explicitly set the instance as verbose in order to next log.info(), otherwise the high concurrency
     * tests are too noisy.
     */
    public void setVerbose(boolean b) {
        this.verbose = b;
    }

    public void simulateOperationExecutionOnThisService(MockOperation o) throws Exception {

        if (verbose) { log.info(this + " performing " + o); }

        //
        // may be executing concurrently on multiple threads
        //

        synchronized (this) {

            executedOperations.add(o);
        }

        Thread currentThread = Thread.currentThread();

        Integer invocationCountPerThread = perThreadInvocationCount.get(currentThread);
        if (invocationCountPerThread == null) {

            invocationCountPerThread = 1;
            perThreadInvocationCount.put(currentThread, invocationCountPerThread);
        }
        else {

            perThreadInvocationCount.put(currentThread, invocationCountPerThread + 1);
        }
    }

    public List<Operation> getExecutedOperations() {

        return executedOperations;
    }

    @Override
    public String toString() {

        return "MockService[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
