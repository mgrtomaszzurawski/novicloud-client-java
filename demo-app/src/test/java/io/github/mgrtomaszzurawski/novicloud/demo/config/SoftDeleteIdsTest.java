/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SoftDeleteIdsTest {

    private static final String KEY_TOWARY = "towary.id";
    private static final String KEY_KARTYLOJ = "kartyloj.kod";
    private static final String KEY_WALUTY = "waluty.id";
    private static final String VAL_TOWARY = "4272";
    private static final String VAL_KARTYLOJ = "SDK-DEMO-LOJ-001";
    private static final String VAL_1 = "1";
    private static final String VAL_2 = "2";
    private static final String SUMMARY_TOWARY_1 = "towary.id=1";
    private static final String SUMMARY_WALUTY_2 = "waluty.id=2";
    private static final String FILE_NAME = "ids.properties";
    private static final String MISSING_FILE = "missing.properties";

    @Test
    void get_whenKeyExists_returnsStoredValue(@TempDir Path tempDir) throws IOException {
        // given
        SoftDeleteIdsCollector collector = new SoftDeleteIdsCollector();
        collector.put(KEY_TOWARY, VAL_TOWARY);
        collector.put(KEY_KARTYLOJ, VAL_KARTYLOJ);
        Path file = tempDir.resolve(FILE_NAME);
        collector.save(file);

        // when
        SoftDeleteIds ids = SoftDeleteIds.load(file);

        // then
        assertEquals(VAL_TOWARY, ids.get(KEY_TOWARY));
        assertEquals(VAL_KARTYLOJ, ids.get(KEY_KARTYLOJ));
        assertNull(ids.get(KEY_WALUTY));
    }

    @Test
    void summary_whenEntriesExist_containsAllKeyValuePairs(@TempDir Path tempDir) throws IOException {
        // given
        SoftDeleteIdsCollector collector = new SoftDeleteIdsCollector();
        collector.put(KEY_TOWARY, VAL_1);
        collector.put(KEY_WALUTY, VAL_2);
        Path file = tempDir.resolve(FILE_NAME);
        collector.save(file);

        // when
        SoftDeleteIds ids = SoftDeleteIds.load(file);
        String summary = ids.summary();

        // then
        assertTrue(summary.contains(SUMMARY_TOWARY_1));
        assertTrue(summary.contains(SUMMARY_WALUTY_2));
    }

    @Test
    void load_whenFileNotFound_throwsIOException(@TempDir Path tempDir) {
        // given
        Path missing = tempDir.resolve(MISSING_FILE);

        // when / then
        assertThrows(IOException.class, () -> SoftDeleteIds.load(missing));
    }
}
