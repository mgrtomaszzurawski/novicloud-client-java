/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.sprzedaz;

/**
 * Immutable filter and pagination parameters for listing sprzedaz records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 *
 * <p><b>Note:</b> The {@code nrRapDob} filter was removed per ADR-031 because
 * it causes an HTTP 500 server error (unknown SQL column).
 * @since 1.0.0
 */
public final class SprzedazQueryBuilder {

    private final Integer start;
    private final String id;
    private final String data;
    private final String nrDok;
    private final String typDok;
    private final String nrSystemowy;
    private final String nrFiskalny;
    private final String ilosc;
    private final String cena;
    private final String stawkaVat;
    private final String brutto;
    private final String podatek;
    private final String rabat;
    private final String towarId;
    private final String sklepId;
    private final String kasaId;
    private final String kasjerId;
    private final String kontrahentId;

    private SprzedazQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.id = builder.id;
        this.data = builder.data;
        this.nrDok = builder.nrDok;
        this.typDok = builder.typDok;
        this.nrSystemowy = builder.nrSystemowy;
        this.nrFiskalny = builder.nrFiskalny;
        this.ilosc = builder.ilosc;
        this.cena = builder.cena;
        this.stawkaVat = builder.stawkaVat;
        this.brutto = builder.brutto;
        this.podatek = builder.podatek;
        this.rabat = builder.rabat;
        this.towarId = builder.towarId;
        this.sklepId = builder.sklepId;
        this.kasaId = builder.kasaId;
        this.kasjerId = builder.kasjerId;
        this.kontrahentId = builder.kontrahentId;
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
    /** Transaction date filter. */
    public String data() { return data; }
    /** Document number (nrDok). */
    public String nrDok() { return nrDok; }
    /**
     * Document type code. Integer value. Known codes:
     * 21=paragon fiskalny, 33=faktura odbiorcy,
     * 34=faktura korygujaca, 36=faktura do paragonu,
     * 112=faktura fiskalna, 113=faktura korygujaca fiskalna.
     * Supports list or range format (e.g. {@code "21"}, {@code "21,33"}, {@code "min21"}).
     *
     * <p><b>Warning:</b> Only integer codes are accepted. String values like "WZ" or "FA"
     * cause an HTTP 500 server error.
     */
    public String typDok() { return typDok; }
    /** System number (nrSystemowy). */
    public String nrSystemowy() { return nrSystemowy; }
    /** Fiscal number (nrFiskalny). */
    public String nrFiskalny() { return nrFiskalny; }
    /** Numeric value. Dot as decimal separator. Supports list or range format (e.g. "1", "1.00", "min5.50"). */
    public String ilosc() { return ilosc; }
    /** Numeric value. Dot as decimal separator. Supports list or range format (e.g. "1", "1.00", "min5.50"). */
    public String cena() { return cena; }
    /**
     * VAT rate code in hundredths (e.g. 2300=23%, 800=8%, 500=5%, -1=exempt).
     * Supports list or range format.
     */
    public String stawkaVat() { return stawkaVat; }
    /** Numeric value. Dot as decimal separator. Supports list or range format (e.g. "1", "1.00", "min5.50"). */
    public String brutto() { return brutto; }
    /** Numeric value. Dot as decimal separator. Supports list or range format (e.g. "1", "1.00", "min5.50"). */
    public String podatek() { return podatek; }
    /** Numeric value. Dot as decimal separator. Supports list or range format (e.g. "1", "1.00", "min5.50"). */
    public String rabat() { return rabat; }
    /** Towar ID link (towarId). */
    public String towarId() { return towarId; }
    /** Sklep (store) ID link (sklepId). */
    public String sklepId() { return sklepId; }
    /** Cash register ID link (kasaId). */
    public String kasaId() { return kasaId; }
    /** Cashier ID link (kasjerId). */
    public String kasjerId() { return kasjerId; }
    /** Contractor ID link (kontrahentId). */
    public String kontrahentId() { return kontrahentId; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder();
        b.start = this.start;
        b.id = this.id;
        b.data = this.data;
        b.nrDok = this.nrDok;
        b.typDok = this.typDok;
        b.nrSystemowy = this.nrSystemowy;
        b.nrFiskalny = this.nrFiskalny;
        b.ilosc = this.ilosc;
        b.cena = this.cena;
        b.stawkaVat = this.stawkaVat;
        b.brutto = this.brutto;
        b.podatek = this.podatek;
        b.rabat = this.rabat;
        b.towarId = this.towarId;
        b.sklepId = this.sklepId;
        b.kasaId = this.kasaId;
        b.kasjerId = this.kasjerId;
        b.kontrahentId = this.kontrahentId;
        return b;
    }

