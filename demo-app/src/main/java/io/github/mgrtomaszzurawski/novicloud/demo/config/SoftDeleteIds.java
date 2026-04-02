/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;

/**
 * Immutable set of soft-delete test record IDs loaded from a properties file.
 *
 * <p>Used in CRUD_ALL mode: runners read saved IDs to run update/delete cycles.
 * For collecting new IDs during CREATE_SOFT, use {@link SoftDeleteIdsCollector}.
 */
public final class SoftDeleteIds {

    private static final String SUMMARY_SEPARATOR = ", ";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> entries;

    private SoftDeleteIds(Map<String, String> entries) {
        this.entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
    }

    /** Loads entries from an existing properties file. */
    public static SoftDeleteIds load(Path file) throws IOException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            props.load(in);
        }
        Map<String, String> map = new LinkedHashMap<>();
        for (String key : props.stringPropertyNames()) {
            map.put(key, props.getProperty(key));
        }
        return new SoftDeleteIds(map);
    }

    /** Returns the stored ID for the given key, or {@code null} if absent. */
    public String get(String key) {
        return entries.get(key);
    }

    /** Returns a summary of all stored IDs for logging. */
    public String summary() {
        StringJoiner joiner = new StringJoiner(SUMMARY_SEPARATOR);
        entries.forEach((k, v) -> joiner.add(k + KEY_VALUE_SEPARATOR + v));
        return joiner.toString();
    }
}
