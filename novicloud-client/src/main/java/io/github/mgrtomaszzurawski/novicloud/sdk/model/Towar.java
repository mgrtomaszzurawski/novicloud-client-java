/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Immutable SDK model for the {@code towar} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record Towar(
        Long id,
        String nazwa,
        String kod,
        String cku,
        Integer stawkaVat,
        Boolean akcyzowy,
        Double cenaEw,
        Double cenaDet,
        Double cenaHurt,
        Double cenaNoc,
        Double cenaDod,
        String gtu,
        String pkwiu,
        Double masaWl,
        Boolean aktywny,
        String opis1,
        String opis2,
        String opis3,
        String opis4,
        String opis5,
        LocalDateTime ostZmiana,
        String jmId,
        String asortId,
        Typ typ,
        PrzySprzedazy przySprzedazy,
        List<TowarKodDodatkowy> kodyDod,
        List<TowarCenaWSklepie> cenyWSklepach,
        List<TowarSkladnik> skladniki
)
{

    /** Numeric code from the NoviCloud API. */
    public enum Typ {
        VALUE_0(0),
        VALUE_2(2),
        VALUE_4(4),
        VALUE_5(5),
        VALUE_6(6),
        VALUE_7(7),
        VALUE_8(8);

        private final int code;
        Typ(int code) { this.code = code; }
        /** Returns the numeric code as used by the NoviCloud API. */
        public int code() { return code; }

        /** Resolves enum from API numeric code; returns {@code null} if unknown. */
        public static Typ fromCode(int code) {
            for (Typ v : values()) {
                if (v.code == code) { return v; }
            }
            return null;
        }
    }

    /** Numeric code from the NoviCloud API. */
    public enum PrzySprzedazy {
        VALUE_0(0),
        VALUE_1(1),
        VALUE_2(2);

        private final int code;
        PrzySprzedazy(int code) { this.code = code; }
        /** Returns the numeric code as used by the NoviCloud API. */
        public int code() { return code; }

        /** Resolves enum from API numeric code; returns {@code null} if unknown. */
        public static PrzySprzedazy fromCode(int code) {
            for (PrzySprzedazy v : values()) {
                if (v.code == code) { return v; }
            }
            return null;
        }
    }
}
