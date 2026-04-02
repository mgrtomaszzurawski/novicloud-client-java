/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn;

/**
 * Immutable data transfer object for updating an existing formaplatn record.
 * The {@code id} field identifies the record to update and is required.
 *
 * <p>Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class FormaPlatnUpdateBuilder {

    private final Long id;
    private final String nazwa;
    private final Integer typ;
    private final Boolean reszta;
    private final Boolean aktywny;

    private FormaPlatnUpdateBuilder(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.typ = builder.typ;
        this.reszta = builder.reszta;
        this.aktywny = builder.aktywny;
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
    /** Record type code (typ). */
    public Integer typ() { return typ; }
    /** reszta value. */
    public Boolean reszta() { return reszta; }
    /** Whether the record is active. Soft-deleted records have {@code aktywny = false}. */
    public Boolean aktywny() { return aktywny; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.id);
        b.nazwa = this.nazwa;
        b.typ = this.typ;
        b.reszta = this.reszta;
        b.aktywny = this.aktywny;
        return b;
    }

    /**
     * Builder for {@link FormaPlatnUpdateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private Integer typ;
        private Boolean reszta;
        private Boolean aktywny;

        private Builder(Long id) { this.id = id; }

        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Record type code (typ). @return this builder */
        public Builder typ(Integer typ) { this.typ = typ; return this; }
        /** Sets reszta value. @return this builder */
        public Builder reszta(Boolean reszta) { this.reszta = reszta; return this; }
        /** Sets active flag. Use {@code true} to reactivate a soft-deleted record. @return this builder */
        public Builder aktywny(Boolean aktywny) { this.aktywny = aktywny; return this; }

        /**
         * Builds the {@link FormaPlatnUpdateBuilder}.
         *
         * @return a new {@link FormaPlatnUpdateBuilder} instance
         */
        public FormaPlatnUpdateBuilder build() { return new FormaPlatnUpdateBuilder(this); }
    }
}
