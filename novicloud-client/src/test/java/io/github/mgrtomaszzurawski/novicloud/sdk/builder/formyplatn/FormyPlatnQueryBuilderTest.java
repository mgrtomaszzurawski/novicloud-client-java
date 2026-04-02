/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.formyplatn;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn.FormaPlatnQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormyPlatnQueryBuilderTest {

    private static final int TB_START = 1;
    private static final String TB_ID = "test-id";

    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        FormaPlatnQueryBuilder q = FormaPlatnQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.id());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        FormaPlatnQueryBuilder original = FormaPlatnQueryBuilder.builder()
                .start(TB_START)
                .id(TB_ID)
                .build();

        // when
        FormaPlatnQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.id(), copy.id());
    }
}
