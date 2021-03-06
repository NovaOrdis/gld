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

package io.novaordis.gld.api.jms.embedded;

import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.Destination;
import io.novaordis.gld.api.jms.JMSServiceBase;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/25/17
 */
public class EmbeddedJMSService extends JMSServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(EmbeddedJMSService.class);

    public static final String DEFAULT_CONNECTION_FACTORY_NAME = "/MockConnectionFactory";

    public static final String DEFAULT_AUTHORIZED_USER = "mock-user";
    public static final char[] DEFAULT_AUTHORIZED_PASSWORD_AS_CHAR_ARRAY = new char[] {'m', 'o', 'c', 'k'};
    public static final String DEFAULT_AUTHORIZED_PASSWORD = new String(DEFAULT_AUTHORIZED_PASSWORD_AS_CHAR_ARRAY);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, EmbeddedConnectionFactory> connectionFactories;
    private Map<String, EmbeddedDestination> destinations;

    // Constructors ----------------------------------------------------------------------------------------------------

    public EmbeddedJMSService() {

        this.connectionFactories = new HashMap<>();

        //
        // We simulate the existence of /MockConnectionFactory, anything else does not exist
        //

        EmbeddedConnectionFactory cf =
                new EmbeddedConnectionFactory(DEFAULT_AUTHORIZED_USER, DEFAULT_AUTHORIZED_PASSWORD);

        connectionFactories.put(DEFAULT_CONNECTION_FACTORY_NAME, cf);

        this.destinations = new HashMap<>();
    }

    // JMSService implementation ---------------------------------------------------------------------------------------

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        super.configure(serviceConfiguration);

        log.debug(this + " configured");
    }

    @Override
    public javax.jms.Destination resolveDestination(Destination d) {

        EmbeddedDestination ed = destinations.get(d.getName());

        if (ed != null) {

            if (ed instanceof javax.jms.Queue && d instanceof Topic ||
                    ed instanceof javax.jms.Topic && d instanceof Queue) {

                throw new IllegalArgumentException(
                        "destination " + d.getName() + " exists but it is a " +
                                (ed instanceof javax.jms.Queue ? "queue" : "topic"));
            }
        }

        return ed;
    }

    /**
     * We simulate the existence of the "/MockConnectionFactory" connection factory by default. Antyhing else does not
     * exist.
     */
    @Override
    public ConnectionFactory resolveConnectionFactory(String connectionFactoryName) {

        //
        // for the time being, any connection factory "exists"
        //

        return connectionFactories.get(connectionFactoryName);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void addToDestination(String name, boolean queue, Message m) {

        EmbeddedDestination d = destinations.get(name);

        if (d == null) {

            d = queue ? new EmbeddedQueue(name) : new EmbeddedTopic(name);
            destinations.put(name, d);

        }
        else {

            if (d instanceof Queue && !queue || d instanceof Topic && queue) {

                throw new IllegalArgumentException(d + " exists and it is a " + d.getClass().getSimpleName());
            }
        }

        d.add(m);
    }

    public void installConnectionFactory(String connectionFactoryName, String username, String password) {

        EmbeddedConnectionFactory cf = new EmbeddedConnectionFactory(username, password);
        connectionFactories.put(connectionFactoryName, cf);
    }

    public void createDestination(Destination d) throws Exception {

        EmbeddedDestination ed = d.isQueue() ? new EmbeddedQueue(d.getName()) : new EmbeddedTopic(d.getName());
        destinations.put(d.getName(), ed);
    }

    public void removeDestination(String jndiName) throws Exception {

        destinations.remove(jndiName);
    }

    @Override
    public String toString() {

        return "EmbeddedJMSService[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------


    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
