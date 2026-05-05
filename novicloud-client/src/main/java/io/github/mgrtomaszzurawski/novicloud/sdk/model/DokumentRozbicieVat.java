/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

/**
 * Immutable SDK model for a single VAT-rate breakdown row on a document.
 *
 * <p>Used inside {@link Dokument#rozbicieVat()}.
 *
 * @param stawka  VAT rate in hundredths (e.g. 2300 for 23%, -1 for exempt)
 * @param netto   net amount for this rate
 * @param podatek tax amount for this rate
 * @param brutto  gross amount for this rate
 * @since 2.0.0
 */
public record DokumentRozbicieVat(
        Integer stawka,
        Double netto,
        Double podatek,
        Double brutto
)
{
}
