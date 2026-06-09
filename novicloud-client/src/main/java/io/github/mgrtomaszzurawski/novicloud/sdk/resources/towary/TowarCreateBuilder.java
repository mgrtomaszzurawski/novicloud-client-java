/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary;

import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarCenaWSklepie;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarKodDodatkowy;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarSkladnik;

import java.util.List;
import java.util.Objects;

/**
 * Immutable data transfer object for creating a new towar record. Required: {@code kod}, {@code nazwa}.
 *
 * <p>Required fields are enforced by the builder factory method.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class TowarCreateBuilder {

    private final Long id;
    private final String nazwa;
    private final String kod;
    private final String cku;
    private final Integer stawkaVat;
    private final Boolean akcyzowy;
    private final Integer typ;
    private final Double cenaEw;
    private final Double cenaDet;
    private final Double cenaHurt;
    private final Double cenaNoc;
    private final Double cenaDod;
    private final Integer przySprzedazy;
    private final String gtu;
    private final String pkwiu;
    private final Double masaWl;
    private final Boolean aktywny;
    private final String jmId;
    private final String asortId;
    private final String opis1;
    private final String opis2;
    private final String opis3;
    private final String opis4;
    private final String opis5;
    private final List<TowarKodDodatkowy> kodyDod;
    private final List<TowarCenaWSklepie> cenyWSklepach;
    private final List<TowarSkladnik> skladniki;

    private TowarCreateBuilder(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.kod = builder.kod;
        this.cku = builder.cku;
        this.stawkaVat = builder.stawkaVat;
        this.akcyzowy = builder.akcyzowy;
        this.typ = builder.typ;
        this.cenaEw = builder.cenaEw;
        this.cenaDet = builder.cenaDet;
        this.cenaHurt = builder.cenaHurt;
        this.cenaNoc = builder.cenaNoc;
        this.cenaDod = builder.cenaDod;
        this.przySprzedazy = builder.przySprzedazy;
        this.gtu = builder.gtu;
        this.pkwiu = builder.pkwiu;
        this.masaWl = builder.masaWl;
        this.aktywny = builder.aktywny;
        this.jmId = builder.jmId;
        this.asortId = builder.asortId;
        this.opis1 = builder.opis1;
        this.opis2 = builder.opis2;
        this.opis3 = builder.opis3;
        this.opis4 = builder.opis4;
        this.opis5 = builder.opis5;
        this.kodyDod = builder.kodyDod == null ? null : List.copyOf(builder.kodyDod);
        this.cenyWSklepach = builder.cenyWSklepach == null ? null : List.copyOf(builder.cenyWSklepach);
        this.skladniki = builder.skladniki == null ? null : List.copyOf(builder.skladniki);
    }

    public static Builder builder(String kod, String nazwa) {
        return new Builder(kod, nazwa);
    }

    /** Record ID. */
    public Long id() { return id; }
    /** Name (nazwa). */
    public String nazwa() { return nazwa; }
    /** Code (kod). */
    public String kod() { return kod; }
    /** CKU code. */
    public String cku() { return cku; }
    /** VAT rate or VAT rate ID (stawkaVat). */
    public Integer stawkaVat() { return stawkaVat; }
    /** Excise tax flag (akcyzowy). */
    public Boolean akcyzowy() { return akcyzowy; }
    /** Record type code (typ). */
    public Integer typ() { return typ; }
    /** Ewidencja price (cenaEw). */
    public Double cenaEw() { return cenaEw; }
    /** Retail price (cenaDet). */
    public Double cenaDet() { return cenaDet; }
    /** Wholesale price (cenaHurt). */
    public Double cenaHurt() { return cenaHurt; }
    /** Night price (cenaNoc). */
    public Double cenaNoc() { return cenaNoc; }
    /** Additional price (cenaDod). */
    public Double cenaDod() { return cenaDod; }
    /** Behavior at sale (przySprzedazy). */
    public Integer przySprzedazy() { return przySprzedazy; }
    /** GTU code for JPK_V7. */
    public String gtu() { return gtu; }
    /** PKWiU classification code. */
    public String pkwiu() { return pkwiu; }
    /** Own weight in kilograms (masaWl). */
    public Double masaWl() { return masaWl; }
    /** Active status flag ({@code true} for active, {@code false} for inactive, {@code null} for all). */
    public Boolean aktywny() { return aktywny; }
    /** Unit of measure ID link (jmId). */
    public String jmId() { return jmId; }
    /** Assortment group ID link (asortId). */
    public String asortId() { return asortId; }
    /** Supplementary description field 1. */
    public String opis1() { return opis1; }
    /** Supplementary description field 2. */
    public String opis2() { return opis2; }
    /** Supplementary description field 3. */
    public String opis3() { return opis3; }
    /** Supplementary description field 4. */
    public String opis4() { return opis4; }
    /** Supplementary description field 5. */
    public String opis5() { return opis5; }
    /** Additional barcodes ({@code kody_dod}); {@code null} if not set. */
    public List<TowarKodDodatkowy> kodyDod() { return kodyDod == null ? null : List.copyOf(kodyDod); }
    /** Per-store price overrides ({@code ceny_w_sklepach}); {@code null} if not set. */
    public List<TowarCenaWSklepie> cenyWSklepach() { return cenyWSklepach == null ? null : List.copyOf(cenyWSklepach); }
    /** Bundle components for product type 5 ({@code skladniki}); {@code null} if not set. */
    public List<TowarSkladnik> skladniki() { return skladniki == null ? null : List.copyOf(skladniki); }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.kod, this.nazwa);
        b.id = this.id;
        b.cku = this.cku;
        b.stawkaVat = this.stawkaVat;
        b.akcyzowy = this.akcyzowy;
        b.typ = this.typ;
        b.cenaEw = this.cenaEw;
        b.cenaDet = this.cenaDet;
        b.cenaHurt = this.cenaHurt;
        b.cenaNoc = this.cenaNoc;
        b.cenaDod = this.cenaDod;
        b.przySprzedazy = this.przySprzedazy;
        b.gtu = this.gtu;
        b.pkwiu = this.pkwiu;
        b.masaWl = this.masaWl;
        b.aktywny = this.aktywny;
        b.jmId = this.jmId;
        b.asortId = this.asortId;
        b.opis1 = this.opis1;
        b.opis2 = this.opis2;
        b.opis3 = this.opis3;
        b.opis4 = this.opis4;
        b.opis5 = this.opis5;
        b.kodyDod = this.kodyDod;
        b.cenyWSklepach = this.cenyWSklepach;
        b.skladniki = this.skladniki;
        return b;
    }

    /**
     * Builder for {@link TowarCreateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private String kod;
        private String cku;
        private Integer stawkaVat;
        private Boolean akcyzowy;
        private Integer typ;
        private Double cenaEw;
        private Double cenaDet;
        private Double cenaHurt;
        private Double cenaNoc;
        private Double cenaDod;
        private Integer przySprzedazy;
        private String gtu;
        private String pkwiu;
        private Double masaWl;
        private Boolean aktywny;
        private String jmId;
        private String asortId;
        private String opis1;
        private String opis2;
        private String opis3;
        private String opis4;
        private String opis5;
        private List<TowarKodDodatkowy> kodyDod;
        private List<TowarCenaWSklepie> cenyWSklepach;
        private List<TowarSkladnik> skladniki;

        private Builder(String kod, String nazwa) {
            this.kod = Objects.requireNonNull(kod, "kod must not be null");
            this.nazwa = Objects.requireNonNull(nazwa, "nazwa must not be null");
        }

        /** Sets Record ID. @return this builder */
        public Builder id(Long id) { this.id = id; return this; }
        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Code (kod). @return this builder */
        public Builder kod(String kod) { this.kod = kod; return this; }
        /** Sets CKU code. @return this builder */
        public Builder cku(String cku) { this.cku = cku; return this; }
        /** Sets VAT rate or VAT rate ID (stawkaVat). @return this builder */
        public Builder stawkaVat(Integer stawkaVat) { this.stawkaVat = stawkaVat; return this; }
        /** Sets Excise tax flag (akcyzowy). @return this builder */
        public Builder akcyzowy(Boolean akcyzowy) { this.akcyzowy = akcyzowy; return this; }
        /** Sets Record type code (typ). @return this builder */
        public Builder typ(Integer typ) { this.typ = typ; return this; }
        /** Sets Ewidencja price (cenaEw). @return this builder */
        public Builder cenaEw(Double cenaEw) { this.cenaEw = cenaEw; return this; }
        /** Sets Retail price (cenaDet). @return this builder */
        public Builder cenaDet(Double cenaDet) { this.cenaDet = cenaDet; return this; }
        /** Sets Wholesale price (cenaHurt). @return this builder */
        public Builder cenaHurt(Double cenaHurt) { this.cenaHurt = cenaHurt; return this; }
        /** Sets Night price (cenaNoc). @return this builder */
        public Builder cenaNoc(Double cenaNoc) { this.cenaNoc = cenaNoc; return this; }
        /** Sets Additional price (cenaDod). @return this builder */
        public Builder cenaDod(Double cenaDod) { this.cenaDod = cenaDod; return this; }
        /** Sets Behavior at sale (przySprzedazy). @return this builder */
        public Builder przySprzedazy(Integer przySprzedazy) { this.przySprzedazy = przySprzedazy; return this; }
        /** Sets GTU code for JPK_V7. @return this builder */
        public Builder gtu(String gtu) { this.gtu = gtu; return this; }
        /** Sets PKWiU classification code. @return this builder */
        public Builder pkwiu(String pkwiu) { this.pkwiu = pkwiu; return this; }
        /** Sets Own weight in kilograms (masaWl). @return this builder */
        public Builder masaWl(Double masaWl) { this.masaWl = masaWl; return this; }
        /** Sets Active status flag ({@code true} for active, {@code false} for inactive, {@code null} for all). @return this builder */
        public Builder aktywny(Boolean aktywny) { this.aktywny = aktywny; return this; }
        /** Sets Unit of measure ID link (jmId). @return this builder */
        public Builder jmId(String jmId) { this.jmId = jmId; return this; }
        /** Sets Assortment group ID link (asortId). @return this builder */
        public Builder asortId(String asortId) { this.asortId = asortId; return this; }
        /** Sets Supplementary description field 1. @return this builder */
        public Builder opis1(String opis1) { this.opis1 = opis1; return this; }
        /** Sets Supplementary description field 2. @return this builder */
        public Builder opis2(String opis2) { this.opis2 = opis2; return this; }
        /** Sets Supplementary description field 3. @return this builder */
        public Builder opis3(String opis3) { this.opis3 = opis3; return this; }
        /** Sets Supplementary description field 4. @return this builder */
        public Builder opis4(String opis4) { this.opis4 = opis4; return this; }
        /** Sets Supplementary description field 5. @return this builder */
        public Builder opis5(String opis5) { this.opis5 = opis5; return this; }
        /** Sets additional barcodes ({@code kody_dod}). @return this builder */
        public Builder kodyDod(List<TowarKodDodatkowy> kodyDod) { this.kodyDod = kodyDod == null ? null : List.copyOf(kodyDod); return this; }
        /** Sets per-store price overrides ({@code ceny_w_sklepach}). @return this builder */
        public Builder cenyWSklepach(List<TowarCenaWSklepie> cenyWSklepach) { this.cenyWSklepach = cenyWSklepach == null ? null : List.copyOf(cenyWSklepach); return this; }
        /** Sets bundle components for product type 5 ({@code skladniki}). @return this builder */
        public Builder skladniki(List<TowarSkladnik> skladniki) { this.skladniki = skladniki == null ? null : List.copyOf(skladniki); return this; }

        /**
         * Builds the {@link TowarCreateBuilder}.
         *
         * @return a new {@link TowarCreateBuilder} instance
         */
        public TowarCreateBuilder build() { return new TowarCreateBuilder(this); }
    }
}
