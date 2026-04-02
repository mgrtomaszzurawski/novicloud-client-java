/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty;

/**
 * Immutable filter and pagination parameters for listing waluta records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 *
 * <p><b>Note:</b> The {@code domyslna} filter is documented as Boolean but is broken server-side
 * (returns 400 for both {@code true} and {@code false}). Removed per ADR-031.
 * @since 1.0.0
 */
public final class WalutaQueryBuilder {

    private final Integer start;
    private final String fts;
    private final String id;
    private final String nazwa;
    private final String kod;
    private final String kurs;
    private final Boolean aktywny;

    private WalutaQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.fts = builder.fts;
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.kod = builder.kod;
        this.kurs = builder.kurs;
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
    /** Full-text search filter; searches across key text fields. */
    public String fts() { return fts; }
    /** Record ID. */
    public String id() { return id; }
    /** Name (nazwa). */
    public String nazwa() { return nazwa; }
    /** Code (kod). */
    public String kod() { return kod; }
    /**
     * Exchange rate filter (kurs). Accepts numeric values with dot as decimal separator.
     * Supports list or range format (e.g. {@code "1"}, {@code "4.20"}, {@code "min1"}, {@code "max5"}).
     */
    public String kurs() { return kurs; }
    /** Active flag filter. Records with {@code aktywny = false} have been soft-deleted
     *  (DELETE sets this flag; rows are never physically removed). Use {@code true} to
     *  match the web panel default, which shows only active records. */
    public Boolean aktywny() { return aktywny; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder();
        b.start = this.start;
        b.fts = this.fts;
        b.id = this.id;
        b.nazwa = this.nazwa;
        b.kod = this.kod;
        b.kurs = this.kurs;
        b.aktywny = this.aktywny;
        return b;
    }

    /**
     * Builder for {@link WalutaQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String fts;
        private String id;
        private String nazwa;
        private String kod;
        private String kurs;
        private Boolean aktywny;

        private Builder() {}

        /** Sets Zero-based record offset for pagination (first record index on the page). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }
        /** Sets Full-text search filter; searches across key text fields. @return this builder */
        public Builder fts(String fts) { this.fts = fts; return this; }
        /** Sets Record ID. @return this builder */
        public Builder id(String id) { this.id = id; return this; }
        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Code (kod). @return this builder */
        public Builder kod(String kod) { this.kod = kod; return this; }
        /** Sets Exchange rate filter. @return this builder @see WalutaQueryBuilder#kurs() */
        public Builder kurs(String kurs) { this.kurs = kurs; return this; }
        /** Sets the active flag filter; see {@link WalutaQueryBuilder#aktywny()} for soft-delete semantics. @return this builder */
        public Builder aktywny(Boolean aktywny) { this.aktywny = aktywny; return this; }

        /**
         * Builds the {@link WalutaQueryBuilder}.
         *
         * @return a new {@link WalutaQueryBuilder} instance
         */
        public WalutaQueryBuilder build() { return new WalutaQueryBuilder(this); }
    }
}
