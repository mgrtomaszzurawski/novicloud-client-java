/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.kasjerzy;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasjerzy.KasjerQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KasjerzyQueryBuilderTest {

    private static final String NAZWISKO = "~Kowal~";
    private static final String KOD_KASJERA = "K1";
    private static final int TB_START = 1;
    private static final String TB_ID = "test-id";
    private static final String TB_NAZWISKO = "test-nazwisko";
    private static final String TB_KOD_KASJERA = "test-kodKasjera";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        KasjerQueryBuilder q = KasjerQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.id());
        assertNull(q.nazwisko());
        assertNull(q.kodKasjera());
    }

    @Test
    void build_whenSelectedFieldsSet_valuesPreserved() {
        // given / when
        KasjerQueryBuilder q = KasjerQueryBuilder.builder().nazwisko(NAZWISKO).kodKasjera(KOD_KASJERA).build();

        // then
        assertEquals(NAZWISKO, q.nazwisko());
        assertEquals(KOD_KASJERA, q.kodKasjera());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        KasjerQueryBuilder original = KasjerQueryBuilder.builder()
                .start(TB_START)
                .id(TB_ID)
                .nazwisko(TB_NAZWISKO)
                .kodKasjera(TB_KOD_KASJERA)
                .aktywny(true)
                .build();

        // when
        KasjerQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwisko(), copy.nazwisko());
        assertEquals(original.kodKasjera(), copy.kodKasjera());
        assertEquals(original.aktywny(), copy.aktywny());
    }
}
