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

package io.novaordis.gld.api.sampler;

import io.novaordis.gld.api.AnotherTypeOfMockSamplerOperation;
import io.novaordis.gld.api.MockSamplerOperation;
import io.novaordis.gld.api.sampler.metrics.FreePhysicalMemorySize;
import io.novaordis.gld.api.sampler.metrics.Metric;
import io.novaordis.gld.api.sampler.metrics.SystemCpuLoad;
import io.novaordis.gld.api.sampler.metrics.SystemLoadAverage;
import io.novaordis.gld.api.sampler.metrics.TotalPhysicalMemorySize;
import io.novaordis.gld.api.statistics.CSVFormatter;
import io.novaordis.utilities.UserErrorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class SamplerTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(SamplerTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    protected File scratchDirectory;

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Before
    public void before() throws Exception {

        String projectBaseDirName = System.getProperty("basedir");
        scratchDirectory = new File(projectBaseDirName, "target/test-scratch");
        assertTrue(scratchDirectory.isDirectory());
    }

    @After
    public void after() throws Exception {

        //
        // scratch directory cleanup
        //

        assertTrue(io.novaordis.utilities.Files.rmdir(scratchDirectory, false));
    }

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void lifecycle() throws Exception {

        Sampler sampler = getSamplerToTest();

        assertFalse(sampler.isStarted());

        long defaultSamplingInterval = sampler.getSamplingIntervalMs();

        assertTrue(defaultSamplingInterval > 0);

        try {

            sampler.record(0L, 0L, 0L, new MockSamplerOperation());
            fail("should throw IllegalStateException on account of calling record() on a stopped sampler");
        }
        catch(IllegalStateException e) {
            log.info(e.getMessage());
        }

        Counter counter = sampler.registerOperation(MockSamplerOperation.class);
        assertNotNull(counter);

        assertTrue(sampler.getConsumers().isEmpty());

        MockSamplingConsumer msc = new MockSamplingConsumer();
        assertTrue(sampler.registerConsumer(msc));

        List<SamplingConsumer> consumer = sampler.getConsumers();
        assertEquals(1, consumer.size());
        assertEquals(msc, consumer.get(0));

        long testSamplingTaskRunInterval = 250L;
        sampler.setSamplingTaskRunIntervalMs(testSamplingTaskRunInterval);
        assertEquals(testSamplingTaskRunInterval, sampler.getSamplingTaskRunIntervalMs());

        long testSamplingInterval = 500L;
        sampler.setSamplingIntervalMs(testSamplingInterval);
        assertEquals(testSamplingInterval, sampler.getSamplingIntervalMs());

        sampler.start();

        assertTrue(sampler.isStarted());

        // test idempotency
        sampler.start();

        // send an operation. One operation only.

        sampler.record(0L, 0L, 7L, new MockSamplerOperation());

        // wait 2 x sampling interval + 100ms, to make sure the operation is accounted for
        long sleep = 2 * testSamplingInterval + 100L;
        log.info("sleeping for " + sleep + " ms ...");
        Thread.sleep(sleep);

        sampler.stop();

        assertFalse(sampler.isStarted());

        // test idempotency
        sampler.stop();

        assertFalse(sampler.isStarted());

        // try one more record()

        try {

            sampler.record(0L, 0L, 0L, new MockSamplerOperation());
            fail("should throw IllegalStateException on account of calling record() on a stopped sampler");
        }
        catch(IllegalStateException e) {
            log.info(e.getMessage());
        }

        final List<SamplingInterval> samplingIntervals = msc.getSamplingIntervals();

        // make sure the generated samples reflect the one operation sent into the sampler
        assertFalse(samplingIntervals.isEmpty());

        int totalCount = 0;

        for(SamplingInterval si: samplingIntervals) {

            totalCount += si.getCounterValues(MockSamplerOperation.class).getSuccessCount();
        }

        assertEquals(1, totalCount);
    }

    @Test
    public void unknownOperation() throws Exception {

        Sampler sampler = getSamplerToTest();

        sampler.registerOperation(MockSamplerOperation.class);

        sampler.start();

        try {
            // try recording an unknown operation
            sampler.record(0L, 0L, 0L, new AnotherTypeOfMockSamplerOperation());
            fail("should throw IllegalArgumentException on account of trying to register an unknown operation");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
        finally {
            sampler.stop();
        }
    }

    @Test
    public void samplingIntervalSmallerThanSamplingTaskRunInterval() throws Exception {

        Sampler sampler = getSamplerToTest();

        sampler.setSamplingIntervalMs(2000L);

        try {
            sampler.setSamplingTaskRunIntervalMs(3000L);
            fail("should throw IllegalArgumentException because of invalid relationship between sampling intervals");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
        finally {
            sampler.stop();
        }
    }

    @Test
    public void samplingIntervalEqualsThanSamplingTaskRunInterval() throws Exception {

        Sampler sampler = getSamplerToTest();

        sampler.setSamplingIntervalMs(2000L);

        try {
            sampler.setSamplingTaskRunIntervalMs(2000L);
            fail("should throw IllegalArgumentException because of invalid relationship between sampling intervals");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
        finally {
            sampler.stop();
        }
    }

    @Test
    public void attemptToRegisterAnOperationAfterTheSamplerWasStarted() throws Exception {
        Sampler sampler = getSamplerToTest();

        sampler.registerOperation(MockSamplerOperation.class);

        sampler.start();

        try {
            sampler.registerOperation(AnotherTypeOfMockSamplerOperation.class);
            fail("should throw IllegalStateException on account of trying to register an operation after startup");
        }
        catch(IllegalStateException e) {
            log.info(e.getMessage());
        }
        finally {
            sampler.stop();
        }
    }

    @Test
    public void cantModifyTheSamplingIntervalAfterSamplerWasStarted() throws Exception {
        Sampler s = getSamplerToTest();

        s.registerOperation(MockSamplerOperation.class);

        s.start();

        assertTrue(s.isStarted());

        try {
            s.setSamplingIntervalMs(100L);
            fail("should fail, can't modify the sampling interval after the sampler was started");
        }
        catch(IllegalStateException e) {
            log.info(e.getMessage());
        }

        s.stop();

        assertFalse(s.isStarted());
    }

    @Test
    public void cantModifyTheSamplingThreadRunIntervalAfterSamplerWasStarted() throws Exception {
        Sampler s = getSamplerToTest();

        s.registerOperation(MockSamplerOperation.class);

        s.start();

        assertTrue(s.isStarted());

        try {
            s.setSamplingTaskRunIntervalMs(100L);
            fail("should fail, can't modify the sampling thread run interval after the sampler was started");
        }
        catch(IllegalStateException e) {
            log.info(e.getMessage());
        }

        s.stop();

        assertFalse(s.isStarted());
    }

    @Test
    public void attemptToStartTheSamplerWithoutAnyRegisteredOperation() throws Exception {
        Sampler sampler = getSamplerToTest();

        try {
            sampler.start();
            fail("should throw IllegalStateException on account of trying to start without any registered operation");
        }
        catch(IllegalStateException e) {
            log.info(e.getMessage());
        }
        finally {
            sampler.stop();
        }
    }

    @Test
    public void successfulOperationRegistration() throws Exception {
        Sampler sampler = getSamplerToTest();

        Counter counter = sampler.registerOperation(MockSamplerOperation.class);
        Counter counter2 = sampler.registerOperation(AnotherTypeOfMockSamplerOperation.class);

        assertEquals(counter, sampler.getCounter(MockSamplerOperation.class));
        assertEquals(counter2, sampler.getCounter(AnotherTypeOfMockSamplerOperation.class));
    }

    @Test
    public void annotate() throws Exception {
        Sampler sampler = getSamplerToTest();
        sampler.registerOperation(MockSamplerOperation.class);
        MockSamplingConsumer msc = new MockSamplingConsumer();
        sampler.registerConsumer(msc);

        sampler.start();

        sampler.annotate("test");

        sampler.stop();

        List<SamplingInterval> sil = msc.getSamplingIntervals();

        // at least one of the samples has the comment, but not more than one
        String annotation = null;
        for(SamplingInterval si: sil) {
            List<String> annotations = si.getAnnotations();
            assertTrue(annotations.size() <= 1);
            if (annotations.isEmpty()) {
                continue;
            }

            if (annotation != null) {
                fail("annotation found twice: " + annotation + ", " + annotations.get(0));
            }

            annotation = annotations.get(0);
        }

        assertEquals("test", annotation);
    }

    @Test
    public void stop_waitForAFullIntervalAndGenerateAFinalSample() throws Exception {
        Sampler sampler = getSamplerToTest();
        sampler.registerOperation(MockSamplerOperation.class);

        MockSamplingConsumer msc = new MockSamplingConsumer();
        sampler.registerConsumer(msc);
        sampler.start();

        sampler.annotate("test");
        sampler.record(0L, 1L, 2L, new MockSamplerOperation());

        sampler.stop();

        List<SamplingInterval> sil = msc.getSamplingIntervals();

        assertFalse(sil.isEmpty());

        int count = 0;
        String annotation = null;

        for(SamplingInterval i: sil) {
            count += i.getCounterValues(MockSamplerOperation.class).getSuccessCount();

            for(String a: i.getAnnotations()) {
                if (annotation != null) {
                    fail("more than one annotation");
                }

                annotation = a;
            }
        }

        assertEquals(1, count);
        assertEquals("test", annotation);
    }

    // metrics ---------------------------------------------------------------------------------------------------------

    @Test
    public void registerDuplicateMetricType() throws Exception {

        Sampler sampler = getSamplerToTest();

        assertTrue(sampler.registerMetric(SystemCpuLoad.class));
        assertFalse(sampler.registerMetric(SystemCpuLoad.class));
    }

    @Test
    public void registerAndReadMetrics() throws Exception {

        Sampler sampler = getSamplerToTest();
        long samplingTaskRunInterval = 10L;
        long samplingInterval = 11L;
        sampler.setSamplingTaskRunIntervalMs(samplingTaskRunInterval);
        sampler.setSamplingIntervalMs(samplingInterval);
        sampler.registerOperation(MockSamplerOperation.class);

        MockSamplingConsumer msc = new MockSamplingConsumer();
        sampler.registerConsumer(msc);

        assertTrue(sampler.registerMetric(SystemCpuLoad.class));
        assertTrue(sampler.registerMetric(SystemLoadAverage.class));
        assertTrue(sampler.registerMetric(FreePhysicalMemorySize.class));
        assertTrue(sampler.registerMetric(TotalPhysicalMemorySize.class));

        sampler.start();

        sampler.record(0L, 1L, 2L, new MockSamplerOperation());

        // wait to next some samples
        Thread.sleep(2 * samplingInterval);

        sampler.stop(); // this will populate the sampler interval

        List<SamplingInterval> sil = msc.getSamplingIntervals();

        assertFalse(sil.isEmpty());

        for(SamplingInterval si: sil) {
            // sampling interval instances must have system-wide metrics

            Set<Metric> metrics = si.getMetrics();
            assertEquals(4, metrics.size());

            for(Metric m: metrics) {
                assertTrue(m.getValue() != null);
                log.info("" + m);
            }
        }
    }

    // stop ------------------------------------------------------------------------------------------------------------

    @Test
    public void verifyThatStopPropagatesToConsumers() throws Exception {

        Sampler s = getSamplerToTest();

        s.registerOperation(MockSamplerOperation.class);

        MockSamplingConsumer consumer1 = new MockSamplingConsumer();
        MockSamplingConsumer consumer2 = new MockSamplingConsumer();

        assertTrue(s.registerConsumer(consumer1));
        assertTrue(s.registerConsumer(consumer2));

        s.start();

        assertTrue(s.isStarted());

        assertFalse(consumer1.wasStopped());
        assertFalse(consumer2.wasStopped());

        consumer1.setStopBroken(true);

        s.stop();

        assertFalse(s.isStarted());

        assertTrue(consumer1.wasStopped());
        assertTrue(consumer2.wasStopped());
    }

    // configure() -----------------------------------------------------------------------------------------------------

    @Test
    public void configure() throws Exception {

        Sampler s = getSamplerToTest();

        File file = new File(scratchDirectory, "test.csv");
        assertFalse(file.isFile());

        MockSamplerConfiguration mc = new MockSamplerConfiguration();
        mc.setSamplingInterval(2222);
        mc.setSamplingTaskRunInterval(333);
        mc.setFile(file);
        mc.setMetrics(Arrays.asList(
                "TotalPhysicalMemorySize", "FreePhysicalMemorySize", "SystemCpuLoad", "SystemLoadAverage"));

        s.configure(mc);

        assertEquals(2222, s.getSamplingIntervalMs());
        assertEquals(333, s.getSamplingTaskRunIntervalMs());
        assertTrue(file.isFile());

        Set<Class<? extends Metric>> metricTypes = s.getMetricTypes();
        assertEquals(4, metricTypes.size());
        assertTrue(metricTypes.contains(TotalPhysicalMemorySize.class));
        assertTrue(metricTypes.contains(FreePhysicalMemorySize.class));
        assertTrue(metricTypes.contains(SystemCpuLoad.class));
        assertTrue(metricTypes.contains(SystemLoadAverage.class));

        List<SamplingConsumer> consumers = s.getConsumers();
        assertEquals(1, consumers.size());
        CSVFormatter c = (CSVFormatter)consumers.get(0);
        assertNotNull(c);
    }

    @Test
    public void configure_InvalidMetric() throws Exception {

        Sampler s = getSamplerToTest();

        File file = new File(scratchDirectory, "test.csv");

        MockSamplerConfiguration mc = new MockSamplerConfiguration();
        mc.setFile(file);
        mc.setMetrics(Collections.singletonList("NoSuchMetric"));

        try {

            s.configure(mc);
            fail("should have thrown exception");

        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("invalid metric type NoSuchMetric.*"));
        }
    }

    @Test
    public void configure_AttemptToWriteADirectory() throws Exception {

        File d = new File(scratchDirectory, "test-dir");
        assertTrue(d.mkdir());
        assertTrue(d.isDirectory());

        MockSamplerConfiguration mc = new MockSamplerConfiguration();
        mc.setFile(d);

        Sampler s = getSamplerToTest();

        try {

            s.configure(mc);
            fail("should have thrown exception");

        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("cannot write file .*, it is either a directory or wrong permissions are in place.*"));
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract Sampler getSamplerToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
