/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Immutable SDK model for the {@code dokument} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record Dokument(
        Long id,
        Integer typDok,
        LocalDateTime dataWystawienia,
        LocalDate dataWplywu,
        LocalDate dataWykonania,
        String nrDok,
        String kartaRabatowa,
        String nipNaPar,
        String nrSystemowy,
        String nrFiskalny,
        String nrRapDobowego,
        String komentarz,
        Boolean storno,
        Double netto,
        Double podatek,
        Double brutto,
        Double rabat,
        LocalDateTime terminPlatn,
        Double zaplacono,
        String sklepId,
        String sklepOdbId,
        String kontrahentId,
        String platnikId,
        String kasaId,
        String kasjerId,
        String formaPlatnId,
        String dotyczyId,
        String pozycjeLink,
        String pozycjeId,
        String pozycjeUrl,
        List<DokumentRozbicieVat> rozbicieVat,
        List<Platnosc> platnosci,
        List<String> korektyIds,
        List<String> fakturyIds,
        List<String> dokMagazynoweIds,
        List<String> paragonyIds,
        List<DokumentRozliczany> dokRozliczane
)
{

    /**
     * Resource ID of the line-items endpoint for this document.
     *
     * <p>Despite the name, this is the {@code id} extracted from the {@code pozycje}
     * link object, not the URL. Prefer {@link #pozycjeId()} for clarity, or
     * {@link #pozycjeUrl()} if you actually need the absolute URL.
     *
     * @return the line-items resource ID; {@code null} if absent
     * @deprecated since 2.0.0 - misleading name. Use {@link #pozycjeId()} or
     *     {@link #pozycjeUrl()}. Scheduled for removal in 3.0.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    @Override
    @SuppressWarnings({
        "java:S6207", // Override is the only way to attach @Deprecated to a record component accessor.
        "java:S1133"  // Deprecation kept through 2.x as a deliberate migration window for 1.0 callers.
    })
    public String pozycjeLink() {
        return pozycjeLink;
    }
}
