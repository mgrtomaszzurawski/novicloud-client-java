/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty;

import java.util.Objects;

/**
 * Immutable data transfer object for creating a new waluta record. Required: {@code nazwa}, {@code kod}.
 *
 * <p>Required fields are enforced by the builder factory method.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class WalutaCreateBuilder {

    private final Long id;
    private final String nazwa;
    private final String kod;
    private final Double kurs;
    private final Boolean domyslna;
    private final Boolean aktywny;

    private WalutaCreateBuilder(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.kod = builder.kod;
        this.kurs = builder.kurs;
        this.domyslna = builder.domyslna;
        this.aktywny = builder.aktywny;
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
    /** Exchange rate (kurs). */
    public Double kurs() { return kurs; }
    /** Default currency flag (domyslna). */
    public Boolean domyslna() { return domyslna; }
    /** Active status flag ({@code true} for active, {@code false} for inactive, {@code null} for all). */
    public Boolean aktywny() { return aktywny; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.nazwa, this.kod);
        b.id = this.id;
        b.kurs = this.kurs;
        b.domyslna = this.domyslna;
        b.aktywny = this.aktywny;
        return b;
    }

    /**
     * Builder for {@link WalutaCreateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private String kod;
        private Double kurs;
        private Boolean domyslna;
        private Boolean aktywny;

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
        /** Sets Exchange rate (kurs). @return this builder */
        public Builder kurs(Double kurs) { this.kurs = kurs; return this; }
        /** Sets Default currency flag (domyslna). @return this builder */
        public Builder domyslna(Boolean domyslna) { this.domyslna = domyslna; return this; }
        /** Sets Active status flag ({@code true} for active, {@code false} for inactive, {@code null} for all). @return this builder */
        public Builder aktywny(Boolean aktywny) { this.aktywny = aktywny; return this; }

        /**
         * Builds the {@link WalutaCreateBuilder}.
         *
         * @return a new {@link WalutaCreateBuilder} instance
         */
        public WalutaCreateBuilder build() { return new WalutaCreateBuilder(this); }
    }
}
