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

package io.novaordis.gld.api;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/12/16
 */
public interface Runner {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    boolean isRunning();

    /**
     * May trigger multi-threaded execution. When all threads finish, return.
     *
     * Implementations typically initialize dependencies, start multiple SingleThreadedRunners in parallel, and wait
     * until those finish. Stopping the life cycle components is not this method's responsibility, but the caller's.
     */
    void run() throws Exception;

    /**
     * Normally the runner stops when all operations are depleted or the load run duration expires. This method
     * is used to forcibly stop the runner during the run.
     */
    void stop() throws Exception;

}
