/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty;

/**
 * Immutable data transfer object for creating a new asorty record. Required: {@code nazwa}.
 *
 * <p>Required fields are enforced by the builder factory method.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class AsortyCreateBuilder {

    private final Long id;
    private final String nazwa;
    private final String parentId;

    private AsortyCreateBuilder(Builder builder) {
        this.id = builder.id;
        this.nazwa = builder.nazwa;
        this.parentId = builder.parentId;
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
    /** Parent assortment group ID (parentId). */
    public String parentId() { return parentId; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder(this.nazwa);
        b.id = this.id;
        b.parentId = this.parentId;
        return b;
    }

    /**
     * Builder for {@link AsortyCreateBuilder}.
     */
    public static final class Builder {
        private Long id;
        private String nazwa;
        private String parentId;

        private Builder(String nazwa) { this.nazwa = nazwa; }

        /** Sets Record ID. @return this builder */
        public Builder id(Long id) { this.id = id; return this; }
        /** Sets Name (nazwa). @return this builder */
        public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
        /** Sets Parent assortment group ID (parentId). @return this builder */
        public Builder parentId(String parentId) { this.parentId = parentId; return this; }

        /**
         * Builds the {@link AsortyCreateBuilder}.
         *
         * @return a new {@link AsortyCreateBuilder} instance
         */
        public AsortyCreateBuilder build() { return new AsortyCreateBuilder(this); }
    }
}
