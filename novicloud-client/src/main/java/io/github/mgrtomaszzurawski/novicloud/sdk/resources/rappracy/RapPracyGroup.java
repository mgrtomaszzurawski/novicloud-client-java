/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy;

/**
 * Grouping mode for the work report ({@code /rappracy}) endpoint.
 *
 * <p>Controls how the server aggregates work time data. When no grouping is specified,
 * the server returns a single summary record.
 *
 * <p>Values are case-sensitive lowercase strings required by the NoviCloud API.
 *
 * @see RapPracyQueryBuilder
 * @since 1.0.0
 */
public enum RapPracyGroup {

    /** Group by store (sklep). Response includes {@code sklep} link object. */
    SKLEP("sklep"),
    /** Group by cash register (kasa). Response includes {@code kasa} link object. */
    KASA("kasa"),
    /** Group by cashier (kasjer). Response includes {@code kasjer} link object. */
    KASJER("kasjer");

    private final String value;

    RapPracyGroup(String value) { this.value = value; }

    /** Returns the lowercase API parameter value. */
    public String value() { return value; }
}
