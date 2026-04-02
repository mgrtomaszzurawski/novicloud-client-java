/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.stanymag;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stanymag.StanMagUpdateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StanMagUpdateBuilderTest {

    private static final double ILOSC = 100.5;
    private static final String TOWAR_ID_TYPE_CHECK = "123";
    private static final double ILOSC_ZERO = 0.0;
    private static final String TB_TOWAR_ID = "test-towarId";
    private static final String TB_SKLEP_ID = "test-sklepId";

    @Test
    void build_whenRequiredFields_allValuesPreserved() {
        // given / when
        StanMagUpdateBuilder d = StanMagUpdateBuilder.builder(
                StanMagTestConstants.TOWAR_ID, StanMagTestConstants.SKLEP_ID, ILOSC).build();

        // then
        assertEquals(StanMagTestConstants.TOWAR_ID, d.towarId());
        assertEquals(StanMagTestConstants.SKLEP_ID, d.sklepId());
        assertEquals(ILOSC, d.ilosc());
    }

    @Test
    void build_whenTowarId_typeIsString() {
        // given
        // Documents the Link.id = String decision (ADR-011).
        // If someone changes the type to Long, this will fail at compile time - intentional.

        // when
        StanMagUpdateBuilder d = StanMagUpdateBuilder.builder(
                TOWAR_ID_TYPE_CHECK, StanMagTestConstants.SKLEP_ID, ILOSC_ZERO).build();

        // then
        assertInstanceOf(String.class, d.towarId());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        StanMagUpdateBuilder original = StanMagUpdateBuilder.builder(TB_TOWAR_ID, TB_SKLEP_ID, 103.107)
                .build();

        // when
        StanMagUpdateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.towarId(), copy.towarId());
        assertEquals(original.sklepId(), copy.sklepId());
        assertEquals(original.ilosc(), copy.ilosc());
    }
}
