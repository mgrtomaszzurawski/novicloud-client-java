/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

/**
 * Immutable SDK model for the {@code jmiary} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record Jmiary(Long id, String nazwa, Precyzja precyzja) {

    /** Numeric code from the NoviCloud API. */
    public enum Precyzja {
        MINUS_2(-2),
        MINUS_1(-1),
        VALUE_0(0),
        VALUE_1(1),
        VALUE_2(2),
        VALUE_3(3);

        private final int code;
        Precyzja(int code) { this.code = code; }
        /** Returns the numeric code as used by the NoviCloud API. */
        public int code() { return code; }

        /** Resolves enum from API numeric code; returns {@code null} if unknown. */
        public static Precyzja fromCode(int code) {
            for (Precyzja v : values()) {
                if (v.code == code) { return v; }
            }
            return null;
        }
    }
}
