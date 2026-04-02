/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.kartyloj;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj.KartaLojQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KartyLojQueryBuilderTest {

    private static final int START = 0;
    private static final String POSIADACZ_TRUE = "true";
    private static final String POSIADACZ_FALSE = "false";
    private static final String TELEFON = "~48~";
    private static final String EMAIL = "~test~";
    private static final int TB_START = 1;
    private static final String TB_KOD = "test-kod";
    private static final String TB_POSIADACZ = "test-posiadacz";
    private static final String TB_TELEFON = "test-telefon";
    private static final String TB_EMAIL = "test-email";
    private static final String TB_UNIEWAZNIONO = "test-uniewazniono";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        KartaLojQueryBuilder q = KartaLojQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.kod());
        assertNull(q.posiadacz());
        assertNull(q.telefon());
        assertNull(q.email());
        assertNull(q.uniewazniono());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        KartaLojQueryBuilder q = KartaLojQueryBuilder.builder()
                .start(START)
                .kod(KartaLojTestConstants.KOD)
                .posiadacz(POSIADACZ_TRUE)
                .telefon(TELEFON)
                .email(EMAIL)
                .uniewazniono(POSIADACZ_FALSE)
                .build();

        // then
        assertEquals(START, q.start());
        assertEquals(KartaLojTestConstants.KOD, q.kod());
        assertEquals(POSIADACZ_TRUE, q.posiadacz());
        assertEquals(TELEFON, q.telefon());
        assertEquals(EMAIL, q.email());
        assertEquals(POSIADACZ_FALSE, q.uniewazniono());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        KartaLojQueryBuilder original = KartaLojQueryBuilder.builder()
                .start(TB_START)
                .kod(TB_KOD)
                .posiadacz(TB_POSIADACZ)
                .telefon(TB_TELEFON)
                .email(TB_EMAIL)
                .uniewazniono(TB_UNIEWAZNIONO)
                .build();

        // when
        KartaLojQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.kod(), copy.kod());
        assertEquals(original.posiadacz(), copy.posiadacz());
        assertEquals(original.telefon(), copy.telefon());
        assertEquals(original.email(), copy.email());
        assertEquals(original.uniewazniono(), copy.uniewazniono());
    }
}
