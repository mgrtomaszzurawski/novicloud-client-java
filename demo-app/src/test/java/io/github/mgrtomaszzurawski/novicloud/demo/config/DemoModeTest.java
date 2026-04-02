/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class DemoModeTest {

    private static final int EXPECTED_MODE_COUNT = 4;
    private static final String INVALID_MODE = "INVALID";
    private static final String TYPO_CUD_SAFE = "CUD_SAFE";
    private static final String TYPO_CUD_ALL = "CUD_ALL";

    @Test
    void values_whenEnumQueried_returnsFourModes() {
        // given / when
        DemoMode[] modes = DemoMode.values();

        // then
        assertEquals(EXPECTED_MODE_COUNT, modes.length);
        assertNotNull(DemoMode.READ_ONLY);
        assertNotNull(DemoMode.CRUD_SAFE);
        assertNotNull(DemoMode.CREATE_SOFT);
        assertNotNull(DemoMode.CRUD_ALL);
    }

    @ParameterizedTest
    @EnumSource(DemoMode.class)
    void valueOf_whenValidMode_returnsNonNull(DemoMode mode) {
        // given / when / then
        assertNotNull(mode);
    }

    @Test
    void valueOf_whenInvalidName_throwsIllegalArgument() {
        // given / when / then
        assertThrows(IllegalArgumentException.class, () -> DemoMode.valueOf(INVALID_MODE));
    }

    @Test
    void valueOf_whenTypoWithoutPrefix_throwsIllegalArgument() {
        // given / when / then
        assertThrows(IllegalArgumentException.class, () -> DemoMode.valueOf(TYPO_CUD_SAFE));
        assertThrows(IllegalArgumentException.class, () -> DemoMode.valueOf(TYPO_CUD_ALL));
    }
}
