/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.KasjerRaw;

/**
 * Immutable SDK model for the {@code kasjer} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record Kasjer(Long id, String nazwisko, String kodKasjera, Boolean aktywny) {

    /**
     * Creates an immutable {@code Kasjer} from the generated {@code KasjerRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code Kasjer}
     */
    public static Kasjer from(KasjerRaw raw) {
        return new Kasjer(
                raw.getId(),
                raw.getNazwisko(),
                raw.getKodKasjera(),
                raw.getAktywny()
        );
    }
}
