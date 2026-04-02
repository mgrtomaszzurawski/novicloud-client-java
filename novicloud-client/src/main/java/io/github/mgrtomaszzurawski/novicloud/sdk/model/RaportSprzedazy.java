/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.RaportSprzedazyRaw;

/**
 * Immutable SDK model for the {@code raportsprzedazy} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record RaportSprzedazy(
        String kartaRabatowa,
        Double ilosc,
        Double sprzNetto,
        Double sprzBrutto,
        Double sprzZakNetto,
        Double sprzZakBrutto,
        Double marzaNetto,
        Double marzaBrutto,
        Double marzaProcNetto,
        Double marzaProcBrutto,
        Double narzutProcNetto,
        Double narzutProcBrutto,
        Double rabat,
        Double rabatProc,
        String towarId,
        String asortId,
        String sklepId,
        String kasaId,
        String kasjerId,
        String kontrahentId,
        String formaPlatnId
)
{

    /**
     * Creates an immutable {@code RaportSprzedazy} from the generated {@code RaportSprzedazyRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code RaportSprzedazy}
     */
    public static RaportSprzedazy from(RaportSprzedazyRaw raw) {
        return new RaportSprzedazy(
                raw.getKartaRabatowa(),
                raw.getIlosc(),
                raw.getSprzNetto(),
                raw.getSprzBrutto(),
                raw.getSprzZakNetto(),
                raw.getSprzZakBrutto(),
                raw.getMarzaNetto(),
                raw.getMarzaBrutto(),
                raw.getMarzaProcNetto(),
                raw.getMarzaProcBrutto(),
                raw.getNarzutProcNetto(),
                raw.getNarzutProcBrutto(),
                raw.getRabat(),
                raw.getRabatProc(),
                LinkUtils.extractId(raw.getTowar()),
                LinkUtils.extractId(raw.getAsort()),
                LinkUtils.extractId(raw.getSklep()),
                LinkUtils.extractId(raw.getKasa()),
                LinkUtils.extractId(raw.getKasjer()),
                LinkUtils.extractId(raw.getKontrahent()),
                LinkUtils.extractId(raw.getFormaPlatn())
        );
    }
}
