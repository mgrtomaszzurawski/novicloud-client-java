/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy;

/**
 * Immutable data transfer object for creating a new sklep record. Required: {@code nazwa}, {@code numer}.
 *
 * <p>Required fields are enforced by the builder factory method.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class SklepCreateBuilder {

    private final Long id;
    private final String nazwa;
    private final String nip;
    private final String skrot;
    private final Integer numer;
    private final String ulica;
    private final String nrDomu;
    private final String nrLokalu;
    private final String kodPoczt;
    private final String poczta;
    private final String miasto;
    private final String krajId;
    private final String telefon;
    private final String email;
    private final String bank;
    private final String konto;
    private final Boolean aktywny;

    private SklepCreateBuilder(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.nip = builder.nip;
        this.skrot = builder.skrot;
        this.numer = builder.numer;
        this.ulica = builder.ulica;
        this.nrDomu = builder.nrDomu;
        this.nrLokalu = builder.nrLokalu;
        this.kodPoczt = builder.kodPoczt;
        this.poczta = builder.poczta;
        this.miasto = builder.miasto;
        this.krajId = builder.krajId;
        this.telefon = builder.telefon;
        this.email = builder.email;
        this.bank = builder.bank;
        this.konto = builder.konto;
        this.aktywny = builder.aktywny;
    }

    /**
     * Creates a new builder with the required fields pre-set.
     *
     * @param nazwa  Name (nazwa) (required)
     * @param numer  Store number (numer) (required)
     * @return a new {@link Builder}
     */
    public static Builder builder(String nazwa, Integer numer) { return new Builder(nazwa, numer); }

    /** Record ID. */
    public Long id() { return id; }
    /** Name (nazwa). */
    public String nazwa() { return nazwa; }
    /** Tax identification number (NIP). */
    public String nip() { return nip; }
    /** Short name / abbreviation (skrot). */
    public String skrot() { return skrot; }
    /** Store number (numer). */
    public Integer numer() { return numer; }
    /** Street name (ulica). */
    public String ulica() { return ulica; }
    /** Building number (nrDomu). */
    public String nrDomu() { return nrDomu; }
    /** Apartment/unit number (nrLokalu). */
    public String nrLokalu() { return nrLokalu; }
    /** Postal code (kodPoczt). */
    public String kodPoczt() { return kodPoczt; }
    /** Post office city (poczta). */
    public String poczta() { return poczta; }
    /** City (miasto). */
    public String miasto() { return miasto; }
    /** Country ID link (krajId). */
    public String krajId() { return krajId; }
    /** Phone number (telefon). */
    public String telefon() { return telefon; }
    /** Email address (email). */
    public String email() { return email; }
    /** bank value. */
    public String bank() { return bank; }
    /** konto value. */
    public String konto() { return konto; }
    /** Active status flag ({@code true} for active, {@code false} for inactive, {@code null} for all). */
    public Boolean aktywny() { return aktywny; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.nazwa, this.numer);
        b.id = this.id;
        b.nip = this.nip;
        b.skrot = this.skrot;
        b.ulica = this.ulica;
        b.nrDomu = this.nrDomu;
        b.nrLokalu = this.nrLokalu;
        b.kodPoczt = this.kodPoczt;
        b.poczta = this.poczta;
        b.miasto = this.miasto;
        b.krajId = this.krajId;
        b.telefon = this.telefon;
        b.email = this.email;
        b.bank = this.bank;
        b.konto = this.konto;
        b.aktywny = this.aktywny;
        return b;
    }

    /**
     * Builder for {@link SklepCreateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private String nip;
        private String skrot;
        private Integer numer;
        private String ulica;
        private String nrDomu;
        private String nrLokalu;
        private String kodPoczt;
        private String poczta;
        private String miasto;
        private String krajId;
        private String telefon;
        private String email;
        private String bank;
        private String konto;
        private Boolean aktywny;

        private Builder(String nazwa, Integer numer) { this.nazwa = nazwa; this.numer = numer; }

        /** Sets Record ID. @return this builder */
        public Builder id(Long id) { this.id = id; return this; }
        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Tax identification number (NIP). @return this builder */
        public Builder nip(String nip) { this.nip = nip; return this; }
        /** Sets Short name / abbreviation (skrot). @return this builder */
        public Builder skrot(String skrot) { this.skrot = skrot; return this; }
        /** Sets Store number (numer). @return this builder */
        public Builder numer(Integer numer) { this.numer = numer; return this; }
        /** Sets Street name (ulica). @return this builder */
        public Builder ulica(String ulica) { this.ulica = ulica; return this; }
        /** Sets Building number (nrDomu). @return this builder */
        public Builder nrDomu(String nrDomu) { this.nrDomu = nrDomu; return this; }
        /** Sets Apartment/unit number (nrLokalu). @return this builder */
        public Builder nrLokalu(String nrLokalu) { this.nrLokalu = nrLokalu; return this; }
        /** Sets Postal code (kodPoczt). @return this builder */
        public Builder kodPoczt(String kodPoczt) { this.kodPoczt = kodPoczt; return this; }
        /** Sets Post office city (poczta). @return this builder */
        public Builder poczta(String poczta) { this.poczta = poczta; return this; }
        /** Sets City (miasto). @return this builder */
        public Builder miasto(String miasto) { this.miasto = miasto; return this; }
        /** Sets Country ID link (krajId). @return this builder */
        public Builder krajId(String krajId) { this.krajId = krajId; return this; }
        /** Sets Phone number (telefon). @return this builder */
        public Builder telefon(String telefon) { this.telefon = telefon; return this; }
        /** Sets Email address (email). @return this builder */
        public Builder email(String email) { this.email = email; return this; }
        /** Sets bank value. @return this builder */
        public Builder bank(String bank) { this.bank = bank; return this; }
        /** Sets konto value. @return this builder */
        public Builder konto(String konto) { this.konto = konto; return this; }
        /** Sets Active status flag ({@code true} for active, {@code false} for inactive, {@code null} for all). @return this builder */
        public Builder aktywny(Boolean aktywny) { this.aktywny = aktywny; return this; }

        /**
         * Builds the {@link SklepCreateBuilder}.
         *
         * @return a new {@link SklepCreateBuilder} instance
         */
        public SklepCreateBuilder build() { return new SklepCreateBuilder(this); }
    }
}
