/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn;

/**
 * Immutable data transfer object for creating a new formaplatn record. Required: {@code nazwa}, {@code typ}.
 *
 * <p>Required fields are enforced by the builder factory method.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class FormaPlatnCreateBuilder {

    private final Long id;
    private final String nazwa;
    private final Integer typ;
    private final Boolean reszta;

    private FormaPlatnCreateBuilder(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.typ = builder.typ;
        this.reszta = builder.reszta;
    }

    /**
     * Creates a new builder with the required fields pre-set.
     *
     * @param nazwa  Name (nazwa) (required)
     * @param typ  Record type code (typ) (required)
     * @return a new {@link Builder}
     */
    public static Builder builder(String nazwa, Integer typ) { return new Builder(nazwa, typ); }

    /** Record ID. */
    public Long id() { return id; }
    /** Name (nazwa). */
    public String nazwa() { return nazwa; }
    /** Record type code (typ). */
    public Integer typ() { return typ; }
    /** reszta value. */
    public Boolean reszta() { return reszta; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.nazwa, this.typ);
        b.id = this.id;
        b.reszta = this.reszta;
        return b;
    }

    /**
     * Builder for {@link FormaPlatnCreateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private Integer typ;
        private Boolean reszta;

        private Builder(String nazwa, Integer typ) { this.nazwa = nazwa; this.typ = typ; }

        /** Sets Record ID. @return this builder */
        public Builder id(Long id) { this.id = id; return this; }
        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Record type code (typ). @return this builder */
        public Builder typ(Integer typ) { this.typ = typ; return this; }
        /** Sets reszta value. @return this builder */
        public Builder reszta(Boolean reszta) { this.reszta = reszta; return this; }

        /**
         * Builds the {@link FormaPlatnCreateBuilder}.
         *
         * @return a new {@link FormaPlatnCreateBuilder} instance
         */
        public FormaPlatnCreateBuilder build() { return new FormaPlatnCreateBuilder(this); }
    }
}
