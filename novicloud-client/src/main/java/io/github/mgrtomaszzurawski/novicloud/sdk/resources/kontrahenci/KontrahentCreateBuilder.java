/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci;

import java.util.Objects;

/**
 * Immutable data transfer object for creating a new kontrahent record. Required: {@code nazwa}.
 *
 * <p>Required fields are enforced by the builder factory method.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class KontrahentCreateBuilder {

    private final Long id;
    private final String nazwa;
    private final String nip;
    private final String skrot;
    private final String ulica;
    private final String nrDomu;
    private final String nrLokalu;
    private final String kodPoczt;
    private final String poczta;
    private final String miasto;
    private final String krajId;
    private final String telefon;
    private final String email;
    private final Boolean aktywny;
    private final Boolean dostawca;
    private final Boolean staly;
    private final Boolean producent;
    private final Boolean odbiorca;
    private final Boolean osoba;

    private KontrahentCreateBuilder(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.nip = builder.nip;
        this.skrot = builder.skrot;
        this.ulica = builder.ulica;
        this.nrDomu = builder.nrDomu;
        this.nrLokalu = builder.nrLokalu;
        this.kodPoczt = builder.kodPoczt;
        this.poczta = builder.poczta;
        this.miasto = builder.miasto;
        this.krajId = builder.krajId;
        this.telefon = builder.telefon;
        this.email = builder.email;
        this.aktywny = builder.aktywny;
        this.dostawca = builder.dostawca;
        this.staly = builder.staly;
        this.producent = builder.producent;
        this.odbiorca = builder.odbiorca;
        this.osoba = builder.osoba;
    }

    /**
     * Creates a new builder with the required fields pre-set.
     *
     * @param nazwa  Name (nazwa) (required)
     * @return a new {@link Builder}
     */
    public static Builder builder(String nazwa) { return new Builder(nazwa); }

    /** Record ID. */
    public Long id() { return id; }
    /** Name (nazwa). */
    public String nazwa() { return nazwa; }
    /** Tax identification number (NIP). */
    public String nip() { return nip; }
    /** Short name / abbreviation (skrot). */
    public String skrot() { return skrot; }
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
    /** Active status flag ({@code true} for active, {@code false} for inactive, {@code null} for all). */
    public Boolean aktywny() { return aktywny; }
    /** Supplier flag (dostawca). */
    public Boolean dostawca() { return dostawca; }
    /** Regular customer flag (staly). */
    public Boolean staly() { return staly; }
    /** Producer flag (producent). */
    public Boolean producent() { return producent; }
    /** Receiver flag (odbiorca). */
    public Boolean odbiorca() { return odbiorca; }
    /** Natural person flag (osoba). */
    public Boolean osoba() { return osoba; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.nazwa);
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
        b.aktywny = this.aktywny;
        b.dostawca = this.dostawca;
        b.staly = this.staly;
        b.producent = this.producent;
        b.odbiorca = this.odbiorca;
        b.osoba = this.osoba;
        return b;
    }

    /**
     * Builder for {@link KontrahentCreateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private String nip;
        private String skrot;
        private String ulica;
        private String nrDomu;
        private String nrLokalu;
        private String kodPoczt;
        private String poczta;
        private String miasto;
        private String krajId;
        private String telefon;
        private String email;
        private Boolean aktywny;
        private Boolean dostawca;
        private Boolean staly;
        private Boolean producent;
        private Boolean odbiorca;
        private Boolean osoba;

        private Builder(String nazwa) {
            this.nazwa = Objects.requireNonNull(nazwa, "nazwa must not be null");
        }

        /** Sets Record ID. @return this builder */
        public Builder id(Long id) { this.id = id; return this; }
        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Tax identification number (NIP). @return this builder */
        public Builder nip(String nip) { this.nip = nip; return this; }
        /** Sets Short name / abbreviation (skrot). @return this builder */
        public Builder skrot(String skrot) { this.skrot = skrot; return this; }
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
        /** Sets Active status flag ({@code true} for active, {@code false} for inactive, {@code null} for all). @return this builder */
        public Builder aktywny(Boolean aktywny) { this.aktywny = aktywny; return this; }
        /** Sets Supplier flag (dostawca). @return this builder */
        public Builder dostawca(Boolean dostawca) { this.dostawca = dostawca; return this; }
        /** Sets Regular customer flag (staly). @return this builder */
        public Builder staly(Boolean staly) { this.staly = staly; return this; }
        /** Sets Producer flag (producent). @return this builder */
        public Builder producent(Boolean producent) { this.producent = producent; return this; }
        /** Sets Receiver flag (odbiorca). @return this builder */
        public Builder odbiorca(Boolean odbiorca) { this.odbiorca = odbiorca; return this; }
        /** Sets Natural person flag (osoba). @return this builder */
        public Builder osoba(Boolean osoba) { this.osoba = osoba; return this; }

        /**
         * Builds the {@link KontrahentCreateBuilder}.
         *
         * @return a new {@link KontrahentCreateBuilder} instance
         */
        public KontrahentCreateBuilder build() { return new KontrahentCreateBuilder(this); }
    }
}
