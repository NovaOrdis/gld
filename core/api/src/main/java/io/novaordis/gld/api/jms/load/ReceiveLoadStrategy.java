/*
 * Copyright (c) 2017 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api.jms.load;

import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.operation.Receive;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/22/17
 */
public class ReceiveLoadStrategy extends JMSLoadStrategyBase {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String NAME = "receive";

    private static final Set<Class<? extends Operation>> OPERATION_TYPES;

    static {

        OPERATION_TYPES = new HashSet<>();
        OPERATION_TYPES.add(Receive.class);
    }

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // TODO migrate to base
    private volatile boolean initialized;

    private long timeoutMs;

    // Constructors ----------------------------------------------------------------------------------------------------

    // JMSLoadStrategyBase overrides -----------------------------------------------------------------------------------

    @Override
    public String getName() {

        return NAME;
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {

        return OPERATION_TYPES;
    }

    @Override
    public Receive next(Operation last, String lastWrittenKey, boolean runtimeShuttingDown) throws Exception {

        return (Receive)super.next(last, lastWrittenKey, runtimeShuttingDown);
    }

    @Override
    protected Operation nextInternal(Operation last, String lastWrittenKey, boolean runtimeShuttingDown)
            throws Exception {

        if (!initialized) {

            throw new IllegalStateException(this + " was not initialized");
        }

        return new Receive(this);

    }
    // Public ----------------------------------------------------------------------------------------------------------

    public long getTimeoutMs() {

        return timeoutMs;
    }

    public void setTimeoutMs(Long timeoutMs) {

        this.timeoutMs = timeoutMs;
    }

    @Override
    public String toString() {

        return "NOT YET IMPLEMENTED";

//        long remainingOperations = getRemainingOperations();
//        return "ReceiveLoadStrategy[remaining=" +
//                (remainingOperations == Long.MAX_VALUE ? "unlimited" : remainingOperations ) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------


    @Override
    protected void initInternal(
            ServiceConfiguration sc, Map<String, Object> loadStrategyRawConfig, LoadConfiguration lc) throws Exception {

        initialized = true;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
