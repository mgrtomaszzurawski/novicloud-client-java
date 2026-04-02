/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.TowarRaw;
import java.time.LocalDateTime;

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
        PrzySprzedazy przySprzedazy
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

    /**
     * Creates an immutable {@code Towar} from the generated {@code TowarRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code Towar}
     */
    public static Towar from(TowarRaw raw) {
        return new Towar(
                raw.getId(),
                raw.getNazwa(),
                raw.getKod(),
                raw.getCku(),
                raw.getStawkaVat(),
                raw.getAkcyzowy(),
                raw.getCenaEw(),
                raw.getCenaDet(),
                raw.getCenaHurt(),
                raw.getCenaNoc(),
                raw.getCenaDod(),
                raw.getGtu(),
                raw.getPkwiu(),
                raw.getMasaWl(),
                raw.getAktywny(),
                raw.getOpis1(),
                raw.getOpis2(),
                raw.getOpis3(),
                raw.getOpis4(),
                raw.getOpis5(),
                raw.getOstZmiana(),
                LinkUtils.extractId(raw.getJm()),
                LinkUtils.extractId(raw.getAsort()),
                raw.getTyp() != null ? Typ.fromCode(raw.getTyp().getValue()) : null,
                raw.getPrzySprzedazy() != null ? PrzySprzedazy.fromCode(raw.getPrzySprzedazy().getValue()) : null
        );
    }
}
