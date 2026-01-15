/*
 * Copyright 2017-2026 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.foundationdb.metrics;

import com.apple.foundationdb.EventKeeper;
import io.micrometer.core.instrument.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


/**
 * Micrometer-based implementation of {@link EventKeeper} for FoundationDB metrics.
 * Pre-registers all known {@link Events} enum.
 *
 * @author Nikolai Papakha
 */
class MicrometerEventKeeper implements EventKeeper {

    private final ConcurrentMap<Event, Counter> counters = new ConcurrentHashMap<>();

    private final ConcurrentMap<Event, Timer> timers = new ConcurrentHashMap<>();

    private final EventMetadataProvider metadataProvider;

    private final MeterRegistry meterRegistry;

    MicrometerEventKeeper(EventMetadataProvider metadataProvider, MeterRegistry meterRegistry) {
        this.metadataProvider = metadataProvider;
        this.meterRegistry = meterRegistry;

        counters.put(Events.JNI_CALL,
            Counter.builder("foundationdb.jni.call")
                .description("Number of JNI calls made by the FoundationDB client")
                .tags(getTags(Events.JNI_CALL))
                .register(meterRegistry));

        counters.put(Events.BYTES_FETCHED,
            Counter.builder("foundationdb.bytes.fetched")
                .description("The total number of bytes pulled from the native layer")
                .tags(getTags(Events.BYTES_FETCHED))
                .baseUnit("bytes")
                .register(meterRegistry));

        counters.put(Events.RANGE_QUERY_DIRECT_BUFFER_HIT,
            Counter.builder("foundationdb.range.query.direct.buffer.hit")
                .description("The number of times a DirectBuffer was used to transfer a range query chunk")
                .tags(getTags(Events.RANGE_QUERY_DIRECT_BUFFER_HIT))
                .register(meterRegistry));

        counters.put(Events.RANGE_QUERY_DIRECT_BUFFER_MISS,
            Counter.builder("foundationdb.range.query.direct.buffer.miss")
                .description("The number of times a range query chunk was unable to use a DirectBuffer")
                .tags(getTags(Events.RANGE_QUERY_DIRECT_BUFFER_MISS))
                .register(meterRegistry));

        counters.put(Events.RANGE_QUERY_FETCHES, Counter.builder("foundationdb.range.query.fetches")
            .description("The number of direct fetches made during a range query")
            .tags(getTags(Events.RANGE_QUERY_FETCHES))
            .register(meterRegistry));

        counters.put(Events.RANGE_QUERY_RECORDS_FETCHED, Counter.builder("foundationdb.range.query.records.fetched")
            .description("The number of tuples fetched during a range query")
            .tags(getTags(Events.RANGE_QUERY_RECORDS_FETCHED))
            .register(meterRegistry));

        counters.put(Events.RANGE_QUERY_CHUNK_FAILED, Counter.builder("foundationdb.range.query.chunk.failed")
            .description("The number of times a range query chunk fetch failed")
            .tags(getTags(Events.RANGE_QUERY_CHUNK_FAILED))
            .register(meterRegistry));

        timers.put(Events.RANGE_QUERY_FETCH_TIME_NANOS, Timer.builder("foundationdb.range.query.fetch.time.nanos")
            .description("The time taken to perform an internal `getRange` fetch")
            .tags(getTags(Events.RANGE_QUERY_FETCH_TIME_NANOS))
            .register(meterRegistry));
    }

    @Override
    public void count(Event event, long amt) {
        counters.computeIfAbsent(event, this::getGenericCounter).increment(amt);
    }

    @Override
    public void timeNanos(Event event, long nanos) {
        timers.computeIfAbsent(event, this::getGenericTimer).record(nanos, TimeUnit.NANOSECONDS);
    }

    @Override
    public long getCount(Event event) {
        Counter counter = counters.get(event);
        return counter == null ? 0 : (long) counter.count();
    }

    @Override
    public long getTimeNanos(Event event) {
        Timer timer = timers.get(event);
        return timer == null ? 0 : (long) timer.totalTime(TimeUnit.NANOSECONDS);
    }

    private Counter getGenericCounter(Event event) {
        return Counter.builder(metadataProvider.getMeterName(event))
            .description(metadataProvider.getCountDescription(event))
            .tags(getTags(event))
            .register(meterRegistry);
    }

    private Timer getGenericTimer(Event event) {
        return Timer.builder(metadataProvider.getMeterName(event))
            .description(metadataProvider.getTimerDescription(event))
            .tags(getTags(event))
            .register(meterRegistry);
    }

    private Iterable<Tag> getTags(Event event) {
        return Tags.of("event", event.name());
    }
}
