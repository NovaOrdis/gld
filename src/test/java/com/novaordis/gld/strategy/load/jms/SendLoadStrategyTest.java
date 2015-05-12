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

package com.novaordis.gld.strategy.load.jms;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.LoadStrategy;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.command.Load;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.operations.jms.Send;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SendLoadStrategyTest extends JmsLoadStrategyTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ReceiveLoadStrategyTest.class);

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    @Override
    @Test
    public void nullArguments() throws Exception
    {
        LoadStrategy s = getLoadStrategyToTest();
        Configuration c = new MockConfiguration();

        try
        {
            s.configure(c, null, -1);
            fail("should complain a about missing destination");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void next() throws Exception
    {
        SendLoadStrategy sls = getLoadStrategyToTest();

        List<String> args = new ArrayList<>(Arrays.asList("--queue", "test"));

        MockConfiguration mc = new MockConfiguration();

        // load will register itself with configuration
        new Load(mc, new ArrayList<>(Arrays.asList("--max-operations", "2")), 0);

        sls.configure(mc, args, 0);

        assertTrue(args.isEmpty());
        Queue queue = (Queue)sls.getDestination();
        assertEquals("test", queue.getName());

        Send send = (Send)sls.next(null, null);
        assertEquals(queue, send.getDestination());

        Send send2 = (Send) sls.next(null, null);
        assertEquals(queue, send2.getDestination());

        Send send3 = (Send) sls.next(null, null);
        assertNull(send3);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected SendLoadStrategy getLoadStrategyToTest()
    {
        return new SendLoadStrategy();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
