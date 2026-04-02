/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn;

/**
 * Immutable filter and pagination parameters for listing formaplatn records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 *
 * <p><b>Note:</b> The {@code nazwa} and {@code typ} fields are not filterable on this endpoint
 * (documentation marks them as "Brak"). Only {@code id} supports server-side filtering.
 * @since 1.0.0
 */
public final class FormaPlatnQueryBuilder {

    private final Integer start;
    private final String id;

    private FormaPlatnQueryBuilder(Builder builder) {
        this.start = builder.start;
        this.id = builder.id;
    }

    /**
     * Creates a new builder with all fields unset.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() { return new Builder(); }

    /** Zero-based record offset for pagination (first record index on the page). */
    public Integer start() { return start; }
    /** Record ID. Supports list or range format (e.g. {@code "1"}, {@code "1,2,3"}, {@code "min1"}). */
    public String id() { return id; }

    /**
     * Creates a new {@link Builder} pre-populated with the values from this instance.
     *
     * @return a new {@link Builder} with all fields copied
     */
    public Builder toBuilder() {
        Builder b = new Builder();
        b.start = this.start;
        b.id = this.id;
        return b;
    }

    /**
     * Builder for {@link FormaPlatnQueryBuilder}.
     */
    public static final class Builder {
        private Integer start;
        private String id;

        private Builder() {}

        /** Sets Zero-based record offset for pagination (first record index on the page). @return this builder */
        public Builder start(Integer start) { this.start = start; return this; }
        /** Sets Record ID. @return this builder */
        public Builder id(String id) { this.id = id; return this; }

        /**
         * Builds the {@link FormaPlatnQueryBuilder}.
         *
         * @return a new {@link FormaPlatnQueryBuilder} instance
         */
        public FormaPlatnQueryBuilder build() { return new FormaPlatnQueryBuilder(this); }
    }
}
