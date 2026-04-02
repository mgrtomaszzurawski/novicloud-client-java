/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj;

/**
 * Immutable filter and pagination parameters for listing kartaloj records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 *
 * <p><b>Note:</b> The {@code nazwisko_imie}, {@code wazna_od}, and {@code wazna_do} filters
 * are documented in the API spec but broken server-side (returns 400 par_niewlasciwe).
 * Removed per ADR-031.
 * @since 1.0.0
 */
public final class KartaLojQueryBuilder {

    private final Integer start;
    private final String kod;
    private final String posiadacz;
    private final String telefon;
    private final String email;
    private final String uniewazniono;

    private KartaLojQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.kod = builder.kod;
        this.posiadacz = builder.posiadacz;
        this.telefon = builder.telefon;
        this.email = builder.email;
        this.uniewazniono = builder.uniewazniono;
    }

    /**
     * Creates a new builder with all fields unset.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() { return new Builder(); }

    /** Zero-based record offset for pagination (first record index on the page). */
    public Integer start() { return start; }
    /** Code (kod). */
    public String kod() { return kod; }
    /** Cardholder name (posiadacz). */
    public String posiadacz() { return posiadacz; }
    /** Phone number (telefon). */
    public String telefon() { return telefon; }
    /** Email address (email). */
    public String email() { return email; }
    /** Invalidation timestamp in ISO-8601 format; {@code null} if still valid. */
    public String uniewazniono() { return uniewazniono; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder();
        b.start = this.start;
        b.kod = this.kod;
        b.posiadacz = this.posiadacz;
        b.telefon = this.telefon;
        b.email = this.email;
        b.uniewazniono = this.uniewazniono;
        return b;
    }

    /**
     * Builder for {@link KartaLojQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String kod;
        private String posiadacz;
        private String telefon;
        private String email;
        private String uniewazniono;

        private Builder() {}

        /** Sets Zero-based record offset for pagination (first record index on the page). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }
        /** Sets Code (kod). @return this builder */
        public Builder kod(String kod) { this.kod = kod; return this; }
        /** Sets Cardholder name (posiadacz). @return this builder */
        public Builder posiadacz(String posiadacz) { this.posiadacz = posiadacz; return this; }
        /** Sets Phone number (telefon). @return this builder */
        public Builder telefon(String telefon) { this.telefon = telefon; return this; }
        /** Sets Email address (email). @return this builder */
        public Builder email(String email) { this.email = email; return this; }
        /** Sets Invalidation timestamp in ISO-8601 format; {@code null} if still valid. @return this builder */
        public Builder uniewazniono(String uniewazniono) { this.uniewazniono = uniewazniono; return this; }

        /**
         * Builds the {@link KartaLojQueryBuilder}.
         *
         * @return a new {@link KartaLojQueryBuilder} instance
         */
        public KartaLojQueryBuilder build() { return new KartaLojQueryBuilder(this); }
    }
}
