/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje;

/**
 * Immutable data transfer object for updating an existing kraj record.
 * The {@code id} field identifies the record to update and is required.
 *
 * <p>Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class KrajUpdateBuilder {

    private final Long id;
    private final String nazwa;
    private final String kod;
    private final String walutaId;

    private KrajUpdateBuilder(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.kod = builder.kod;
        this.walutaId = builder.walutaId;
    }

    /**
     * Creates a new builder with the required fields pre-set.
     *
     * @param id  Record ID (required)
     * @return a new {@link Builder}
     */
    public static Builder builder(Long id) { return new Builder(id); }

    /** Record ID. */
    public Long id() { return id; }
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
        Builder b = new Builder(this.id);
        b.nazwa = this.nazwa;
        b.kod = this.kod;
        b.walutaId = this.walutaId;
        return b;
    }

    /**
     * Builder for {@link KrajUpdateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private String kod;
        private String walutaId;

        private Builder(Long id) { this.id = id; }

        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Code (kod). @return this builder */
        public Builder kod(String kod) { this.kod = kod; return this; }
        /** Sets Default currency ID link for this country (walutaId). @return this builder */
        public Builder walutaId(String walutaId) { this.walutaId = walutaId; return this; }

        /**
         * Builds the {@link KrajUpdateBuilder}.
         *
         * @return a new {@link KrajUpdateBuilder} instance
         */
        public KrajUpdateBuilder build() { return new KrajUpdateBuilder(this); }
    }
}
