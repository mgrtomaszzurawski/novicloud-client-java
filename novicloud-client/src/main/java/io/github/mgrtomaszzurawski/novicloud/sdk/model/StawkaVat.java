/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.StawkaVatRaw;

/**
 * Immutable SDK model for the {@code stawkavat} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record StawkaVat(Integer id, String opis, Etykieta etykieta) {

    /** String code from the NoviCloud API. */
    public enum Etykieta {
        A("A"),
        B("B"),
        C("C"),
        D("D"),
        E("E"),
        F("F"),
        G("G");

        private final String code;
        Etykieta(String code) { this.code = code; }
        /** Returns the string code as used by the NoviCloud API. */
        public String code() { return code; }

        /** Resolves enum from API string code; returns {@code null} if unknown. */
        public static Etykieta fromCode(String code) {
            for (Etykieta v : values()) {
                if (v.code.equals(code)) { return v; }
            }
            return null;
        }
    }

    /**
     * Creates an immutable {@code StawkaVat} from the generated {@code StawkaVatRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code StawkaVat}
     */
    public static StawkaVat from(StawkaVatRaw raw) {
        return new StawkaVat(
                raw.getId(),
                raw.getOpis(),
                raw.getEtykieta() != null ? Etykieta.fromCode(raw.getEtykieta().getValue()) : null
        );
    }
}
