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

package io.novaordis.gld.driver.keystore;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.store.KeyStoreException;
import io.novaordis.gld.api.store.Value;

import java.util.Iterator;
import java.util.Set;

/**
 * This implementation reads the entire key space in memory on startup and then keeps cycling through it.
 */
public class SetKeyStore implements KeyStore {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private volatile boolean started;

    private Iterator<String> iterator;

    private int size;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Auto-starting.
     */
    public SetKeyStore(Set<String> keys) throws Exception
    {
        this.size = keys.size();
        this.iterator = keys.iterator();
        start();
    }

    // KeyStore implementation -----------------------------------------------------------------------------------------

    @Override
    public void store(String key, byte[] ... value) throws KeyStoreException
    {
        throw new IllegalStateException("this is a read-only keystore, cannot store");
    }

    @Override
    public Value retrieve(String key) throws KeyStoreException {
        throw new RuntimeException("retrieve() NOT YET IMPLEMENTED");
    }

    @Override
    public Set<String> getKeys() throws KeyStoreException {
        throw new RuntimeException("getKeys() NOT YET IMPLEMENTED");
    }

    @Override
    public long getKeyCount() {
        throw new RuntimeException("getKeyCount() NOT YET IMPLEMENTED");
    }

    //    @Override
    public synchronized String get()
    {
        if (!iterator.hasNext())
        {
            return null;
        }

        size --;
        return iterator.next();
    }

    @Override
    public void start() throws KeyStoreException
    {
        started = true;
    }

    @Override
    public void stop() throws KeyStoreException
    {
        started = false;
    }

    @Override
    public boolean isStarted()
    {
        return started;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public int size()
    {
        return size;
    }

    @Override
    public String toString()
    {
        return "SetKeyStore[" + size + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
