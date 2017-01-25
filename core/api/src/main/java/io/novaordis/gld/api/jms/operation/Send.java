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

package io.novaordis.gld.api.jms.operation;

import io.novaordis.gld.api.jms.embedded.EmbeddedQueue;
import io.novaordis.gld.api.jms.load.JmsLoadStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/23/17
 */
public class Send extends JmsOperationBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(Send.class);
    private static final boolean trace = log.isTraceEnabled();

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    public Send(JmsLoadStrategy loadStrategy) {

        super(loadStrategy);

        // create the payload outside perform() method to influence as little as possible the execution duration;
        // in this specific case we reuse the message created by the strategy (and presumably cached), because we
        // are not interested creating distinct message bodies, we're only interested in the payload length

        String v = loadStrategy.computeValue();
        setPayload(v);
    }

    // Constructors ----------------------------------------------------------------------------------------------------

    // JmsOperation implementation -------------------------------------------------------------------------------------

    @Override
    public boolean wasPerformed() {
        throw new RuntimeException("wasPerformed() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean wasSuccessful() {
        throw new RuntimeException("wasSuccessful() NOT YET IMPLEMENTED");
    }

    @Override
    public void perform(Session session) throws Exception {

        Destination destination = new EmbeddedQueue(getLoadStrategy().getDestination().getName());
        MessageProducer producer = session.createProducer(destination);
        String payload = getPayload();
        TextMessage m = session.createTextMessage(payload);

        // if (trace) { log.trace("sending message with payload \"" + payload + "\""); }

        producer.send(m);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
