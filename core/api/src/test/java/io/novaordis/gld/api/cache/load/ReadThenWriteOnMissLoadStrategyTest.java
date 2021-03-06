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

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.LoadStrategyFactory;
import io.novaordis.gld.api.LoadStrategyTest;
import io.novaordis.gld.api.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.configuration.MockServiceConfiguration;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.gld.api.cache.MockCacheServiceConfiguration;
import io.novaordis.gld.api.cache.operation.Read;
import io.novaordis.gld.api.cache.operation.Write;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ReadThenWriteOnMissLoadStrategyTest extends LoadStrategyTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ReadThenWriteOnMissLoadStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void identity() throws Exception {

        ReadThenWriteOnMissLoadStrategy s = getLoadStrategyToTest();

        assertEquals("read-then-write-on-miss", s.getName());
        assertEquals(ServiceType.cache, s.getServiceType());

        Set<Class<? extends Operation>> operations = s.getOperationTypes();
        assertEquals(2, operations.size());
        assertTrue(operations.contains(Read.class));
        assertTrue(operations.contains(Write.class));

        assertTrue(s.isReuseValue());
    }

    @Test
    public void init() throws Exception {

        ReadThenWriteOnMissLoadStrategy s = getLoadStrategyToTest();

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();

        Map<String, Object> m = new HashMap<>();
        m.put(LoadStrategy.NAME_LABEL, s.getName());
        msc.set(m, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);

        s.init(msc, mlc);

        // no relationship with a service yet
        assertNull(s.getService());
    }

    @Test
    public void init_reuseValue_InvalidValue() throws Exception {

        ReadThenWriteOnMissLoadStrategy s = getLoadStrategyToTest();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockCacheServiceConfiguration sc = getCorrespondingServiceConfiguration();
        sc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);
        sc.set("true", ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.REUSE_VALUE_LABEL);

        try {

            s.init(sc, mlc);
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.contains("Boolean"));
            assertTrue(msg.contains("String"));
            assertTrue(msg.contains("\"true\""));
        }
    }

