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

package io.novaordis.gld.api.cache.load;

import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.LoadStrategyBase;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.gld.api.cache.CacheServiceConfiguration;
import io.novaordis.gld.api.cache.operation.Read;
import io.novaordis.gld.api.cache.operation.Write;
import io.novaordis.gld.api.provider.RandomKeyProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A load strategy that implements the following behavior:
 *
 * It first attempts a read.
 *
 * If it's a hit, it keeps reading.
 *
 * If it's a miss, it writes the key and a random value. It gets the keys from the configured key store or it randomly
 * generates them if there is not key store.
 *
 * It is supposed to be thread-safe, the intention is to be accessed concurrently from different threads. This implies
 * that the KeyStore implementation is also thread-safe.
 *
 * Configuration documentation:
 *
 * @{linktourl https://kb.novaordis.com/index.php/Gld_Configuration#read-then-write-on-miss_Load_Strategy_Configuration}
 */
public class ReadThenWriteOnMissLoadStrategy extends LoadStrategyBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Set<Class<? extends Operation>> OPERATION_TYPES;

    static {

        Set<Class<? extends Operation>> s = new HashSet<>();
        s.add(Read.class);
        s.add(Write.class);
        OPERATION_TYPES = Collections.unmodifiableSet(s);
    }

    public static final String NAME = "read-then-write-on-miss";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private volatile boolean initialized;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ReadThenWriteOnMissLoadStrategy() {

        super();

        //
        // set the default value size, it will be overridden when init() is called
        //
        setValueSize(ServiceType.cache.getDefaultValueSize());
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public String getName() {

        return NAME;
    }

    @Override
    public ServiceType getServiceType() {

        return ServiceType.cache;
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {

        return OPERATION_TYPES;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return getName();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected void init(ServiceConfiguration sc, Map<String, Object> loadStrategyRawConfig, LoadConfiguration lc)
            throws Exception {

        if (!(sc instanceof CacheServiceConfiguration)) {

            throw new IllegalArgumentException(sc + " not a CacheServiceConfiguration");
        }

        //
        // create and configure the key provider
        //

        Integer keySize = lc.getKeySize();
        keySize = keySize == null ? ServiceType.cache.getDefaultKeySize() : keySize;
        RandomKeyProvider keyProvider = new RandomKeyProvider(keySize);

        Long keyCount = lc.getOperations();
        keyProvider.setKeyCount(keyCount);

        //
        // install the provider ...
        //
        setKeyProvider(keyProvider);

        //
        // ... and start it
        //

        keyProvider.start();
        initialized = true;
    }

    @Override
    protected Operation nextInternal(Operation lastOperation, String lastWrittenKey, boolean runtimeShuttingDown)
            throws Exception {

        if (!initialized) {

            throw new IllegalStateException(this + " was not initialized");
        }

        Operation result;

        if (lastOperation == null) {

            //
            // start with a read
            //
            result = getNextRead();
        }
        else if (lastOperation instanceof Read) {

            Read lastRead = (Read)lastOperation;

            // if the last read is a hit, generate another read

            if (lastRead.getValue() != null) {

                result = getNextRead();
            }
            else {

                String lastReadKey = lastRead.getKey();
                String value = computeValue();
                result = new Write(lastReadKey, value);
            }
        }
        else if (lastOperation instanceof Write) {

            // read follows a write
            result = getNextRead();
        }
        else {

            throw new IllegalArgumentException("unknown last operation " + lastOperation);
        }

        return result;

    }

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * @return a non-null next Read if the key store has not run out of read operations, or null if it has.
     */
    private Read getNextRead() {

        String key =  getKeyProvider().next();

        if (key == null) {

            // the key store ran out of keys, so we ran out of operations, return null

            return null;
        }

        return new Read(key);
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
