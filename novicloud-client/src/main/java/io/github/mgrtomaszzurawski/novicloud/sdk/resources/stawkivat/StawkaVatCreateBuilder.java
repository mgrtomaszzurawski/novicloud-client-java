/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat;

/**
 * Immutable data transfer object for creating a new stawkavat record. Required: {@code id}.
 *
 * <p>Required fields are enforced by the builder factory method.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class StawkaVatCreateBuilder {

    private final Integer id;
    private final String opis;
    private final String etykieta;

    private StawkaVatCreateBuilder(Builder builder) {
        this.id = builder.id;
        this.opis = builder.opis;
        this.etykieta = builder.etykieta;
    }

    /**
     * Creates a new builder with the required fields pre-set.
     *
     * @param id  Record ID (required)
     * @return a new {@link Builder}
     */
    public static Builder builder(Integer id) { return new Builder(id); }

    /** Record ID. */
    public Integer id() { return id; }
    /** opis value. */
    public String opis() { return opis; }
    /** etykieta value. */
    public String etykieta() { return etykieta; }

    private static final String ERR_ID_NULL = "id must not be null";

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.id);
        b.opis = this.opis;
        b.etykieta = this.etykieta;
        return b;
    }

    /**
     * Builder for {@link StawkaVatCreateBuilder}.
     */
    public static final class Builder {
        private final Integer id;
        private String opis;
        private String etykieta;

        private Builder(Integer id) {
            if (id == null) {
                throw new IllegalArgumentException(ERR_ID_NULL);
            }
            this.id = id;
        }

        /** Sets opis value. @return this builder */
        public Builder opis(String opis) { this.opis = opis; return this; }
        /** Sets etykieta value. @return this builder */
        public Builder etykieta(String etykieta) { this.etykieta = etykieta; return this; }

        /**
         * Builds the {@link StawkaVatCreateBuilder}.
         *
         * @return a new {@link StawkaVatCreateBuilder} instance
         */
        public StawkaVatCreateBuilder build() { return new StawkaVatCreateBuilder(this); }
    }
}
