/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.pozdok;

/**
 * Immutable filter and pagination parameters for listing pozdok records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 *
 * <p><b>Note:</b> Five filters were removed per ADR-031 because they cause HTTP 500 server
 * errors (unknown SQL columns, NPE): {@code id}, {@code dokumentTypDok},
 * {@code dokumentDataWystawienia}, {@code dokumentDataWplywu}, {@code dokumentDataWykonania}.
 * The remaining 9 filters work correctly.
 * @since 1.0.0
 */
public final class PozdokQueryBuilder {

    private final Integer start;
    private final String dokumentId;
    private final String dokumentNrDok;
    private final String dokumentKontrahentId;
    private final String dokumentPlatnikId;
    private final String dokumentSklepId;
    private final String dokumentKasaId;
    private final String dokumentKasjerId;
    private final String towarId;
    private final String nrPozycji;

    private PozdokQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.dokumentId = builder.dokumentId;
        this.dokumentNrDok = builder.dokumentNrDok;
        this.dokumentKontrahentId = builder.dokumentKontrahentId;
        this.dokumentPlatnikId = builder.dokumentPlatnikId;
        this.dokumentSklepId = builder.dokumentSklepId;
        this.dokumentKasaId = builder.dokumentKasaId;
        this.dokumentKasjerId = builder.dokumentKasjerId;
        this.towarId = builder.towarId;
        this.nrPozycji = builder.nrPozycji;
    }

    /**
     * Creates a new builder with all fields unset.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() { return new Builder(); }

    /** Zero-based record offset for pagination (first record index on the page). */
    public Integer start() { return start; }
    /** Parent document ID filter (dokumentId). */
    public String dokumentId() { return dokumentId; }
    /** Parent document number filter (dokumentNrDok). */
    public String dokumentNrDok() { return dokumentNrDok; }
    /** Parent document contractor ID filter. */
    public String dokumentKontrahentId() { return dokumentKontrahentId; }
    /** Parent document payer ID filter. */
    public String dokumentPlatnikId() { return dokumentPlatnikId; }
    /** Parent document store ID filter. */
    public String dokumentSklepId() { return dokumentSklepId; }
    /** Parent document cash register ID filter. */
    public String dokumentKasaId() { return dokumentKasaId; }
    /** Parent document cashier ID filter. */
    public String dokumentKasjerId() { return dokumentKasjerId; }
    /** Towar ID link (towarId). */
    public String towarId() { return towarId; }
    /** Line item number within the document (nrPozycji). */
    public String nrPozycji() { return nrPozycji; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder();
        b.start = this.start;
        b.dokumentId = this.dokumentId;
        b.dokumentNrDok = this.dokumentNrDok;
        b.dokumentKontrahentId = this.dokumentKontrahentId;
        b.dokumentPlatnikId = this.dokumentPlatnikId;
        b.dokumentSklepId = this.dokumentSklepId;
        b.dokumentKasaId = this.dokumentKasaId;
        b.dokumentKasjerId = this.dokumentKasjerId;
        b.towarId = this.towarId;
        b.nrPozycji = this.nrPozycji;
        return b;
    }

    /**
     * Builder for {@link PozdokQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String dokumentId;
        private String dokumentNrDok;
        private String dokumentKontrahentId;
        private String dokumentPlatnikId;
        private String dokumentSklepId;
        private String dokumentKasaId;
        private String dokumentKasjerId;
        private String towarId;
        private String nrPozycji;

        private Builder() {}

        /** Sets Zero-based record offset for pagination (first record index on the page). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }
        /** Sets Parent document ID filter (dokumentId). @return this builder */
        public Builder dokumentId(String dokumentId) { this.dokumentId = dokumentId; return this; }
        /** Sets Parent document number filter (dokumentNrDok). @return this builder */
        public Builder dokumentNrDok(String dokumentNrDok) { this.dokumentNrDok = dokumentNrDok; return this; }
        /** Sets Parent document contractor ID filter. @return this builder */
        public Builder dokumentKontrahentId(String dokumentKontrahentId) { this.dokumentKontrahentId = dokumentKontrahentId; return this; }
        /** Sets Parent document payer ID filter. @return this builder */
        public Builder dokumentPlatnikId(String dokumentPlatnikId) { this.dokumentPlatnikId = dokumentPlatnikId; return this; }
        /** Sets Parent document store ID filter. @return this builder */
        public Builder dokumentSklepId(String dokumentSklepId) { this.dokumentSklepId = dokumentSklepId; return this; }
        /** Sets Parent document cash register ID filter. @return this builder */
        public Builder dokumentKasaId(String dokumentKasaId) { this.dokumentKasaId = dokumentKasaId; return this; }
        /** Sets Parent document cashier ID filter. @return this builder */
        public Builder dokumentKasjerId(String dokumentKasjerId) { this.dokumentKasjerId = dokumentKasjerId; return this; }
        /** Sets Towar ID link (towarId). @return this builder */
        public Builder towarId(String towarId) { this.towarId = towarId; return this; }
        /** Sets Line item number within the document (nrPozycji). @return this builder */
        public Builder nrPozycji(String nrPozycji) { this.nrPozycji = nrPozycji; return this; }

        /**
         * Builds the {@link PozdokQueryBuilder}.
         *
         * @return a new {@link PozdokQueryBuilder} instance
         */
        public PozdokQueryBuilder build() { return new PozdokQueryBuilder(this); }
    }
}
