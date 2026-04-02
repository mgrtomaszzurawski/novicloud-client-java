/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunResult;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunReport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RunReportTest {

    private static final String STEP = "step-1";
    private static final String STEP_A = "step-a";
    private static final String STEP_B = "step-b";
    private static final String STEP_C = "step-c";
    private static final String STEP_D = "step-d";
    private static final String STEP_EXTRA = "step-extra";
    private static final String REASON = "reason";
    private static final String FAIL_MSG = "something went wrong";
    private static final int EXPECTED_ZERO = 0;
    private static final int EXPECTED_ONE = 1;
    private static final int MIXED_OK_COUNT = 2;
    private static final int MIXED_TOTAL = 4;

    @Test
    void runAll_whenNoRunners_returnsEmptyReport() {
        // given / when
        RunReport report = new RunReport();

        // then
        assertEquals(EXPECTED_ZERO, report.okCount());
        assertEquals(EXPECTED_ZERO, report.failCount());
        assertEquals(EXPECTED_ZERO, report.skipCount());
        assertFalse(report.hasFailures());
        assertTrue(report.results().isEmpty());
    }

    @Test
    void add_whenOkResult_incrementsOkCount() {
        // given
        RunReport report = new RunReport();

        // when
        report.add(RunResult.ok(STEP));

        // then
        assertEquals(EXPECTED_ONE, report.okCount());
        assertEquals(EXPECTED_ZERO, report.failCount());
        assertFalse(report.hasFailures());
    }

    @Test
    void add_whenFailResult_incrementsFailCount() {
        // given
        RunReport report = new RunReport();

        // when
        report.add(RunResult.fail(STEP, FAIL_MSG));

        // then
        assertEquals(EXPECTED_ZERO, report.okCount());
        assertEquals(EXPECTED_ONE, report.failCount());
        assertTrue(report.hasFailures());
    }

    @Test
    void add_whenSkipResult_incrementsSkipCount() {
        // given
        RunReport report = new RunReport();

        // when
        report.add(RunResult.skip(STEP, REASON));

        // then
        assertEquals(EXPECTED_ONE, report.skipCount());
        assertFalse(report.hasFailures());
    }

    @Test
    void add_whenMixedResults_countsEachStatusCorrectly() {
        // given
        RunReport report = new RunReport();

        // when
        report.add(RunResult.ok(STEP_A));
        report.add(RunResult.ok(STEP_B));
        report.add(RunResult.fail(STEP_C, FAIL_MSG));
        report.add(RunResult.skip(STEP_D, REASON));

        // then
        assertEquals(MIXED_OK_COUNT, report.okCount());
        assertEquals(EXPECTED_ONE, report.failCount());
        assertEquals(EXPECTED_ONE, report.skipCount());
        assertEquals(MIXED_TOTAL, report.results().size());
        assertTrue(report.hasFailures());
    }

    @Test
    void results_whenModified_throwsUnsupportedOperation() {
        // given
        RunReport report = new RunReport();
        report.add(RunResult.ok(STEP));
        var copy = report.results();
        RunResult extra = RunResult.ok(STEP_EXTRA);

        // when / then
        assertThrows(UnsupportedOperationException.class, () -> copy.add(extra));
    }
}
