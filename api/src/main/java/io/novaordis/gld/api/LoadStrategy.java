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

package io.novaordis.gld.api;

import java.util.List;
import java.util.Set;

import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.utilities.UserErrorException;

/**
 * Implementations *must* provide a no-argument constructor.
 */
public interface LoadStrategy {

    String getName();

    /**
     * A strategy must be generally configured before the first use. If the strategy requires configuration and it was
     * not properly configured, the first next() invocation will throw IllegalStateException.
     *
     * @param arguments - command line arguments. Relevant arguments will be used and removed from the list. null is
     *                  fine, will be ignored/
     *
     * @exception IllegalArgumentException on null configuration.
     * @exception UserErrorException recommended on invalid configuration values or inconsistent state after
     * configuration.
     *
     */
    void configure(Configuration configuration, List<String> arguments, int from) throws Exception;

    /**
     * @return the next operation to be sent into the service, factoring in the last operation that has been sent into
     * the service.
     *
     * @param last the last operation that has been sent into the service. It may be null (which means that no operation
     *             was yet sent into the service..
     *
     * @param lastWrittenKey last successfully written key - the method should be prepared for the situation the key
     *        is null.
     *
     * @param runtimeShuttingDown - true if the run context is shutting down and this is the last operation to be sent
     *                            into the service, false otherwise. This gives the load strategy a chance to send a
     *                            "cleanup" operation, if needed.
     *
     * @exception java.lang.IllegalStateException if the strategy was not properly configured before use.
     */
    Operation next(Operation last, String lastWrittenKey, boolean runtimeShuttingDown) throws Exception;

    /**
     * @return the full set of operation types that can be possibly generated by this load strategy.
     */
    Set<Class<? extends Operation>> getOperationTypes();

    /**
     * @return the key store used by this load strategy. May return null.
     */
    KeyStore getKeyStore();
}