//    @Test
//    public void hit_noKeyStore() throws Exception {
//
//        ReadThenWriteOnMissLoadStrategy rtwom = getLoadStrategyToTest(null, null, -1);
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setKeySize(11);
//        mc.setValueSize(17);
//        mc.setUseDifferentValues(false);
//
//        rtwom.configure(mc, Collections.<String>emptyList(), 0);
//
//        // first operation is always a read
//        Operation o = rtwom.next(null, null, false);
//
//        Read r = (Read)o;
//
//        String key = r.getKey();
//        log.info(key);
//        assertEquals(11, key.length());
//        assertNull(r.getValue());
//
//        // make it a "hit"
//        r.setValue("something");
//
//        o = rtwom.next(r, null, false);
//
//        // the next operation is another read, for a different random key
//
//        Read r2 = (Read)o;
//
//        String key2 = r2.getKey();
//        log.info(key2);
//        assertEquals(11, key2.length());
//        assertNull(r2.getValue());
//        assertNotEquals(key, key2);
//    }
//
//    @Test
//    public void miss_noKeyStore() throws Exception {
//        ReadThenWriteOnMissLoadStrategy rtwom = getLoadStrategyToTest(null, null, -1);
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setKeySize(11);
//        mc.setValueSize(17);
//        mc.setUseDifferentValues(false);
//
//        rtwom.configure(mc, Collections.<String>emptyList(), 0);
//
//        // first operation is always a read
//        Operation o = rtwom.next(null, null, false);
//
//        Read r = (Read)o;
//
//        String key = r.getKey();
//        log.info(key);
//        assertEquals(11, key.length());
//
//        // insure it's a miss
//        assertNull(r.getValue());
//
//        o = rtwom.next(r, null, false);
//
//        // the next operation is a write for the key we missed
//
//        Write w = (Write)o;
//
//        String key2 = w.getKey();
//        assertEquals(key, key2);
//
//        String value = w.getValue();
//        assertEquals(17, value.length());
//    }
//
//    @Test
//    public void readAfterWrite_noKeyStore() throws Exception {
//
//        ReadThenWriteOnMissLoadStrategy rtwom = getLoadStrategyToTest(null, null, -1);
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setKeySize(11);
//        mc.setValueSize(17);
//        mc.setUseDifferentValues(false);
//
//        rtwom.configure(mc, Collections.<String>emptyList(), 0);
//
//        Write w = new Write("TEST-KEY", "TEST-VALUE");
//
//        Operation o = rtwom.next(w, null, false);
//
//        // the next operation after a write is another read
//
//        Read r = (Read)o;
//
//        String key = r.getKey();
//        log.info(key);
//        assertEquals(11, key.length());
//    }
//
//    @Test
//    public void hit_validKeyStore() throws Exception {
//
//        File keyStoreFile = new File(Tests.getScratchDir(), "keys.txt");
//        Files.write(keyStoreFile, "KEY0\nKEY1\nKEY2\n");
//
//        ReadThenWriteOnMissLoadStrategy rtwom = getLoadStrategyToTest(null, null, -1);
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setKeySize(11);
//        mc.setValueSize(17);
//        mc.setUseDifferentValues(false);
//        mc.setKeyStoreFile(keyStoreFile.getPath());
//
//        rtwom.configure(mc, Collections.<String>emptyList(), 0);
//
//        // first operation is always a read
//        Operation o = rtwom.next(null, null, false);
//
//        Read r = (Read)o;
//
//        String key = r.getKey();
//        log.info(key);
//        assertEquals("KEY0", key);
//        assertNull(r.getValue());
//
//        // make it a "hit"
//        r.setValue("something");
//
//        o = rtwom.next(r, null, false);
//
//        // the next operation is another read, for the next key
//
//        Read r2 = (Read)o;
//
//        String key2 = r2.getKey();
//        log.info(key2);
//        assertEquals("KEY1", key2);
//        assertNull(r2.getValue());
//    }
//
//    @Test
//    public void miss_validKeyStore() throws Exception {
//        File keyStoreFile = new File(Tests.getScratchDir(), "keys.txt");
//        Files.write(keyStoreFile, "KEY0\nKEY1\nKEY2\n");
//
//        ReadThenWriteOnMissLoadStrategy rtwom = getLoadStrategyToTest(null, null, -1);
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setKeySize(11);
//        mc.setValueSize(17);
//        mc.setUseDifferentValues(false);
//        mc.setKeyStoreFile(keyStoreFile.getPath());
//
//        rtwom.configure(mc, Collections.<String>emptyList(), 0);
//
//        // first operation is always a read
//        Operation o = rtwom.next(null, null, false);
//
//        Read r = (Read)o;
//
//        String key = r.getKey();
//        log.info(key);
//        assertEquals("KEY0", key);
//
//        // insure it's a miss
//        assertNull(r.getValue());
//
//        o = rtwom.next(r, null, false);
//
//        // the next operation is a write for the key we missed
//
//        Write w = (Write)o;
//
//        String key2 = w.getKey();
//        assertEquals(key, key2);
//
//        String value = w.getValue();
//        assertEquals(17, value.length());
//    }
//
//    @Test
//    public void readAfterWrite_validKeyStore() throws Exception {
//        File keyStoreFile = new File(Tests.getScratchDir(), "keys.txt");
//        Files.write(keyStoreFile, "KEY0\nKEY1\nKEY2\n");
//
//        ReadThenWriteOnMissLoadStrategy rtwom = getLoadStrategyToTest(null, null, -1);
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setKeySize(11);
//        mc.setValueSize(17);
//        mc.setUseDifferentValues(false);
//        mc.setKeyStoreFile(keyStoreFile.getPath());
//
//        rtwom.configure(mc, Collections.<String>emptyList(), 0);
//
//        Write w = new Write("TEST-KEY", "TEST-VALUE");
//
//        Operation o = rtwom.next(w, null, false);
//
//        // the next operation after a write is another read
//
//        Read r = (Read)o;
//
//        String key = r.getKey();
//        log.info(key);
//        assertEquals("KEY0", key);
//    }

    //
    // integration with SingleThreadedRunner
    //

//    @Test
//    public void integration_ReadThenWriteOnMiss_SingleThreadedRunner_ReadThenOutOfOps() throws Exception {
//
//        MockCacheService mcs = new MockCacheService()
//        {
//            @Override
//            public String next(String key)
//            {
//                // we override next() to return a hit for any key
//                return "SYNTHETIC-HIT";
//            }
//        };
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setKeySize(1);
//        mc.setValueSize(1);
//        mc.setUseDifferentValues(false);
//        mc.setService(mcs);
//        mc.setCommand(new Load(mc, new ArrayList<>(Arrays.asList("--max-operations", "1")), 0));
//
//        ReadThenWriteOnMissLoadStrategy rtwom = new ReadThenWriteOnMissLoadStrategy();
//        rtwom.configure(mc, Collections.<String>emptyList(), 0);
//        assertTrue(rtwom.getKeyProvider() instanceof RandomKeyProvider);
//
//        MockSampler ms = new MockSampler();
//        CyclicBarrier barrier = new CyclicBarrier(1);
//        SingleThreadedRunner st = new SingleThreadedRunner("TEST", mc, rtwom, ms, barrier, new AtomicBoolean(false));
//        SingleThreadedRunnerTest.setRunning(st);
//
//        st.run();
//
//        List<OperationThrowablePair> recorded = ms.getRecorded();
//
//        // we should record a read and a write
//        assertEquals(1, recorded.size());
//
//        Read r = (Read)recorded.next(0).operation;
//        assertNull(recorded.next(0).throwable);
//        assertTrue(r.hasBeenPerformed());
//        assertNotNull(r.getKey());
//        assertEquals("SYNTHETIC-HIT", r.getValue());
//    }

