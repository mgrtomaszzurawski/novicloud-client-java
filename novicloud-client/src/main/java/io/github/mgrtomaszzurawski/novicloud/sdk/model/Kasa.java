/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.KasaRaw;
import java.time.LocalDateTime;

/**
 * Immutable SDK model for the {@code kasa} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record Kasa(
        Long id,
        String nazwa,
        Integer numer,
        String ecr,
        LocalDateTime ostatniaSync,
        LocalDateTime ostatniaSprzed,
        Boolean aktywny
)
{

    /**
     * Creates an immutable {@code Kasa} from the generated {@code KasaRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code Kasa}
     */
    public static Kasa from(KasaRaw raw) {
        return new Kasa(
                raw.getId(),
                raw.getNazwa(),
                raw.getNumer(),
                raw.getEcr(),
                raw.getOstatniaSync(),
                raw.getOstatniaSprzed(),
                raw.getAktywny()
        );
    }
}
