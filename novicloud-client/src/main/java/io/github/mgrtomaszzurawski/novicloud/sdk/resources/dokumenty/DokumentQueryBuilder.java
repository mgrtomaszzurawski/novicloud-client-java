/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.dokumenty;

/**
 * Immutable filter and pagination parameters for listing dokument records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class DokumentQueryBuilder {

    private final Integer start;
    private final String id;
    private final String typDok;
    private final String dataWystawienia;
    private final String dataWplywu;
    private final String dataWykonania;
    private final String nrDok;
    private final String sklepId;
    private final String kontrahentId;
    private final String platnikId;
    private final String kasaId;
    private final String kasjerId;
    private final Boolean storno;

    private DokumentQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.id = builder.id;
        this.typDok = builder.typDok;
        this.dataWystawienia = builder.dataWystawienia;
        this.dataWplywu = builder.dataWplywu;
        this.dataWykonania = builder.dataWykonania;
        this.nrDok = builder.nrDok;
        this.sklepId = builder.sklepId;
        this.kontrahentId = builder.kontrahentId;
        this.platnikId = builder.platnikId;
        this.kasaId = builder.kasaId;
        this.kasjerId = builder.kasjerId;
        this.storno = builder.storno;
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
    /**
     * Document type code. Integer value. Known codes:
     * 21=paragon, 22=faktura sprzedazy, 33=faktura odbiorcy,
     * 34=faktura korygujaca odbiorcy, 36=faktura do paragonu,
     * 112=faktura fiskalna, 113=faktura korygujaca fiskalna.
     * Supports list or range format (e.g. {@code "21"}, {@code "21,33"}, {@code "min21"}).
     *
     * <p><b>Warning:</b> Only integer codes are accepted. String values like "WZ" or "FA"
     * cause an HTTP 500 server error.
     */
    public String typDok() { return typDok; }
    /** Issue date filter (dataWystawienia). */
    public String dataWystawienia() { return dataWystawienia; }
    /** Receipt date filter (dataWplywu). */
    public String dataWplywu() { return dataWplywu; }
    /** Execution date filter (dataWykonania). */
    public String dataWykonania() { return dataWykonania; }
    /** Document number (nrDok). */
    public String nrDok() { return nrDok; }
    /** Sklep (store) ID link (sklepId). */
    public String sklepId() { return sklepId; }
    /** Contractor ID link (kontrahentId). */
    public String kontrahentId() { return kontrahentId; }
    /** Payer ID link (platnikId). */
    public String platnikId() { return platnikId; }
    /** Cash register ID link (kasaId). */
    public String kasaId() { return kasaId; }
    /** Cashier ID link (kasjerId). */
    public String kasjerId() { return kasjerId; }
    /** Storno (reversal) flag. */
    public Boolean storno() { return storno; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder();
        b.start = this.start;
        b.id = this.id;
        b.typDok = this.typDok;
        b.dataWystawienia = this.dataWystawienia;
        b.dataWplywu = this.dataWplywu;
        b.dataWykonania = this.dataWykonania;
        b.nrDok = this.nrDok;
        b.sklepId = this.sklepId;
        b.kontrahentId = this.kontrahentId;
        b.platnikId = this.platnikId;
        b.kasaId = this.kasaId;
        b.kasjerId = this.kasjerId;
        b.storno = this.storno;
        return b;
    }

    /**
     * Builder for {@link DokumentQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String id;
        private String typDok;
        private String dataWystawienia;
        private String dataWplywu;
        private String dataWykonania;
        private String nrDok;
        private String sklepId;
        private String kontrahentId;
        private String platnikId;
        private String kasaId;
        private String kasjerId;
        private Boolean storno;

        private Builder() {}

        /** Sets Zero-based record offset for pagination (first record index on the page). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }
        /** Sets Record ID. @return this builder */
        public Builder id(String id) { this.id = id; return this; }
        /** Sets Document type code. @return this builder */
        public Builder typDok(String typDok) { this.typDok = typDok; return this; }
        /** Sets Issue date filter (dataWystawienia). @return this builder */
        public Builder dataWystawienia(String dataWystawienia) { this.dataWystawienia = dataWystawienia; return this; }
        /** Sets Receipt date filter (dataWplywu). @return this builder */
        public Builder dataWplywu(String dataWplywu) { this.dataWplywu = dataWplywu; return this; }
        /** Sets Execution date filter (dataWykonania). @return this builder */
        public Builder dataWykonania(String dataWykonania) { this.dataWykonania = dataWykonania; return this; }
        /** Sets Document number (nrDok). @return this builder */
        public Builder nrDok(String nrDok) { this.nrDok = nrDok; return this; }
        /** Sets Sklep (store) ID link (sklepId). @return this builder */
        public Builder sklepId(String sklepId) { this.sklepId = sklepId; return this; }
        /** Sets Contractor ID link (kontrahentId). @return this builder */
        public Builder kontrahentId(String kontrahentId) { this.kontrahentId = kontrahentId; return this; }
        /** Sets Payer ID link (platnikId). @return this builder */
        public Builder platnikId(String platnikId) { this.platnikId = platnikId; return this; }
        /** Sets Cash register ID link (kasaId). @return this builder */
        public Builder kasaId(String kasaId) { this.kasaId = kasaId; return this; }
        /** Sets Cashier ID link (kasjerId). @return this builder */
        public Builder kasjerId(String kasjerId) { this.kasjerId = kasjerId; return this; }
        /** Sets Storno (reversal) flag. @return this builder */
        public Builder storno(Boolean storno) { this.storno = storno; return this; }

        /**
         * Builds the {@link DokumentQueryBuilder}.
         *
         * @return a new {@link DokumentQueryBuilder} instance
         */
        public DokumentQueryBuilder build() { return new DokumentQueryBuilder(this); }
    }
}
