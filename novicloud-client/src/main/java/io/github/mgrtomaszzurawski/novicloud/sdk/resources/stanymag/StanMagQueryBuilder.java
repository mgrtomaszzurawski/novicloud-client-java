/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.stanymag;

/**
 * Immutable filter and pagination parameters for listing stanmag records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class StanMagQueryBuilder {

    private final Integer start;
    private final String towarId;
    private final String sklepId;
    private final String naDzien;

    private StanMagQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.towarId = builder.towarId;
        this.sklepId = builder.sklepId;
        this.naDzien = builder.naDzien;
    }

    /**
     * Creates a new builder with all fields unset.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() { return new Builder(); }

    /** Zero-based record offset for pagination (first record index on the page). */
    public Integer start() { return start; }
    /** Towar ID link (towarId). */
    public String towarId() { return towarId; }
    /** Sklep (store) ID link (sklepId). */
    public String sklepId() { return sklepId; }
    /** As-of date in {@code YYYY-MM-DD} format; {@code null} for current state. */
    public String naDzien() { return naDzien; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder();
        b.start = this.start;
        b.towarId = this.towarId;
        b.sklepId = this.sklepId;
        b.naDzien = this.naDzien;
        return b;
    }

    /**
     * Builder for {@link StanMagQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String towarId;
        private String sklepId;
        private String naDzien;

        private Builder() {}

        /** Sets Zero-based record offset for pagination (first record index on the page). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }
        /** Sets Towar ID link (towarId). @return this builder */
        public Builder towarId(String towarId) { this.towarId = towarId; return this; }
        /** Sets Sklep (store) ID link (sklepId). @return this builder */
        public Builder sklepId(String sklepId) { this.sklepId = sklepId; return this; }
        /** Sets As-of date in {@code YYYY-MM-DD} format; {@code null} for current state. @return this builder */
        public Builder naDzien(String naDzien) { this.naDzien = naDzien; return this; }

        /**
         * Builds the {@link StanMagQueryBuilder}.
         *
         * @return a new {@link StanMagQueryBuilder} instance
         */
        public StanMagQueryBuilder build() { return new StanMagQueryBuilder(this); }
    }
}
