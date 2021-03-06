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

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.todiscard.Configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Delete extends CommandBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    /**
     * Keep us as low as possible, to limit the damage if used incorrectly.
     */
    public static final int DEFAULT_KEYS_TO_DELETE = 1;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private LoadStrategy loadStrategy;

    private int keysToDelete;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Delete(Configuration c)
    {
        super(c);

        this.keysToDelete = DEFAULT_KEYS_TO_DELETE;
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void initialize() throws Exception
    {
//        processCommandSpecificArguments(getArguments());
//
//        Configuration config = getConfiguration();
//        CacheService cacheService = (CacheService) config.getService();
//
//        // we need to have a valid cache service
//        if (cacheService == null)
//        {
//            throw new IllegalStateException("null cache service");
//        }
//
//        // start the cache if not started
//        if (!cacheService.isStarted())
//        {
//            cacheService.start();
//        }
//
//        // wire in a "DeleteKeys" strategy
//
//        loadStrategy = new DeleteLoadStrategy(keysToDelete);
//
//        loadStrategy.init(config, new ArrayList<String>(), 0);
//        config.setLoadStrategy(loadStrategy);

        throw new RuntimeException("RETURN HERE");
    }

    @Override
    public boolean isInitialized()
    {
//        return getConfiguration().getLoadStrategy() != null;
//
        throw new RuntimeException("RETURN HERE");

    }

    @Override
    public void execute() throws Exception
    {
//        insureInitialized();
//
//        new MultiThreadedRunnerImpl(getConfiguration()).run();

        // the command will exit when all parallel threads finish - no exit guard here ...

        throw new RuntimeException("RETURN HERE");

    }

    // Public ----------------------------------------------------------------------------------------------------------

    public int getKeysToDelete()
    {
        return keysToDelete;
    }

    public String toString()
    {
        return "Delete[count=" + keysToDelete + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    LoadStrategy getLoadStrategy()
    {
        return loadStrategy;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void processCommandSpecificArguments(List<String> args)
    {
//        for(Iterator<String> i = args.iterator(); i.hasNext(); )
//        {
//            String crt = i.next();
//            if ("--key-count".equals(crt))
//            {
//                if (!i.hasNext())
//                {
//                    throw new UserErrorException("--key-count should be followed by an positive integer");
//                }
//
//                crt = i.next();
//
//                try
//                {
//                    keysToDelete = Integer.parseInt(crt);
//                }
//                catch(Exception e)
//                {
//                    throw new UserErrorException("invalid --key-count value: " + crt, e);
//                }
//            }
//            else
//            {
//                throw new UserErrorException("unknown 'delete' argument: " + crt);
//            }
//        }

        throw new RuntimeException("RETURN HERE");
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
