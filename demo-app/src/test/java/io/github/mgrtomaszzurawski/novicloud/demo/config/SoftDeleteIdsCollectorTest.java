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

class SoftDeleteIdsCollectorTest {

    private static final String KEY_TOWARY = "towary.id";
    private static final String KEY_WALUTY = "waluty.id";
    private static final String KEY_KARTYLOJ = "kartyloj.kod";
    private static final String VAL_TOWARY = "4272";
    private static final String VAL_WALUTY = "3";
    private static final String VAL_KARTYLOJ = "SDK-DEMO-LOJ-001";
    private static final String VAL_ANY = "123";
    private static final String VAL_1 = "1";
    private static final String VAL_2 = "2";
    private static final String SUMMARY_TOWARY_1 = "towary.id=1";
    private static final String SUMMARY_WALUTY_2 = "waluty.id=2";
    private static final String EMPTY = "";
    private static final String FILE_NAME = "test-ids.properties";

    @Test
    void isEmpty_whenNewCollector_returnsTrue() {
        // given
        SoftDeleteIdsCollector collector = new SoftDeleteIdsCollector();

        // when / then
        assertTrue(collector.isEmpty());
        assertEquals(EMPTY, collector.summary());
    }

    @Test
    void isEmpty_whenEntryAdded_returnsFalse() {
        // given
        SoftDeleteIdsCollector collector = new SoftDeleteIdsCollector();

        // when
        collector.put(KEY_TOWARY, VAL_ANY);

        // then
        assertFalse(collector.isEmpty());
    }

    @Test
    void summary_whenMultipleEntries_containsAllKeyValuePairs() {
        // given
        SoftDeleteIdsCollector collector = new SoftDeleteIdsCollector();
        collector.put(KEY_TOWARY, VAL_1);
        collector.put(KEY_WALUTY, VAL_2);

        // when
        String summary = collector.summary();

        // then
        assertTrue(summary.contains(SUMMARY_TOWARY_1));
        assertTrue(summary.contains(SUMMARY_WALUTY_2));
    }

    @Test
    void save_whenCalled_createsFile(@TempDir Path tempDir) throws IOException {
        // given
        SoftDeleteIdsCollector collector = new SoftDeleteIdsCollector();
        collector.put(KEY_TOWARY, VAL_TOWARY);
        Path file = tempDir.resolve(FILE_NAME);

        // when
        collector.save(file);

        // then
        assertTrue(file.toFile().exists());
    }

    @Test
    void save_whenLoadedBack_preservesAllEntries(@TempDir Path tempDir) throws IOException {
        // given
        SoftDeleteIdsCollector collector = new SoftDeleteIdsCollector();
        collector.put(KEY_TOWARY, VAL_TOWARY);
        collector.put(KEY_WALUTY, VAL_WALUTY);
        collector.put(KEY_KARTYLOJ, VAL_KARTYLOJ);
        Path file = tempDir.resolve(FILE_NAME);
        collector.save(file);

        // when
        SoftDeleteIds loaded = SoftDeleteIds.load(file);

        // then
        assertEquals(VAL_TOWARY, loaded.get(KEY_TOWARY));
        assertEquals(VAL_WALUTY, loaded.get(KEY_WALUTY));
        assertEquals(VAL_KARTYLOJ, loaded.get(KEY_KARTYLOJ));
    }
}