    /**
     * Builder for {@link SprzedazQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String id;
        private String data;
        private String nrDok;
        private String typDok;
        private String nrSystemowy;
        private String nrFiskalny;
        private String ilosc;
        private String cena;
        private String stawkaVat;
        private String brutto;
        private String podatek;
        private String rabat;
        private String towarId;
        private String sklepId;
        private String kasaId;
        private String kasjerId;
        private String kontrahentId;

        private Builder() {}

        /** Sets Zero-based record offset for pagination (first record index on the page). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }
        /** Sets Record ID. @return this builder */
        public Builder id(String id) { this.id = id; return this; }
        /** Sets Transaction date filter. @return this builder */
        public Builder data(String data) { this.data = data; return this; }
        /** Sets Document number (nrDok). @return this builder */
        public Builder nrDok(String nrDok) { this.nrDok = nrDok; return this; }
        /** Sets Document type code. @return this builder */
        public Builder typDok(String typDok) { this.typDok = typDok; return this; }
        /** Sets System number (nrSystemowy). @return this builder */
        public Builder nrSystemowy(String nrSystemowy) { this.nrSystemowy = nrSystemowy; return this; }
        /** Sets Fiscal number (nrFiskalny). @return this builder */
        public Builder nrFiskalny(String nrFiskalny) { this.nrFiskalny = nrFiskalny; return this; }
        /** Sets Stock quantity (ilosc). @return this builder */
        public Builder ilosc(String ilosc) { this.ilosc = ilosc; return this; }
        /** Sets Unit price (cena). @return this builder */
        public Builder cena(String cena) { this.cena = cena; return this; }
        /** Sets VAT rate code (stawkaVat). @return this builder */
        public Builder stawkaVat(String stawkaVat) { this.stawkaVat = stawkaVat; return this; }
        /** Sets Gross amount (brutto). @return this builder */
        public Builder brutto(String brutto) { this.brutto = brutto; return this; }
        /** Sets Tax amount (podatek). @return this builder */
        public Builder podatek(String podatek) { this.podatek = podatek; return this; }
        /** Sets Discount (rabat). @return this builder */
        public Builder rabat(String rabat) { this.rabat = rabat; return this; }
        /** Sets Towar ID link (towarId). @return this builder */
        public Builder towarId(String towarId) { this.towarId = towarId; return this; }
        /** Sets Sklep (store) ID link (sklepId). @return this builder */
        public Builder sklepId(String sklepId) { this.sklepId = sklepId; return this; }
        /** Sets Cash register ID link (kasaId). @return this builder */
        public Builder kasaId(String kasaId) { this.kasaId = kasaId; return this; }
        /** Sets Cashier ID link (kasjerId). @return this builder */
        public Builder kasjerId(String kasjerId) { this.kasjerId = kasjerId; return this; }
        /** Sets Contractor ID link (kontrahentId). @return this builder */
        public Builder kontrahentId(String kontrahentId) { this.kontrahentId = kontrahentId; return this; }

        /**
         * Builds the {@link SprzedazQueryBuilder}.
         *
         * @return a new {@link SprzedazQueryBuilder} instance
         */
        public SprzedazQueryBuilder build() { return new SprzedazQueryBuilder(this); }
    }
}
