/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.rapsprzed;

/**
 * Grouping mode for the sales report ({@code /rapsprzed}) endpoint.
 *
 * <p>Controls how the server aggregates sales data. The first record in the response
 * is always a summary row; subsequent records are grouped by the selected dimension.
 * When no grouping is specified, the server returns a single summary record.
 *
 * <p>Values are case-sensitive lowercase strings required by the NoviCloud API.
 *
 * @see RapSprzedQueryBuilder
 * @since 1.0.0
 */
public enum RapSprzedGroup {

    /** Group by product (towar). Response includes {@code towar} link object. */
    TOWAR("towar"),
    /** Group by assortment (asortyment). Response includes {@code asort} link object. */
    ASORT("asort"),
    /** Group by store (sklep). Response includes {@code sklep} link object. */
    SKLEP("sklep"),
    /** Group by cash register (kasa). Response includes {@code kasa} link object. */
    KASA("kasa"),
    /** Group by cashier (kasjer). Response includes {@code kasjer} link object. */
    KASJER("kasjer"),
    /** Group by contractor (kontrahent). Response includes {@code kontrahent} link object. */
    KONTR("kontr"),
    /** Group by loyalty card (karta rabatowa). Response includes {@code karta_rabatowa} field. */
    KARTARAB("kartarab"),
    /** Group by payment form (forma platnosci). Response includes {@code forma_platn} link object. */
    FORMAPLATN("formaplatn");

    private final String value;

    RapSprzedGroup(String value) { this.value = value; }

    /** Returns the lowercase API parameter value. */
    public String value() { return value; }
}
