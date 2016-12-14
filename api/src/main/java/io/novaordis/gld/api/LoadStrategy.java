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

import java.util.Set;

import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.utilities.UserErrorException;

public interface LoadStrategy {

    // lifecycle -------------------------------------------------------------------------------------------------------

    /**
     * A strategy must be generally initialized before the first use. If the strategy requires configuration and it was
     * not properly configured, the first next() invocation will throw IllegalStateException.
     *
     * Note that initialization may also consist in establishing relationship with other components, such as the
     * associated Service. However, that can be done outside the init() method, which is, strictly speaking, aimed
     * to configure the LoadStrategy instance private internal state.
     *
     * @param serviceConfiguration the service configuration. Subclasses may cast it to more specific types. It gives
     *                             access to raw configuration sub-maps, if necessary.It is here to allow access to
     *                             the load strategy raw configuration sub-map and also to service configuration
     *                             elements (such as the default key size for a cache service, etc.)
     *
     * @param loadConfiguration the load characteristics.
     *
     * @exception UserErrorException on initialization failure. The implementations are required to fail if they
     * encounter unknown configuration options.
     *
     * @exception IllegalStateException on inconsistencies.
     */
    void init(ServiceConfiguration serviceConfiguration, LoadConfiguration loadConfiguration) throws Exception;

    void start() throws Exception;

    boolean isStarted();

    void stop();

    // accessors -------------------------------------------------------------------------------------------------------

    /**
     * The name this load strategy is known under, and which can be used in a configuration file.
     *
     * Example: "read-then-write-on-miss" will instruct the runtime to dynamically load ReadThenWriteOnMissLoadStrategy.
     */
    String getName();

    /**
     * @return the service type this load strategy is associated with.
     */
    ServiceType getServiceType();

    /**
     * @return The "owner" service.
     */
    Service getService();

    /**
     * Use it to link the load strategy factory to the associated service.
     *
     * @param s the associated service.
     *
     * @exception IllegalArgumentException if the service and this load strategy instance are not compatible.
     */
    void setService(Service s) throws IllegalArgumentException;

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
     * @param shuttingDown - true if the load driver is in process of shutting down and this is the last operation to
     *                     be sent into the service, false otherwise. This gives the load strategy a chance to send a
     *                            "cleanup" operation, if needed.
     *
     * @exception java.lang.IllegalStateException if the strategy was not properly initialized before the first next()
     * invocation.
     */
    Operation next(Operation last, String lastWrittenKey, boolean shuttingDown) throws Exception;

    /**
     * @return the full set of operation types that can be possibly generated by this load strategy.
     */
    Set<Class<? extends Operation>> getOperationTypes();

    /**
     * @return the key provider used by this load strategy. May return null.
     */
    KeyProvider getKeyProvider();


}
