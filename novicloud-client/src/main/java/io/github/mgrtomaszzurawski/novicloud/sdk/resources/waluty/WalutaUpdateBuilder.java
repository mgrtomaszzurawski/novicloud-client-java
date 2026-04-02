/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty;

/**
 * Immutable data transfer object for updating an existing waluta record.
 * The {@code id} field identifies the record to update and is required.
 *
 * <p>Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class WalutaUpdateBuilder {

    private final Long id;
    private final String nazwa;
    private final String kod;
    private final Double kurs;
    private final Boolean domyslna;
    private final Boolean aktywny;

    private WalutaUpdateBuilder(Builder builder) {
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
     * @param id  Record ID (required)
     * @return a new {@link Builder}
     */
    public static Builder builder(Long id) { return new Builder(id); }

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
        Builder b = new Builder(this.id);
        b.nazwa = this.nazwa;
        b.kod = this.kod;
        b.kurs = this.kurs;
        b.domyslna = this.domyslna;
        b.aktywny = this.aktywny;
        return b;
    }

    /**
     * Builder for {@link WalutaUpdateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private String kod;
        private Double kurs;
        private Boolean domyslna;
        private Boolean aktywny;

        private Builder(Long id) { this.id = id; }

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
         * Builds the {@link WalutaUpdateBuilder}.
         *
         * @return a new {@link WalutaUpdateBuilder} instance
         */
        public WalutaUpdateBuilder build() { return new WalutaUpdateBuilder(this); }
    }
}
