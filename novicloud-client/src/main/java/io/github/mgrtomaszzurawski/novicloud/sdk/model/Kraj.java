/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.KrajRaw;

/**
 * Immutable SDK model for the {@code kraj} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record Kraj(Long id, String nazwa, String kod, String walutaId) {

    /**
     * Creates an immutable {@code Kraj} from the generated {@code KrajRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code Kraj}
     */
    public static Kraj from(KrajRaw raw) {
        return new Kraj(
                raw.getId(),
                raw.getNazwa(),
                raw.getKod(),
                LinkUtils.extractId(raw.getWaluta())
        );
    }
}
