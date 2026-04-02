/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.WalutaRaw;

/**
 * Immutable SDK model for the {@code waluta} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record Waluta(
        Long id,
        String nazwa,
        String kod,
        Double kurs,
        Boolean domyslna,
        Boolean aktywny
)
{

    /**
     * Creates an immutable {@code Waluta} from the generated {@code WalutaRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code Waluta}
     */
    public static Waluta from(WalutaRaw raw) {
        return new Waluta(
                raw.getId(),
                raw.getNazwa(),
                raw.getKod(),
                raw.getKurs(),
                raw.getDomyslna(),
                raw.getAktywny()
        );
    }
}
