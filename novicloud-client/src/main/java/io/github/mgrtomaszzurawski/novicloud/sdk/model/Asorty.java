/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.AsortyRaw;

/**
 * Immutable SDK model for the {@code asorty} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record Asorty(Long id, String nazwa, String parentId) {

    /**
     * Creates an immutable {@code Asorty} from the generated {@code AsortyRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code Asorty}
     */
    public static Asorty from(AsortyRaw raw) {
        return new Asorty(
                raw.getId(),
                raw.getNazwa(),
                LinkUtils.extractId(raw.getParent())
        );
    }
}
