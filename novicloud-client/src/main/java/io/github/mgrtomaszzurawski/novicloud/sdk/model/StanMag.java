/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.StanMagRaw;

/**
 * Immutable SDK model for the {@code stanmag} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record StanMag(
        Double ilosc,
        Double wCZakNetto,
        Double wCZakBrutto,
        Double wCSprzedNetto,
        Double wCSprzedBrutto,
        String towarId,
        String sklepId
)
{

    /**
     * Creates an immutable {@code StanMag} from the generated {@code StanMagRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code StanMag}
     */
    public static StanMag from(StanMagRaw raw) {
        return new StanMag(
                raw.getIlosc(),
                raw.getwCZakNetto(),
                raw.getwCZakBrutto(),
                raw.getwCSprzedNetto(),
                raw.getwCSprzedBrutto(),
                LinkUtils.extractId(raw.getTowar()),
                LinkUtils.extractId(raw.getSklep())
        );
    }
}
