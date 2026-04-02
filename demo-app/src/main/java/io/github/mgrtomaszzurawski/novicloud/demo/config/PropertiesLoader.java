/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesLoader {

    private static final String ERR_LOAD_PREFIX = "Failed to load properties: ";

    private PropertiesLoader() {
    }

    public static Properties load(String resourceName) {
        Properties props = new Properties();
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            if (stream != null) {
                props.load(stream);
            }
        } catch (IOException e) {
            throw new IllegalStateException(ERR_LOAD_PREFIX + resourceName, e);
        }
        return props;
    }
}
