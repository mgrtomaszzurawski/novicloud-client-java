/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj;

/**
 * Immutable data transfer object for updating an existing kartaloj record.
 * The {@code kod} field identifies the record to update and is required.
 *
 * <p>Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class KartaLojUpdateBuilder {

    private final String kod;
    private final Integer typ;
    private final String waznaOd;
    private final String waznaDo;
    private final String posiadacz;
    private final String opis1;
    private final String opis2;
    private final String uniewazniono;
    private final String nazwiskoImie;
    private final String skrot;
    private final String telefon;
    private final String email;
    private final String miejscowosc;
    private final String ulica;
    private final String nrDomu;
    private final String nrLokalu;
    private final String kodPoczt;
    private final String poczta;
    private final String nip;
    private final String dataUrodzenia;
    private final String plec;

    private KartaLojUpdateBuilder(Builder builder) {
        this.kod = builder.kod;
        this.typ = builder.typ;
        this.waznaOd = builder.waznaOd;
        this.waznaDo = builder.waznaDo;
        this.posiadacz = builder.posiadacz;
        this.opis1 = builder.opis1;
        this.opis2 = builder.opis2;
        this.uniewazniono = builder.uniewazniono;
        this.nazwiskoImie = builder.nazwiskoImie;
        this.skrot = builder.skrot;
        this.telefon = builder.telefon;
        this.email = builder.email;
        this.miejscowosc = builder.miejscowosc;
        this.ulica = builder.ulica;
        this.nrDomu = builder.nrDomu;
        this.nrLokalu = builder.nrLokalu;
        this.kodPoczt = builder.kodPoczt;
        this.poczta = builder.poczta;
        this.nip = builder.nip;
        this.dataUrodzenia = builder.dataUrodzenia;
        this.plec = builder.plec;
    }

    /**
     * Creates a new builder with the required fields pre-set.
     *
     * @param kod  Code (kod) (required)
     * @return a new {@link Builder}
     */
    public static Builder builder(String kod) { return new Builder(kod); }

    /** Code (kod). */
    public String kod() { return kod; }
    /** Record type code (typ). */
    public Integer typ() { return typ; }
    /** Valid-from date in ISO-8601 format. */
    public String waznaOd() { return waznaOd; }
    /** Valid-to date in ISO-8601 format. */
    public String waznaDo() { return waznaDo; }
    /** Cardholder name (posiadacz). */
    public String posiadacz() { return posiadacz; }
    /** Supplementary description field 1. */
    public String opis1() { return opis1; }
    /** Supplementary description field 2. */
    public String opis2() { return opis2; }
    /** Invalidation timestamp in ISO-8601 format; {@code null} if still valid. */
    public String uniewazniono() { return uniewazniono; }
    /** Cardholder full name (nazwiskoImie). */
    public String nazwiskoImie() { return nazwiskoImie; }
    /** Short name / abbreviation (skrot). */
    public String skrot() { return skrot; }
    /** Phone number (telefon). */
    public String telefon() { return telefon; }
    /** Email address (email). */
    public String email() { return email; }
    /** City (miejscowosc). */
    public String miejscowosc() { return miejscowosc; }
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
    /** Tax identification number (NIP). */
    public String nip() { return nip; }
    /** Date of birth in {@code YYYY-MM-DD} format. */
    public String dataUrodzenia() { return dataUrodzenia; }
    /** Gender (plec): enum value. */
    public String plec() { return plec; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.kod);
        b.typ = this.typ;
        b.waznaOd = this.waznaOd;
        b.waznaDo = this.waznaDo;
        b.posiadacz = this.posiadacz;
        b.opis1 = this.opis1;
        b.opis2 = this.opis2;
        b.uniewazniono = this.uniewazniono;
        b.nazwiskoImie = this.nazwiskoImie;
        b.skrot = this.skrot;
        b.telefon = this.telefon;
        b.email = this.email;
        b.miejscowosc = this.miejscowosc;
        b.ulica = this.ulica;
        b.nrDomu = this.nrDomu;
        b.nrLokalu = this.nrLokalu;
        b.kodPoczt = this.kodPoczt;
        b.poczta = this.poczta;
        b.nip = this.nip;
        b.dataUrodzenia = this.dataUrodzenia;
        b.plec = this.plec;
        return b;
    }

    /**
     * Builder for {@link KartaLojUpdateBuilder}.
     */
    public static final class Builder {
        private String kod;
        private Integer typ;
        private String waznaOd;
        private String waznaDo;
        private String posiadacz;
        private String opis1;
        private String opis2;
        private String uniewazniono;
        private String nazwiskoImie;
        private String skrot;
        private String telefon;
        private String email;
        private String miejscowosc;
        private String ulica;
        private String nrDomu;
        private String nrLokalu;
        private String kodPoczt;
        private String poczta;
        private String nip;
        private String dataUrodzenia;
        private String plec;

        private Builder(String kod) { this.kod = kod; }

        /** Sets Record type code (typ). @return this builder */
        public Builder typ(Integer typ) { this.typ = typ; return this; }
        /** Sets Valid-from date in ISO-8601 format. @return this builder */
        public Builder waznaOd(String waznaOd) { this.waznaOd = waznaOd; return this; }
        /** Sets Valid-to date in ISO-8601 format. @return this builder */
        public Builder waznaDo(String waznaDo) { this.waznaDo = waznaDo; return this; }
        /** Sets Cardholder name (posiadacz). @return this builder */
        public Builder posiadacz(String posiadacz) { this.posiadacz = posiadacz; return this; }
        /** Sets Supplementary description field 1. @return this builder */
        public Builder opis1(String opis1) { this.opis1 = opis1; return this; }
        /** Sets Supplementary description field 2. @return this builder */
        public Builder opis2(String opis2) { this.opis2 = opis2; return this; }
        /** Sets Invalidation timestamp in ISO-8601 format; {@code null} if still valid. @return this builder */
        public Builder uniewazniono(String uniewazniono) { this.uniewazniono = uniewazniono; return this; }
        /** Sets Cardholder full name (nazwiskoImie). @return this builder */
        public Builder nazwiskoImie(String nazwiskoImie) { this.nazwiskoImie = nazwiskoImie; return this; }
        /** Sets Short name / abbreviation (skrot). @return this builder */
        public Builder skrot(String skrot) { this.skrot = skrot; return this; }
        /** Sets Phone number (telefon). @return this builder */
        public Builder telefon(String telefon) { this.telefon = telefon; return this; }
        /** Sets Email address (email). @return this builder */
        public Builder email(String email) { this.email = email; return this; }
        /** Sets City (miejscowosc). @return this builder */
        public Builder miejscowosc(String miejscowosc) { this.miejscowosc = miejscowosc; return this; }
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
        /** Sets Tax identification number (NIP). @return this builder */
        public Builder nip(String nip) { this.nip = nip; return this; }
        /** Sets Date of birth in {@code YYYY-MM-DD} format. @return this builder */
        public Builder dataUrodzenia(String dataUrodzenia) { this.dataUrodzenia = dataUrodzenia; return this; }
        /** Sets Gender (plec): enum value. @return this builder */
        public Builder plec(String plec) { this.plec = plec; return this; }

        /**
         * Builds the {@link KartaLojUpdateBuilder}.
         *
         * @return a new {@link KartaLojUpdateBuilder} instance
         */
        public KartaLojUpdateBuilder build() { return new KartaLojUpdateBuilder(this); }
    }
}