//    @Test
//    public void integration_ReadThenWriteOnMiss_SingleThreadedRunner_ReadThenWrite() throws Exception {
//
//        MockCacheService mcs = new MockCacheService();
//        MockConfiguration mc = new MockConfiguration();
//        mc.setKeySize(1);
//        mc.setValueSize(1);
//        mc.setUseDifferentValues(false);
//        mc.setService(mcs);
//        mc.setCommand(new Load(mc, new ArrayList<>(Arrays.asList("--max-operations", "1")), 0));
//
//        ReadThenWriteOnMissLoadStrategy rtwom = new ReadThenWriteOnMissLoadStrategy();
//        rtwom.configure(mc, Collections.<String>emptyList(), 0);
//        assertTrue(rtwom.getKeyProvider() instanceof RandomKeyProvider);
//
//        MockSampler ms = new MockSampler();
//        CyclicBarrier barrier = new CyclicBarrier(1);
//        SingleThreadedRunner st = new SingleThreadedRunner("TEST", mc, rtwom, ms, barrier, new AtomicBoolean(false));
//        SingleThreadedRunnerTest.setRunning(st);
//
//        st.run();
//
//        List<OperationThrowablePair> recorded = ms.getRecorded();
//
//        // we should record a read and a write
//        assertEquals(2, recorded.size());
//
//        Read r = (Read)recorded.next(0).operation;
//        assertNull(recorded.next(0).throwable);
//        assertTrue(r.hasBeenPerformed());
//        String key = r.getKey();
//        assertNotNull(key);
//        assertNull(r.getValue());
//
//        Write w = (Write)recorded.next(1).operation;
//        assertNull(recorded.next(1).throwable);
//        assertTrue(w.isSuccessful());
//        String value = w.getValue();
//
//        //
//        // make sure the key was written in cache
//        //
//
//        assertEquals(value, mcs.next(key));
//    }


    @Test
    public void reuseValueBehavior_reuseValueIsSet() throws Exception {

        ReadThenWriteOnMissLoadStrategy s = getLoadStrategyToTest();

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockCacheServiceConfiguration sc = new MockCacheServiceConfiguration();
        Map<String, Object> m = new HashMap<>();
        m.put(LoadStrategy.NAME_LABEL, s.getName());
        m.put(ReadThenWriteOnMissLoadStrategy.REUSE_VALUE_LABEL, true);
        sc.set(m, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);

        s.init(sc, mlc);

        assertTrue(s.isReuseValue());

        Read read = (Read)s.next(null, null, false);
        Write write = (Write)s.next(read, null, false);
        String value = write.getValue();
        Read read2 = (Read)s.next(write, null, false);
        Write write2 = (Write)s.next(read2, null, false);
        String value2 = write2.getValue();

        assertEquals(value, value2);
    }

    @Test
    public void reuseValueBehavior_reuseValueIsNotSet() throws Exception {

        ReadThenWriteOnMissLoadStrategy s = getLoadStrategyToTest();

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockCacheServiceConfiguration sc = new MockCacheServiceConfiguration();
        Map<String, Object> m = new HashMap<>();
        m.put(LoadStrategy.NAME_LABEL, s.getName());
        m.put(ReadThenWriteOnMissLoadStrategy.REUSE_VALUE_LABEL, false);
        sc.set(m, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);

        s.init(sc, mlc);

        assertFalse(s.isReuseValue());

        Read read = (Read)s.next(null, null, false);
        Write write = (Write)s.next(read, null, false);
        String value = write.getValue();
        Read read2 = (Read)s.next(write, null, false);
        Write write2 = (Write)s.next(read2, null, false);
        String value2 = write2.getValue();

        assertNotEquals(value, value2);
    }

    // factory ---------------------------------------------------------------------------------------------------------

    @Test
    public void factory() throws Exception {

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        msc.set(new HashMap<>(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        msc.set(ReadThenWriteOnMissLoadStrategy.NAME,
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL,
                LoadStrategy.NAME_LABEL);


        ReadThenWriteOnMissLoadStrategy s = (ReadThenWriteOnMissLoadStrategy) LoadStrategyFactory.build(msc, mlc);
        assertEquals(ReadThenWriteOnMissLoadStrategy.NAME, s.getName());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected ReadThenWriteOnMissLoadStrategy getLoadStrategyToTest() throws Exception {

        return new ReadThenWriteOnMissLoadStrategy();
    }

    @Override
    protected MockCacheServiceConfiguration getCorrespondingServiceConfiguration() {

        MockCacheServiceConfiguration c = new MockCacheServiceConfiguration();
        c.set(new HashMap<String, Object>(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        return c;
    }

    @Override
    protected void initialize(LoadStrategy ls, MockServiceConfiguration msc) throws Exception {

        assertTrue(ls instanceof ReadThenWriteOnMissLoadStrategy);
        assertTrue(msc instanceof MockCacheServiceConfiguration);
        msc.set(ls.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);
        ls.init(msc, new MockLoadConfiguration());
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
