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

package com.novaordis.gld.statistics;

import com.novaordis.gld.RedisFailure;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Deprecated
public class SamplingInterval
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Object RENDEZVOUS_TOKEN = new Object();

    // Static ----------------------------------------------------------------------------------------------------------

    public static String getCsvHeaders()
    {
        return toCsvLine(
            true, -1L,
            null, null, null, null, null,
            RedisFailure.EMPTY_COUNTERS,
            null, null, null, null,
            null, null,
            null);
    }

    /**
     * This method can be used to:
     *
     * 1) generate headers (set 'headers' on true)
     * 2) generate empty lines (set 'headers' on false and everything else on null)
     * 3) generate a data CSV line - with or without comments.
     */
    public static String toCsvLine(boolean headers,
                                   long intervalStartMs,
                                   Long validReadsCount,
                                   Long readHitsCount,
                                   Long validWritesCount,
                                   Long cumulatedValidReadsTimeNano,
                                   Long cumulatedValidWritesTimeNano,
                                   Long[] failureCounters,
                                   Double systemLoadAverage,
                                   Double systemCpuLoad,
                                   Double processCpuLoad,
                                   Double processCpuTime,
                                   Long usedHeap,
                                   Long committedHeap,
                                   String comment)
    {
        StringBuilder sb = new StringBuilder();

        //
        // time
        //

        if (headers)
        {
            sb.append("time");
        }
        else
        {
            sb.append(CollectorBasedCsvStatistics.TIMESTAMP_FORMAT_MS.format(intervalStartMs));
        }

        sb.append(", ");

        //
        // throughput (ops/sec)
        //

        if (headers)
        {
            sb.append("throughput (ops/sec)");
        }
        else if (validReadsCount != null || validWritesCount != null)
        {
            long throughput =
                (validReadsCount == null ? 0L : validReadsCount) +
                    (validWritesCount == null ? 0L : validWritesCount);

            sb.append(throughput);
        }

        sb.append(", ");

        //
        // reads/sec
        //

        if (headers)
        {
            sb.append("reads/sec");
        }
        else if (validReadsCount != null)
        {
            sb.append(validReadsCount);
        }

        sb.append(", ");

        //
        // hits/sec
        //

        if (headers)
        {
            sb.append("hits/sec");
        }
        else if (readHitsCount != null)
        {
            sb.append(readHitsCount);
        }

        sb.append(", ");

        //
        // hits (%)
        //

        if (headers)
        {
            sb.append("hits (%)");
        }
        else if (validReadsCount != null && validReadsCount != 0 && readHitsCount != null)
        {
            double hits = ((double)readHitsCount/validReadsCount);
            sb.append(CollectorBasedCsvStatistics.PERCENTAGE.format(hits));
        }

        sb.append(", ");

        //
        // writes/sec
        //

        if (headers)
        {
            sb.append("writes/sec");
        }
        else if (validWritesCount != null)
        {
            sb.append(validWritesCount);
        }

        sb.append(", ");

        sb.append("   ");

        //
        // "average read duration (ms)"
        //

        if (headers)
        {
            sb.append("average read duration (ms)");
        }
        else if (validReadsCount != null && validReadsCount > 0)
        {
            double averageReadTime = cumulatedValidReadsTimeNano / validReadsCount / CollectorBasedCsvStatistics.NANOS_IN_MILLS;

            if (averageReadTime >= 0)
            {
                sb.append(CollectorBasedCsvStatistics.DURATION_MS_FORMAT.format(averageReadTime));
            }
        }

        sb.append(", ");

        //
        // average write duration (ms)
        //

        if (headers)
        {
            sb.append("average write duration (ms)");
        }
        else if (validWritesCount != null && validWritesCount > 0)
        {
            double averageWriteTime = cumulatedValidWritesTimeNano / validWritesCount / CollectorBasedCsvStatistics.NANOS_IN_MILLS;

            if (averageWriteTime >= 0)
            {
                sb.append(CollectorBasedCsvStatistics.DURATION_MS_FORMAT.format(averageWriteTime));
            }
        }

        sb.append(", ");

        sb.append("   ");

        //
        // failure counters
        //

        for(int i = 0; i < failureCounters.length; i ++)
        {
            if (headers)
            {
                sb.append(RedisFailure.toHeader(i));

            }
            else if (failureCounters[i] != null)
            {
                sb.append(failureCounters[i]);
            }

            sb.append(", ");
        }

        sb.append("   ");

        //
        // system load average
        //

        if (headers)
        {
            sb.append("system load average");
        }
        else if (systemLoadAverage != null)
        {
            sb.append(CollectorBasedCsvStatistics.LOAD_FORMAT.format(systemLoadAverage));
        }

        sb.append(", ");

        //
        // system cpu load
        //

        if (headers)
        {
            sb.append("system cpu load");
        }
        else if (systemCpuLoad != null)
        {
            sb.append(CollectorBasedCsvStatistics.LOAD_FORMAT.format(systemCpuLoad));
        }

        sb.append(", ");

        //
        // process cpu load
        //

        if (headers)
        {
            sb.append("process cpu load");
        }
        else if (processCpuLoad != null)
        {
            sb.append(CollectorBasedCsvStatistics.LOAD_FORMAT.format(processCpuLoad));
        }

        sb.append(", ");

        //
        // process cpu time
        //

        //sb.append(headers ? "process cpu time" : Statistics.LOAD_FORMAT.format(processCpuTime));

        //
        // used heap (MB)
        //

        if (headers)
        {
            sb.append("used heap (MB)");
        }
        else if (usedHeap != null)
        {
            sb.append(CollectorBasedCsvStatistics.MEMORY_MB_FORMAT.format((double) usedHeap / CollectorBasedCsvStatistics.BYTES_IN_MB));
        }

        sb.append(", ");

        //
        // committed heap (MB)
        //

        if (headers)
        {
            sb.append("committed heap (MB)");
        }
        else if (committedHeap != null)
        {
            sb.append(CollectorBasedCsvStatistics.MEMORY_MB_FORMAT.format((double)committedHeap / CollectorBasedCsvStatistics.BYTES_IN_MB));
        }

        sb.append(", ");

        //
        // comment
        //

        if (headers)
        {
            sb.append("comments");
        }
        else if (comment != null)
        {
            sb.append(comment);
        }

        return sb.toString();
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private long intervalStartMs;

    private long validReadsCount;
    private long readHitsCount;
    private long validWritesCount;

    private long cumulatedValidReadsTimeNano;
    private long cumulatedValidWritesTimeNano;

    /**
     * An array with failure counters, indexed by failure type.
     * @see RedisFailure
     */
    private long[] failureCounters;

    private double systemLoadAverage;
    private double systemCpuLoad;
    private double processCpuLoad;
    private double processCpuTime;

    private long usedHeap; // in bytes
    private long committedHeap; // in bytes

    private String comments;

    private final BlockingQueue<Object> rendezvous = new ArrayBlockingQueue<>(1);

    // Constructors ----------------------------------------------------------------------------------------------------

    public SamplingInterval(long intervalStartMs,
                            long validReadsCount,
                            long readHitsCount,
                            long validWritesCount,
                            long cumulatedValidReadsTimeNano,
                            long cumulatedValidWritesTimeNano,
                            long[] failureCounters,
                            double systemLoadAverage,
                            double systemCpuLoad,
                            double processCpuLoad,
                            double processCpuTime,
                            long usedHeap,
                            long committedHeap)
    {
        this.intervalStartMs = intervalStartMs;
        this.validReadsCount = validReadsCount;
        this.readHitsCount = readHitsCount;
        this.validWritesCount = validWritesCount;
        this.cumulatedValidReadsTimeNano = cumulatedValidReadsTimeNano;
        this.cumulatedValidWritesTimeNano = cumulatedValidWritesTimeNano;
        this.failureCounters = new long[failureCounters.length];
        System.arraycopy(failureCounters, 0, this.failureCounters, 0, failureCounters.length);
        this.systemLoadAverage = systemLoadAverage;
        this.systemCpuLoad = systemCpuLoad;
        this.processCpuLoad = processCpuLoad;
        this.processCpuTime = processCpuTime;
        this.usedHeap = usedHeap;
        this.committedHeap = committedHeap;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Start timestamp, in milliseconds.
     */
    public long getIntervalStartMs()
    {
        return intervalStartMs;
    }

    public long getValidOperationsCount()
    {
        return validReadsCount + validWritesCount;
    }

    public long getValidReadsCount()
    {
        return validReadsCount;
    }

    public long getValidWritesCount()
    {
        return validWritesCount;
    }

    public long getCumulatedValidReadsTimeNano()
    {
        return cumulatedValidReadsTimeNano;
    }

    public long getCumulatedValidWritesTimeNano()
    {
        return cumulatedValidWritesTimeNano;
    }

    public long[] getFailureCounters()
    {
        return failureCounters;
    }

    @Override
    public String toString()
    {
        Long[] fc = new Long[failureCounters.length];
        for(int i = 0; i < failureCounters.length; i ++)
        {
            fc[i] = failureCounters[i];
        }
        return toCsvLine(false, intervalStartMs, validReadsCount, readHitsCount, validWritesCount,
            cumulatedValidReadsTimeNano, cumulatedValidWritesTimeNano, fc, systemLoadAverage,
            systemCpuLoad, processCpuLoad, processCpuTime, usedHeap, committedHeap, comments);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    void waitUntilProcessed()
    {
        while(true)
        {
            try
            {
                Object o = rendezvous.take();

                if (o != null)
                {
                    return;
                }

            }
            catch (InterruptedException e)
            {
                // noop, fine
            }
        }

    }

    void markProcessed()
    {
        try
        {
            rendezvous.put(RENDEZVOUS_TOKEN);
        }
        catch(InterruptedException e)
        {
            throw new IllegalStateException("failed to mark " + this + " as processed", e);
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
