/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy;

/**
 * Immutable filter and pagination parameters for listing sklep records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 *
 * <p><b>Note:</b> The {@code nr_domu}, {@code nr_lokalu}, {@code poczta}, and {@code kraj.id}
 * filters are documented in the API spec but broken server-side (returns 400 par_niewlasciwe).
 * Removed per ADR-031.
 * @since 1.0.0
 */
public final class SklepQueryBuilder {

    private final Integer start;
    private final String fts;
    private final String id;
    private final String nazwa;
    private final String nip;
    private final String skrot;
    private final String numer;
    private final String ulica;
    private final String kodPoczt;
    private final String miasto;
    private final String telefon;
    private final String email;
    private final Boolean aktywny;

    private SklepQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.fts = builder.fts;
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.nip = builder.nip;
        this.skrot = builder.skrot;
        this.numer = builder.numer;
        this.ulica = builder.ulica;
        this.kodPoczt = builder.kodPoczt;
        this.miasto = builder.miasto;
        this.telefon = builder.telefon;
        this.email = builder.email;
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
    /** Tax identification number (NIP). */
    public String nip() { return nip; }
    /** Short name / abbreviation (skrot). */
    public String skrot() { return skrot; }
    /**
     * Store number. Integer value.
     * Supports list or range format (e.g. {@code "1"}, {@code "1,2"}, {@code "min1"}).
     */
    public String numer() { return numer; }
    /** Street name (ulica). */
    public String ulica() { return ulica; }
    /** Postal code (kodPoczt). */
    public String kodPoczt() { return kodPoczt; }
    /** City (miasto). */
    public String miasto() { return miasto; }
    /** Phone number (telefon). */
    public String telefon() { return telefon; }
    /** Email address (email). */
    public String email() { return email; }
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
        b.nip = this.nip;
        b.skrot = this.skrot;
        b.numer = this.numer;
        b.ulica = this.ulica;
        b.kodPoczt = this.kodPoczt;
        b.miasto = this.miasto;
        b.telefon = this.telefon;
        b.email = this.email;
        b.aktywny = this.aktywny;
        return b;
    }

    /**
     * Builder for {@link SklepQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String fts;
        private String id;
        private String nazwa;
        private String nip;
        private String skrot;
        private String numer;
        private String ulica;
        private String kodPoczt;
        private String miasto;
        private String telefon;
        private String email;
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
        /** Sets Tax identification number (NIP). @return this builder */
        public Builder nip(String nip) { this.nip = nip; return this; }
        /** Sets Short name / abbreviation (skrot). @return this builder */
        public Builder skrot(String skrot) { this.skrot = skrot; return this; }
        /** Sets Store number. @return this builder @see SklepQueryBuilder#numer() */
        public Builder numer(String numer) { this.numer = numer; return this; }
        /** Sets Street name (ulica). @return this builder */
        public Builder ulica(String ulica) { this.ulica = ulica; return this; }
        /** Sets Postal code (kodPoczt). @return this builder */
        public Builder kodPoczt(String kodPoczt) { this.kodPoczt = kodPoczt; return this; }
        /** Sets City (miasto). @return this builder */
        public Builder miasto(String miasto) { this.miasto = miasto; return this; }
        /** Sets Phone number (telefon). @return this builder */
        public Builder telefon(String telefon) { this.telefon = telefon; return this; }
        /** Sets Email address (email). @return this builder */
        public Builder email(String email) { this.email = email; return this; }
        /** Sets the active flag filter; see {@link SklepQueryBuilder#aktywny()} for soft-delete semantics. @return this builder */
        public Builder aktywny(Boolean aktywny) { this.aktywny = aktywny; return this; }

        /**
         * Builds the {@link SklepQueryBuilder}.
         *
         * @return a new {@link SklepQueryBuilder} instance
         */
        public SklepQueryBuilder build() { return new SklepQueryBuilder(this); }
    }
}
