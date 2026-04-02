/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje;

/**
 * Immutable filter and pagination parameters for listing kraj records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class KrajQueryBuilder {

    private final Integer start;
    private final String fts;
    private final String id;
    private final String nazwa;
    private final String kod;
    private final String walutaId;

    private KrajQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.fts = builder.fts;
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.kod = builder.kod;
        this.walutaId = builder.walutaId;
    }

    /**
     * Creates a new builder with all fields unset.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() { return new Builder(); }

    /** Zero-based record offset for pagination (first record index on the page). */
    public Integer start() { return start; }
    /** Full-text search filter; searches across key text fields. */
    public String fts() { return fts; }
    /** Record ID. */
    public String id() { return id; }
    /** Name (nazwa). */
    public String nazwa() { return nazwa; }
    /** Code (kod). */
    public String kod() { return kod; }
    /** Default currency ID link for this country (walutaId). */
    public String walutaId() { return walutaId; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder();
        b.start = this.start;
        b.fts = this.fts;
        b.id = this.id;
        b.nazwa = this.nazwa;
        b.kod = this.kod;
        b.walutaId = this.walutaId;
        return b;
    }

    /**
     * Builder for {@link KrajQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String fts;
        private String id;
        private String nazwa;
        private String kod;
        private String walutaId;

        private Builder() {}

        /** Sets Zero-based record offset for pagination (first record index on the page). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }
        /** Sets Full-text search filter; searches across key text fields. @return this builder */
        public Builder fts(String fts) { this.fts = fts; return this; }
        /** Sets Record ID. @return this builder */
        public Builder id(String id) { this.id = id; return this; }
        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Code (kod). @return this builder */
        public Builder kod(String kod) { this.kod = kod; return this; }
        /** Sets Default currency ID link for this country (walutaId). @return this builder */
        public Builder walutaId(String walutaId) { this.walutaId = walutaId; return this; }

        /**
         * Builds the {@link KrajQueryBuilder}.
         *
         * @return a new {@link KrajQueryBuilder} instance
         */
        public KrajQueryBuilder build() { return new KrajQueryBuilder(this); }
    }
}
