/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary;

/**
 * Immutable data transfer object for creating a new jmiary record. Required: {@code nazwa}.
 *
 * <p>Required fields are enforced by the builder factory method.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class JmiaryCreateBuilder {

    private final Long id;
    private final String nazwa;
    private final Integer precyzja;

    private JmiaryCreateBuilder(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.precyzja = builder.precyzja;
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
    /** Decimal precision for quantities (precyzja). */
    public Integer precyzja() { return precyzja; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.nazwa);
        b.id = this.id;
        b.precyzja = this.precyzja;
        return b;
    }

    /**
     * Builder for {@link JmiaryCreateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private Integer precyzja;

        private Builder(String nazwa) { this.nazwa = nazwa; }

        /** Sets Record ID. @return this builder */
        public Builder id(Long id) { this.id = id; return this; }
        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Decimal precision for quantities (precyzja). @return this builder */
        public Builder precyzja(Integer precyzja) { this.precyzja = precyzja; return this; }

        /**
         * Builds the {@link JmiaryCreateBuilder}.
         *
         * @return a new {@link JmiaryCreateBuilder} instance
         */
        public JmiaryCreateBuilder build() { return new JmiaryCreateBuilder(this); }
    }
}
