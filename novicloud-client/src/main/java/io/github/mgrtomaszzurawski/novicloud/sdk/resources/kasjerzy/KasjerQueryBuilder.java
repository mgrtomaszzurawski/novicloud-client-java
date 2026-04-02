/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasjerzy;

/**
 * Immutable filter and pagination parameters for listing kasjer records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class KasjerQueryBuilder {

    private final Integer start;
    private final String id;
    private final String nazwisko;
    private final String kodKasjera;
    private final Boolean aktywny;

    private KasjerQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.id = builder.id;
        this.nazwisko = builder.nazwisko;
        this.kodKasjera = builder.kodKasjera;
        this.aktywny = builder.aktywny;
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
    /** nazwisko value. */
    public String nazwisko() { return nazwisko; }
    /** kodKasjera value. */
    public String kodKasjera() { return kodKasjera; }
    /** Active flag filter ({@code true} = active only, {@code false} = inactive only,
     *  {@code null} = no filter). */
    public Boolean aktywny() { return aktywny; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder();
        b.start = this.start;
        b.id = this.id;
        b.nazwisko = this.nazwisko;
        b.kodKasjera = this.kodKasjera;
        b.aktywny = this.aktywny;
        return b;
    }

    /**
     * Builder for {@link KasjerQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String id;
        private String nazwisko;
        private String kodKasjera;
        private Boolean aktywny;

        private Builder() {}

        /** Sets Zero-based record offset for pagination (first record index on the page). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }
        /** Sets Record ID. @return this builder */
        public Builder id(String id) { this.id = id; return this; }
        /** Sets nazwisko value. @return this builder */
        public Builder nazwisko(String nazwisko) { this.nazwisko = nazwisko; return this; }
        /** Sets kodKasjera value. @return this builder */
        public Builder kodKasjera(String kodKasjera) { this.kodKasjera = kodKasjera; return this; }
        /** Sets the active flag filter. @return this builder */
        public Builder aktywny(Boolean aktywny) { this.aktywny = aktywny; return this; }

        /**
         * Builds the {@link KasjerQueryBuilder}.
         *
         * @return a new {@link KasjerQueryBuilder} instance
         */
        public KasjerQueryBuilder build() { return new KasjerQueryBuilder(this); }
    }
}
