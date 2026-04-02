/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat;

/**
 * Immutable filter and pagination parameters for listing stawki VAT records.
 *
 * <p>All fields are optional. Unset fields ({@code null}) are not sent to the server.
 * Use {@link Builder} to construct an instance.
 * @since 1.0.0
 */
public final class StawkaVatQueryBuilder {

    private final Integer start;
    private final String id;

    private StawkaVatQueryBuilder(Builder builder) {
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
    /** Record ID. */
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
     * Builder for {@link StawkaVatQueryBuilder}.
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
         * Builds the {@link StawkaVatQueryBuilder}.
         *
         * @return a new {@link StawkaVatQueryBuilder} instance
         */
        public StawkaVatQueryBuilder build() { return new StawkaVatQueryBuilder(this); }
    }
}
