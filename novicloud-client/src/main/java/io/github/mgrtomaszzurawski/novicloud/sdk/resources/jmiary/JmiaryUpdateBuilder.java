/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary;

/**
 * Immutable data transfer object for updating an existing jmiary record.
 * The {@code id} field identifies the record to update and is required.
 *
 * <p>Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class JmiaryUpdateBuilder {

    private final Long id;
    private final String nazwa;
    private final Integer precyzja;

    private JmiaryUpdateBuilder(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.precyzja = builder.precyzja;
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
    /** Decimal precision for quantities (precyzja). */
    public Integer precyzja() { return precyzja; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.id);
        b.nazwa = this.nazwa;
        b.precyzja = this.precyzja;
        return b;
    }

    /**
     * Builder for {@link JmiaryUpdateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private Integer precyzja;

        private Builder(Long id) { this.id = id; }

        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Decimal precision for quantities (precyzja). @return this builder */
        public Builder precyzja(Integer precyzja) { this.precyzja = precyzja; return this; }

        /**
         * Builds the {@link JmiaryUpdateBuilder}.
         *
         * @return a new {@link JmiaryUpdateBuilder} instance
         */
        public JmiaryUpdateBuilder build() { return new JmiaryUpdateBuilder(this); }
    }
}
