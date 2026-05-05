/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

/**
 * Immutable SDK model for a single payment entry on a document or sale.
 *
 * <p>Used inside {@link Dokument#platnosci()} and {@link Sprzedaz#platnosci()}.
 *
 * @param formaPlatnosciId      payment-method ID (links to {@code formyplatn})
 * @param walutaId              currency ID (links to {@code waluty})
 * @param typFormyPlatnosci     payment-method type code (mirrors {@code FormaPlatn.typ})
 * @param kodWaluty             ISO currency code (e.g. {@code "PLN"})
 * @param kurs                  exchange rate at the time of payment
 * @param wplataWaluta          deposit in the original currency
 * @param wplataWalutaDomyslna  deposit converted to the system default currency
 * @param resztaWaluta          change given in the original currency
 * @param resztaWalutaDomyslna  change converted to the system default currency
 * @param dokPlatnosciId        related payment-document ID, if any
 * @param kwota                 final settled amount
 * @since 2.0.0
 */
public record Platnosc(
        String formaPlatnosciId,
        String walutaId,
        Integer typFormyPlatnosci,
        String kodWaluty,
        Double kurs,
        Double wplataWaluta,
        Double wplataWalutaDomyslna,
        Double resztaWaluta,
        Double resztaWalutaDomyslna,
        String dokPlatnosciId,
        Double kwota
)
{
}
