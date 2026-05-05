/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje;

import java.util.Objects;

/**
 * Immutable data transfer object for creating a new kraj record. Required: {@code nazwa}, {@code kod}.
 *
 * <p>Required fields are enforced by the builder factory method.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class KrajCreateBuilder {

    private final Long id;
    private final String nazwa;
    private final String kod;
    private final String walutaId;

    private KrajCreateBuilder(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.kod = builder.kod;
        this.walutaId = builder.walutaId;
    }

    /**
     * Creates a new builder with the required fields pre-set.
     *
     * @param nazwa  Name (nazwa) (required)
     * @param kod  Code (kod) (required)
     * @return a new {@link Builder}
     */
    public static Builder builder(String nazwa, String kod) { return new Builder(nazwa, kod); }

    /** Record ID. */
    public Long id() { return id; }
    /** Name (nazwa). */
    public String nazwa() { return nazwa; }
    /** Code (kod). */
    public String kod() { return kod; }
    /** Default currency ID link for this country (walutaId). */
    public String walutaId() { return walutaId; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.nazwa, this.kod);
        b.id = this.id;
        b.walutaId = this.walutaId;
        return b;
    }

    /**
     * Builder for {@link KrajCreateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private String kod;
        private String walutaId;

        private Builder(String nazwa, String kod) {
            this.nazwa = Objects.requireNonNull(nazwa, "nazwa must not be null");
            this.kod = Objects.requireNonNull(kod, "kod must not be null");
        }

        /** Sets Record ID. @return this builder */
        public Builder id(Long id) { this.id = id; return this; }
        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Code (kod). @return this builder */
        public Builder kod(String kod) { this.kod = kod; return this; }
        /** Sets Default currency ID link for this country (walutaId). @return this builder */
        public Builder walutaId(String walutaId) { this.walutaId = walutaId; return this; }

        /**
         * Builds the {@link KrajCreateBuilder}.
         *
         * @return a new {@link KrajCreateBuilder} instance
         */
        public KrajCreateBuilder build() { return new KrajCreateBuilder(this); }
    }
}
