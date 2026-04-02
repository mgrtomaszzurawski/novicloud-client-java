/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Immutable filter and pagination parameters for listing rappracy records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class RapPracyQueryBuilder {

    private final Integer start;
    private final String dataPocz;
    private final String dataKonc;
    private final String grupowanie;
    private final String sklepId;
    private final String kasaId;
    private final String kasjerId;

    private RapPracyQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.dataPocz = builder.dataPocz;
        this.dataKonc = builder.dataKonc;
        this.grupowanie = builder.grupowanie;
        this.sklepId = builder.sklepId;
        this.kasaId = builder.kasaId;
        this.kasjerId = builder.kasjerId;
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
    /** Grouping mode for report aggregation. Determines how work time data is summed. */
    public String grupowanie() { return grupowanie; }
    /** Sklep (store) ID link (sklepId). */
    public String sklepId() { return sklepId; }
    /** Cash register ID link (kasaId). */
    public String kasaId() { return kasaId; }
    /** Cashier ID link (kasjerId). */
    public String kasjerId() { return kasjerId; }

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
        b.sklepId = this.sklepId;
        b.kasaId = this.kasaId;
        b.kasjerId = this.kasjerId;
        return b;
    }

    /**
     * Builder for {@link RapPracyQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String dataPocz;
        private String dataKonc;
        private String grupowanie;
        private String sklepId;
        private String kasaId;
        private String kasjerId;

        private Builder() {}

        /** Sets Zero-based record offset for pagination (first record index on the page). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }
        /** Sets Report period start date in yyyy-MM-dd format. @return this builder */
        public Builder dataPocz(String dataPocz) { this.dataPocz = dataPocz; return this; }
        /** Sets Report period end date in yyyy-MM-dd format. @return this builder */
        public Builder dataKonc(String dataKonc) { this.dataKonc = dataKonc; return this; }
        /** Sets Grouping mode for report aggregation. @return this builder */
        public Builder grupowanie(RapPracyGroup grupowanie) { this.grupowanie = grupowanie.value(); return this; }
        /** Sets Sklep (store) ID link (sklepId). @return this builder */
        public Builder sklepId(String sklepId) { this.sklepId = sklepId; return this; }
        /** Sets Cash register ID link (kasaId). @return this builder */
        public Builder kasaId(String kasaId) { this.kasaId = kasaId; return this; }
        /** Sets Cashier ID link (kasjerId). @return this builder */
        public Builder kasjerId(String kasjerId) { this.kasjerId = kasjerId; return this; }

        /**
         * Builds the {@link RapPracyQueryBuilder}.
         *
         * @return a new {@link RapPracyQueryBuilder} instance
         */
        public RapPracyQueryBuilder build() {
            validateDate(dataPocz, FIELD_DATA_POCZ);
            validateDate(dataKonc, FIELD_DATA_KONC);
            return new RapPracyQueryBuilder(this);
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
