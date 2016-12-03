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

package io.novaordis.gld.driver.todeplete.command;

import io.novaordis.gld.api.todiscard.Command;
import io.novaordis.gld.api.todiscard.Configuration;
import org.apache.log4j.Logger;

public abstract class CommandTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CommandTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

//    @Test
//    public void weFailIfWeAreNotInitialized() throws Exception
//    {
//        Command c = getCommandToTest(new MockConfiguration());
//
//        if (c.isInitialized())
//        {
//            log.info(c + " initialized by default");
//            return;
//        }
//
//        try
//        {
//            c.execute();
//            fail("should fail with IllegalStateException because we're not initialized");
//        }
//        catch(IllegalStateException e)
//        {
//            log.info(e.getMessage());
//        }
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract Command getCommandToTest(Configuration c);

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
