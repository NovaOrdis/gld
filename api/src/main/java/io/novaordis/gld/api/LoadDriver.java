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
 * @{linktourl https://kb.novaordis.com/index.php/Gld_Concepts#Load_Driver_Instance}
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public interface LoadDriver {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Initialize the load driver and dependencies (the service and auxiliary components), based on the Configuration
     * passed to it.
     */
    void init(Configuration c) throws Exception;

    /**
     * The main LoadDriver instance loop.
     */
    void run();


    /**
     * @return true if the load driver runs in background and cannot be controlled directly from stdin, false if it
     * runs in foreground and can be controlled from stdin.
     */
    boolean isBackground();

    //
    // topology --------------------------------------------------------------------------------------------------------
    //

    /**
     * A LoadDriver instance can only be associated with a service at a time.
     */
    Service getService();

    /**
     * A LoadDriver instance can only be associated with a service at a time.
     */
    KeyStore getKeyStore();


}