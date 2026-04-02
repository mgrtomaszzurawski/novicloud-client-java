/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.pozdok;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.pozdok.PozdokQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PozdokQueryBuilderTest {

    private static final String DOKUMENT_ID = "42";
    private static final int TB_START = 1;
    private static final String TB_DOKUMENT_ID = "test-dokumentId";
    private static final String TB_DOKUMENT_NR_DOK = "test-dokumentNrDok";
    private static final String TB_DOKUMENT_KONTRAHENT_ID = "test-dokumentKontrahentId";
    private static final String TB_DOKUMENT_PLATNIK_ID = "test-dokumentPlatnikId";
    private static final String TB_DOKUMENT_SKLEP_ID = "test-dokumentSklepId";
    private static final String TB_DOKUMENT_KASA_ID = "test-dokumentKasaId";
    private static final String TB_DOKUMENT_KASJER_ID = "test-dokumentKasjerId";
    private static final String TB_TOWAR_ID = "test-towarId";
    private static final String TB_NR_POZYCJI = "test-nrPozycji";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        PozdokQueryBuilder q = PozdokQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.dokumentId());
    }

    @Test
    void build_whenSelectedFieldsSet_valuesPreserved() {
        // given / when
        PozdokQueryBuilder q = PozdokQueryBuilder.builder().dokumentId(DOKUMENT_ID).build();

        // then
        assertEquals(DOKUMENT_ID, q.dokumentId());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        PozdokQueryBuilder original = PozdokQueryBuilder.builder()
                .start(TB_START)
                .dokumentId(TB_DOKUMENT_ID)
                .dokumentNrDok(TB_DOKUMENT_NR_DOK)
                .dokumentKontrahentId(TB_DOKUMENT_KONTRAHENT_ID)
                .dokumentPlatnikId(TB_DOKUMENT_PLATNIK_ID)
                .dokumentSklepId(TB_DOKUMENT_SKLEP_ID)
                .dokumentKasaId(TB_DOKUMENT_KASA_ID)
                .dokumentKasjerId(TB_DOKUMENT_KASJER_ID)
                .towarId(TB_TOWAR_ID)
                .nrPozycji(TB_NR_POZYCJI)
                .build();

        // when
        PozdokQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.dokumentId(), copy.dokumentId());
        assertEquals(original.dokumentNrDok(), copy.dokumentNrDok());
        assertEquals(original.dokumentKontrahentId(), copy.dokumentKontrahentId());
        assertEquals(original.dokumentPlatnikId(), copy.dokumentPlatnikId());
        assertEquals(original.dokumentSklepId(), copy.dokumentSklepId());
        assertEquals(original.dokumentKasaId(), copy.dokumentKasaId());
        assertEquals(original.dokumentKasjerId(), copy.dokumentKasjerId());
        assertEquals(original.towarId(), copy.towarId());
        assertEquals(original.nrPozycji(), copy.nrPozycji());
    }
}
