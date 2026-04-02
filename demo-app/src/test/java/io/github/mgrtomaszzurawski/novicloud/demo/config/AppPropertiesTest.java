/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.config;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class AppPropertiesTest {

    private static final String PROP_DEMO_MODE = "demo.mode";
    private static final String MODE_CRUD_SAFE = "CRUD_SAFE";
    private static final String MODE_READ_ONLY_LOWER = "read_only";
    private static final String EMPTY = "";

    @Test
    void demoMode_whenPropertySet_returnsParsedValue() {
        // given
        Properties props = new Properties();
        props.setProperty(PROP_DEMO_MODE, MODE_CRUD_SAFE);

        // when
        AppProperties appProps = AppProperties.from(props);

        // then
        assertEquals(MODE_CRUD_SAFE, appProps.demoMode());
    }

    @Test
    void demoMode_whenPropertyMissing_returnsEmpty() {
        // given
        Properties props = new Properties();

        // when
        AppProperties appProps = AppProperties.from(props);

        // then
        assertEquals(EMPTY, appProps.demoMode());
    }

    @Test
    void demoMode_whenLowerCase_preservesOriginalCase() {
        // given
        Properties props = new Properties();
        props.setProperty(PROP_DEMO_MODE, MODE_READ_ONLY_LOWER);

        // when
        AppProperties appProps = AppProperties.from(props);

        // then
        assertEquals(MODE_READ_ONLY_LOWER, appProps.demoMode());
    }
}
