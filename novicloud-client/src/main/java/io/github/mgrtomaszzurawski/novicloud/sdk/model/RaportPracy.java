/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.RaportPracyRaw;

/**
 * Immutable SDK model for the {@code raportpracy} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record RaportPracy(
        Double czasPracy,
        Double utarg,
        Double gotowka,
        Double karta,
        Double czek,
        Double bon,
        Double przelew,
        Double inna,
        Double paragonyIlosc,
        Double paragonyWartosc,
        Double paragonyPozycje,
        Double fakturyIlosc,
        Double fakturyWartosc,
        Double fakturyPozycje,
        Double stornoPozycje,
        Double stornoWartosc,
        Double paragonyAnulowaneIlosc,
        Double paragonyAnulowaneWartosc,
        String sklepId,
        String kasaId,
        String kasjerId
)
{

    /**
     * Creates an immutable {@code RaportPracy} from the generated {@code RaportPracyRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code RaportPracy}
     */
    public static RaportPracy from(RaportPracyRaw raw) {
        return new RaportPracy(
                raw.getCzasPracy(),
                raw.getUtarg(),
                raw.getGotowka(),
                raw.getKarta(),
                raw.getCzek(),
                raw.getBon(),
                raw.getPrzelew(),
                raw.getInna(),
                raw.getParagonyIlosc(),
                raw.getParagonyWartosc(),
                raw.getParagonyPozycje(),
                raw.getFakturyIlosc(),
                raw.getFakturyWartosc(),
                raw.getFakturyPozycje(),
                raw.getStornoPozycje(),
                raw.getStornoWartosc(),
                raw.getParagonyAnulowaneIlosc(),
                raw.getParagonyAnulowaneWartosc(),
                LinkUtils.extractId(raw.getSklep()),
                LinkUtils.extractId(raw.getKasa()),
                LinkUtils.extractId(raw.getKasjer())
        );
    }
}
