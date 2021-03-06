/*
 * Copyright (c) 2016 Nova Ordis LLC
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

import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.jms.Destination;
import io.novaordis.gld.api.jms.JMSEndpoint;
import io.novaordis.gld.api.jms.load.JMSLoadStrategy;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/5/16
 */
public interface JmsOperation extends Operation {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    JMSLoadStrategy getLoadStrategy();

    /**
     * May be null.
     */
    String getPayload();

    Destination getDestination();

    /**
     * May return null if a send operation which was not submitted for processing.
     */
    String getId();

    /**
     * Performs the operation with the endpoint that was checked out from the service. After the operation is performed
     * the endpoint should be checked in, according to the following pattern:
     *
     * JMSEndpoint endpoint = service.checkOut(operation);
     *
     * try {
     *
     *     operation.perform(endpoint);
     * }
     * finally {
     *
     *     service.checkIn(endpoint);
     * }
     */
    void perform(JMSEndpoint endpoint) throws Exception;

}
