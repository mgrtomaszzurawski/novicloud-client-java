/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.kraje;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje.KrajQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KrajeQueryBuilderTest {

    private static final String NAZWA_FILTER = "~Pol~";
    private static final int TB_START = 1;
    private static final String TB_FTS = "test-fts";
    private static final String TB_ID = "test-id";
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_KOD = "test-kod";
    private static final String TB_WALUTA_ID = "test-walutaId";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        KrajQueryBuilder q = KrajQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.id());
        assertNull(q.nazwa());
    }

    @Test
    void build_whenSelectedFieldsSet_valuesPreserved() {
        // given / when
        KrajQueryBuilder q = KrajQueryBuilder.builder().nazwa(NAZWA_FILTER).build();

        // then
        assertEquals(NAZWA_FILTER, q.nazwa());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        KrajQueryBuilder original = KrajQueryBuilder.builder()
                .start(TB_START)
                .fts(TB_FTS)
                .id(TB_ID)
                .nazwa(TB_NAZWA)
                .kod(TB_KOD)
                .walutaId(TB_WALUTA_ID)
                .build();

        // when
        KrajQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.fts(), copy.fts());
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.kod(), copy.kod());
        assertEquals(original.walutaId(), copy.walutaId());
    }
}
