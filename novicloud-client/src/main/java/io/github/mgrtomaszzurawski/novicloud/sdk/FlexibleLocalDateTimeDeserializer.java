/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Jackson deserializer for {@link LocalDateTime} fields that the NoviCloud server may return
 * as either a full datetime string ({@code "2019-08-28T13:57:39"}) or a date-only string
 * ({@code "2019-08-27"}), depending on document type.
 *
 * <p>Date-only values are converted to {@code LocalDateTime} at the start of the day
 * ({@code 00:00:00}).
 *
 * <p>Registered globally on the shared {@link com.fasterxml.jackson.databind.ObjectMapper}
 * in {@link NoviCloudClient.Builder#build(String, String)}.
 * @since 1.0.0
 */
final class FlexibleLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    private static final String DATETIME_SEPARATOR = "T";

    FlexibleLocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim();
        if (value.contains(DATETIME_SEPARATOR)) {
            return LocalDateTime.parse(value);
        }
        return LocalDate.parse(value).atStartOfDay();
    }
}
