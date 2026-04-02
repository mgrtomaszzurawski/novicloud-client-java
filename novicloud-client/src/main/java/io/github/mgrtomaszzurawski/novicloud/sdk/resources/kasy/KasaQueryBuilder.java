/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasy;

/**
 * Immutable filter and pagination parameters for listing kasa records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 *
 * <p><b>Note:</b> The {@code ecr} filter is documented as "Tekst, Fts" but broken server-side
 * (returns 400 par_niewlasciwe). Removed per ADR-031.
 * @since 1.0.0
 */
public final class KasaQueryBuilder {

    private final Integer start;
    private final String id;
    private final String nazwa;
    private final String numer;
    private final String ostatniaSync;
    private final Boolean aktywny;

    private KasaQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.numer = builder.numer;
        this.ostatniaSync = builder.ostatniaSync;
        this.aktywny = builder.aktywny;
    }

    /**
     * Creates a new builder with all fields unset.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() { return new Builder(); }

    /** Zero-based record offset for pagination (first record index on the page). */
    public Integer start() { return start; }
    /** Record ID. */
    public String id() { return id; }
    /** Name (nazwa). */
    public String nazwa() { return nazwa; }
    /**
     * Cash register number. Integer value.
     * Supports list or range format (e.g. {@code "1"}, {@code "1,2"}, {@code "min1"}).
     */
    public String numer() { return numer; }
    /** Last sync date filter (ostatniaSync). */
    public String ostatniaSync() { return ostatniaSync; }
    /** Active flag filter ({@code true} = active only, {@code false} = inactive only,
     *  {@code null} = no filter). */
    public Boolean aktywny() { return aktywny; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder();
        b.start = this.start;
        b.id = this.id;
        b.nazwa = this.nazwa;
        b.numer = this.numer;
        b.ostatniaSync = this.ostatniaSync;
        b.aktywny = this.aktywny;
        return b;
    }

    /**
     * Builder for {@link KasaQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String id;
        private String nazwa;
        private String numer;
        private String ostatniaSync;
        private Boolean aktywny;

        private Builder() {}

        /** Sets Zero-based record offset for pagination (first record index on the page). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }
        /** Sets Record ID. @return this builder */
        public Builder id(String id) { this.id = id; return this; }
        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Cash register number. @return this builder @see KasaQueryBuilder#numer() */
        public Builder numer(String numer) { this.numer = numer; return this; }
        /** Sets ostatniaSync value. @return this builder */
        public Builder ostatniaSync(String ostatniaSync) { this.ostatniaSync = ostatniaSync; return this; }
        /** Sets the active flag filter. @return this builder */
        public Builder aktywny(Boolean aktywny) { this.aktywny = aktywny; return this; }

        /**
         * Builds the {@link KasaQueryBuilder}.
         *
         * @return a new {@link KasaQueryBuilder} instance
         */
        public KasaQueryBuilder build() { return new KasaQueryBuilder(this); }
    }
}
