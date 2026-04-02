/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.stanymag;

/**
 * Immutable data transfer object for updating an existing stanmag record.
 * The {@code towarId} and {@code sklepId} fields identify the record to update.
 *
 * <p>Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class StanMagUpdateBuilder {

    private final String towarId;
    private final String sklepId;
    private final Double ilosc;

    private StanMagUpdateBuilder(Builder builder) {
        this.towarId = builder.towarId;
        this.sklepId = builder.sklepId;
        this.ilosc = builder.ilosc;
    }

    public static Builder builder(String towarId, String sklepId, Double ilosc) {
        return new Builder(towarId, sklepId, ilosc);
    }

    /** Towar ID link (towarId). */
    public String towarId() { return towarId; }
    /** Sklep (store) ID link (sklepId). */
    public String sklepId() { return sklepId; }
    /** Stock quantity (ilosc). */
    public Double ilosc() { return ilosc; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        return new Builder(this.towarId, this.sklepId, this.ilosc);
    }

    /**
     * Builder for {@link StanMagUpdateBuilder}.
     */
    public static final class Builder {
        private String towarId;
        private String sklepId;
        private Double ilosc;

        private Builder(String towarId, String sklepId, Double ilosc) {
            this.towarId = towarId;
            this.sklepId = sklepId;
            this.ilosc = ilosc;
        }

        /**
         * Builds the {@link StanMagUpdateBuilder}.
         *
         * @return a new {@link StanMagUpdateBuilder} instance
         */
        public StanMagUpdateBuilder build() { return new StanMagUpdateBuilder(this); }
    }
}
