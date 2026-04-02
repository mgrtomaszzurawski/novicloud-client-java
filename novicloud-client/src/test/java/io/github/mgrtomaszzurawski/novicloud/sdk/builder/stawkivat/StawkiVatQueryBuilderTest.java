/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.stawkivat;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat.StawkaVatQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StawkiVatQueryBuilderTest {

    private static final String ID = "23";
    private static final int TB_START = 1;
    private static final String TB_ID = "test-id";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        StawkaVatQueryBuilder q = StawkaVatQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.id());
    }

    @Test
    void build_whenSelectedFieldsSet_valuesPreserved() {
        // given / when
        StawkaVatQueryBuilder q = StawkaVatQueryBuilder.builder().id(ID).build();

        // then
        assertEquals(ID, q.id());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        StawkaVatQueryBuilder original = StawkaVatQueryBuilder.builder()
                .start(TB_START)
                .id(TB_ID)
                .build();

        // when
        StawkaVatQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.id(), copy.id());
    }
}
