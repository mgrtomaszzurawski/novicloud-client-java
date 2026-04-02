/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary;

/**
 * Immutable filter and pagination parameters for listing towar records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 *
 * <p><b>Note:</b> The {@code typ} filter (values 0,2,4,5,6,7,8) is documented but broken
 * server-side (returns 400 par_bledna_wart). The {@code cenaDet} filter causes a server 500
 * (Hibernate property error). Both removed per ADR-031.
 * @since 1.0.0
 */
public final class TowarQueryBuilder {

    /** Pagination offset (0-based record index to start from). */
    private final Integer start;
    /** Full-text search across all text fields. */
    private final String fts;
    /** Filter by record ID (exact match). */
    private final String id;
    /** Filter by product name (partial match). */
    private final String nazwa;
    /** Filter by product code (partial match). */
    private final String kod;
    /** Filter by VAT rate ID. */
    private final String stawkaVat;
    /** Filter by excise duty flag. */
    private final Boolean akcyzowy;
    /** Filter by unit of measure (jmiary) ID. */
    private final String jmId;
    /** Filter by assortment (asorty) ID. */
    private final String asortId;
    /** Filter by active flag; see {@link #aktywny()} for soft-delete semantics. */
    private final Boolean aktywny;

    private TowarQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.fts = builder.fts;
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.kod = builder.kod;
        this.stawkaVat = builder.stawkaVat;
        this.akcyzowy = builder.akcyzowy;
        this.jmId = builder.jmId;
        this.asortId = builder.asortId;
        this.aktywny = builder.aktywny;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer start() {
        return start;
    }

    public String fts() {
        return fts;
    }

    public String id() {
        return id;
    }

    public String nazwa() {
        return nazwa;
    }

    public String kod() {
        return kod;
    }

    public String stawkaVat() {
        return stawkaVat;
    }

    public Boolean akcyzowy() {
        return akcyzowy;
    }

    public String jmId() {
        return jmId;
    }

    public String asortId() {
        return asortId;
    }

    /** Active flag filter. Records with {@code aktywny = false} have been soft-deleted
     *  (DELETE sets this flag; rows are never physically removed). Use {@code true} to
     *  match the web panel default, which shows only active records. */
    public Boolean aktywny() {
        return aktywny;
    }

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
        b.stawkaVat = this.stawkaVat;
        b.akcyzowy = this.akcyzowy;
        b.jmId = this.jmId;
        b.asortId = this.asortId;
        b.aktywny = this.aktywny;
        return b;
    }

    /**
     * Builder for {@link TowarQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String fts;
        private String id;
        private String nazwa;
        private String kod;
        private String stawkaVat;
        private Boolean akcyzowy;
        private String jmId;
        private String asortId;
        private Boolean aktywny;

        private Builder() { }

        /** Pagination offset (0-based record index to start from). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }

        /** Full-text search across all text fields. @return this builder */
        public Builder fts(String fts) { this.fts = fts; return this; }

        /** Filter by record ID (exact match). @return this builder */
        public Builder id(String id) { this.id = id; return this; }

        /** Filter by product name (partial match). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }

        /** Filter by product code (partial match). @return this builder */
        public Builder kod(String kod) { this.kod = kod; return this; }

        /** Filter by VAT rate ID. @return this builder */
        public Builder stawkaVat(String stawkaVat) { this.stawkaVat = stawkaVat; return this; }

        /** Filter by excise duty flag. @return this builder */
        public Builder akcyzowy(Boolean akcyzowy) { this.akcyzowy = akcyzowy; return this; }

        /** Filter by unit of measure (jmiary) ID. @return this builder */
        public Builder jmId(String jmId) { this.jmId = jmId; return this; }

        /** Filter by assortment (asorty) ID. @return this builder */
        public Builder asortId(String asortId) { this.asortId = asortId; return this; }

        /** Active flag filter. Records with {@code aktywny = false} have been soft-deleted
         *  (DELETE sets this flag; rows are never physically removed). Use {@code true} to
         *  match the web panel default. @return this builder */
        public Builder aktywny(Boolean aktywny) { this.aktywny = aktywny; return this; }

        /**
         * Builds the {@link TowarQueryBuilder}.
         *
         * @return a new {@link TowarQueryBuilder} instance
         */
        public TowarQueryBuilder build() {
            return new TowarQueryBuilder(this);
        }
    }
}
