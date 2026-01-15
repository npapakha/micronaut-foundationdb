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

import com.apple.foundationdb.EventKeeper.Event;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;


/**
 * Provides generic metadata for a given {@link Event}.
 *
 * @author Nikolai Papakha
 */
@Internal
@Singleton
final class EventMetadataProvider {

    static final String PREFIX = "foundationdb";

    String getMeterName(@NonNull Event event) {
        StringBuilder meterName = new StringBuilder(PREFIX).append('.');
        char[] chars = event.name().toCharArray();
        for (char c : chars) {
            if (c == '_') {
                meterName.append('.');
            } else {
                meterName.append(Character.toLowerCase(c));
            }
        }
        return meterName.toString();
    }

    String getCountDescription(Event event) {
        return "The number of times the " + event.name() + " event occurred";
    }

    String getTimerDescription(Event event) {
        return "The time taken to perform the " + event.name() + " event";
    }
}
