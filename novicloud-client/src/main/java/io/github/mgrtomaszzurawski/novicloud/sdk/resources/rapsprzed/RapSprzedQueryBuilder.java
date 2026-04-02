/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.rapsprzed;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Immutable filter and pagination parameters for listing rapsprzed records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class RapSprzedQueryBuilder {

    private final Integer start;
    private final String dataPocz;
    private final String dataKonc;
    private final String grupowanie;
    private final String skladniki;
    private final String towarId;
    private final String asortId;
    private final String sklepId;
    private final String kasaId;
    private final String kasjerId;
    private final String kontrahentId;
    private final String formaPlatnId;

    private RapSprzedQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.dataPocz = builder.dataPocz;
        this.dataKonc = builder.dataKonc;
        this.grupowanie = builder.grupowanie;
        this.skladniki = builder.skladniki;
        this.towarId = builder.towarId;
        this.asortId = builder.asortId;
        this.sklepId = builder.sklepId;
        this.kasaId = builder.kasaId;
        this.kasjerId = builder.kasjerId;
        this.kontrahentId = builder.kontrahentId;
        this.formaPlatnId = builder.formaPlatnId;
    }

    /**
     * Creates a new builder with all fields unset.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() { return new Builder(); }

    /** Zero-based record offset for pagination (first record index on the page). */
    public Integer start() { return start; }
    /** Report period start date in yyyy-MM-dd format. Default: 7 days ago. */
    public String dataPocz() { return dataPocz; }
    /** Report period end date in yyyy-MM-dd format. Default: current date. */
    public String dataKonc() { return dataKonc; }
    /** Grouping mode for report aggregation. Determines how sales data is summed. */
    public String grupowanie() { return grupowanie; }
    /**
     * Document type codes to include in the report. Comma-separated list of integers.
     * Known codes: 1=paragony bez NIP, 2=paragony z NIP, 3=faktury do paragonow,
     * 4=faktury fiskalne, 5=zwroty do paragonow, 6=faktury korygujace fiskalne,
     * 7=faktury wlasne, 8=faktury korygujace wlasne.
     * Default (when not set): "1,2,4,5,6,7,8" (all except faktury do paragonu).
     * Example: {@code "1,2,4,5,6,7,8"} or {@code "1"} for paragony only.
     */
    public String skladniki() { return skladniki; }
    /** Towar ID link (towarId). */
    public String towarId() { return towarId; }
    /** Assortment group ID link (asortId). */
    public String asortId() { return asortId; }
    /** Sklep (store) ID link (sklepId). */
    public String sklepId() { return sklepId; }
    /** Cash register ID link (kasaId). */
    public String kasaId() { return kasaId; }
    /** Cashier ID link (kasjerId). */
    public String kasjerId() { return kasjerId; }
    /** Contractor ID link (kontrahentId). */
    public String kontrahentId() { return kontrahentId; }
    /** formaPlatnId value. */
    public String formaPlatnId() { return formaPlatnId; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder();
        b.start = this.start;
        b.dataPocz = this.dataPocz;
        b.dataKonc = this.dataKonc;
        b.grupowanie = this.grupowanie;
        b.skladniki = this.skladniki;
        b.towarId = this.towarId;
        b.asortId = this.asortId;
        b.sklepId = this.sklepId;
        b.kasaId = this.kasaId;
        b.kasjerId = this.kasjerId;
        b.kontrahentId = this.kontrahentId;
        b.formaPlatnId = this.formaPlatnId;
        return b;
    }

    /**
     * Builder for {@link RapSprzedQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String dataPocz;
        private String dataKonc;
        private String grupowanie;
        private String skladniki;
        private String towarId;
        private String asortId;
        private String sklepId;
        private String kasaId;
        private String kasjerId;
        private String kontrahentId;
        private String formaPlatnId;

        private Builder() {}

        /** Sets Zero-based record offset for pagination (first record index on the page). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }
        /** Sets Report period start date in yyyy-MM-dd format. @return this builder */
        public Builder dataPocz(String dataPocz) { this.dataPocz = dataPocz; return this; }
        /** Sets Report period end date in yyyy-MM-dd format. @return this builder */
        public Builder dataKonc(String dataKonc) { this.dataKonc = dataKonc; return this; }
        /** Sets Grouping mode for report aggregation. @return this builder */
        public Builder grupowanie(RapSprzedGroup grupowanie) { this.grupowanie = grupowanie.value(); return this; }
        /** Sets Document type codes to include in the report. @return this builder */
        public Builder skladniki(String skladniki) { this.skladniki = skladniki; return this; }
        /** Sets Towar ID link (towarId). @return this builder */
        public Builder towarId(String towarId) { this.towarId = towarId; return this; }
        /** Sets Assortment group ID link (asortId). @return this builder */
        public Builder asortId(String asortId) { this.asortId = asortId; return this; }
        /** Sets Sklep (store) ID link (sklepId). @return this builder */
        public Builder sklepId(String sklepId) { this.sklepId = sklepId; return this; }
        /** Sets Cash register ID link (kasaId). @return this builder */
        public Builder kasaId(String kasaId) { this.kasaId = kasaId; return this; }
        /** Sets Cashier ID link (kasjerId). @return this builder */
        public Builder kasjerId(String kasjerId) { this.kasjerId = kasjerId; return this; }
        /** Sets Contractor ID link (kontrahentId). @return this builder */
        public Builder kontrahentId(String kontrahentId) { this.kontrahentId = kontrahentId; return this; }
        /** Sets formaPlatnId value. @return this builder */
        public Builder formaPlatnId(String formaPlatnId) { this.formaPlatnId = formaPlatnId; return this; }

        /**
         * Builds the {@link RapSprzedQueryBuilder}.
         *
         * @return a new {@link RapSprzedQueryBuilder} instance
         */
        public RapSprzedQueryBuilder build() {
            validateDate(dataPocz, FIELD_DATA_POCZ);
            validateDate(dataKonc, FIELD_DATA_KONC);
            return new RapSprzedQueryBuilder(this);
        }

        private static final String FIELD_DATA_POCZ = "dataPocz";
        private static final String FIELD_DATA_KONC = "dataKonc";
        private static final String ERR_INVALID_DATE_FMT = "%s: invalid date format '%s', expected yyyy-MM-dd";

        private static void validateDate(String value, String fieldName) {
            if (value == null) {
                return;
            }
            try {
                LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(String.format(ERR_INVALID_DATE_FMT, fieldName, value), e);
            }
        }
    }
}
