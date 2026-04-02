/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty;

/**
 * Immutable data transfer object for updating an existing asorty record.
 * The {@code id} field identifies the record to update and is required.
 *
 * <p>Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class AsortyUpdateBuilder {

    private final Long id;
    private final String nazwa;
    private final String parentId;

    private AsortyUpdateBuilder(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.parentId = builder.parentId;
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
    /** Parent assortment group ID (parentId). */
    public String parentId() { return parentId; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.id);
        b.nazwa = this.nazwa;
        b.parentId = this.parentId;
        return b;
    }

    /**
     * Builder for {@link AsortyUpdateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private String parentId;

        private Builder(Long id) { this.id = id; }

        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Parent assortment group ID (parentId). @return this builder */
        public Builder parentId(String parentId) { this.parentId = parentId; return this; }

        /**
         * Builds the {@link AsortyUpdateBuilder}.
         *
         * @return a new {@link AsortyUpdateBuilder} instance
         */
        public AsortyUpdateBuilder build() { return new AsortyUpdateBuilder(this); }
    }
}
