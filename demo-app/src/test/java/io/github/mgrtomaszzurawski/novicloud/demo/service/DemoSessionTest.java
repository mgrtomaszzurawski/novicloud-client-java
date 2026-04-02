/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.service;

import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunReport;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DemoSessionTest {

    private static final String RUNNER_A = "a";
    private static final String RUNNER_B = "b";
    private static final String RUNNER_C = "c";
    private static final String RUNNER_X = "x";
    private static final String RUNNER_Y = "y";
    private static final String ERR_BOOM = "boom";
    private static final String ERR_GENERIC = "err";
    private static final String ERR_1 = "e1";
    private static final String ERR_2 = "e2";
    private static final String EXPECTED_ERROR = "expected error message";
    private static final int EXPECTED_ZERO = 0;
    private static final int EXPECTED_ONE = 1;
    private static final int TWO_RUNNERS = 2;
    private static final int THREE_RUNNERS = 3;
    private static final int FIRST_RESULT = 0;

    private static EndpointRunner ok(String name) {
        return new EndpointRunner() {
            @Override public String name() { return name; }
            @Override public void run(NoviCloudClient sdk) { }
        };
    }

    private static EndpointRunner failing(String name, String error) {
        return new EndpointRunner() {
            @Override public String name() { return name; }
            @Override public void run(NoviCloudClient sdk) {
                throw new RuntimeException(error);
            }
        };
    }

    // DemoSession accepts null client because mock runners don't use it.
    // This is intentional: tests verify DemoSession orchestration logic,
    // not actual API calls.
    private static final DemoSession SESSION = new DemoSession(null);

    @Test
    void runAll_whenNoRunners_returnsEmptyReport() {
        // given
        List<EndpointRunner> runners = List.of();

        // when
        RunReport report = SESSION.runAll(runners);

        // then
        assertEquals(EXPECTED_ZERO, report.okCount());
        assertFalse(report.hasFailures());
    }

    @Test
    void runAll_whenAllSucceed_countsAllAsOk() {
        // given
        List<EndpointRunner> runners = List.of(ok(RUNNER_A), ok(RUNNER_B), ok(RUNNER_C));

        // when
        RunReport report = SESSION.runAll(runners);

        // then
        assertEquals(THREE_RUNNERS, report.okCount());
        assertEquals(EXPECTED_ZERO, report.failCount());
        assertFalse(report.hasFailures());
    }

    @Test
    void runAll_whenOneRunnerFails_countsOneFail() {
        // given
        List<EndpointRunner> runners = List.of(ok(RUNNER_A), failing(RUNNER_B, ERR_BOOM), ok(RUNNER_C));

        // when
        RunReport report = SESSION.runAll(runners);

        // then
        assertEquals(TWO_RUNNERS, report.okCount());
        assertEquals(EXPECTED_ONE, report.failCount());
        assertTrue(report.hasFailures());
    }

    @Test
    void runAll_whenFirstRunnerFails_continuesRemaining() {
        // given
        List<EndpointRunner> runners = List.of(failing(RUNNER_X, ERR_GENERIC), ok(RUNNER_Y));

        // when
        RunReport report = SESSION.runAll(runners);

        // then
        assertEquals(EXPECTED_ONE, report.okCount());
        assertEquals(EXPECTED_ONE, report.failCount());
        assertEquals(TWO_RUNNERS, report.results().size());
    }

    @Test
    void runAll_whenAllRunnersFail_countsAllAsFail() {
        // given
        List<EndpointRunner> runners = List.of(failing(RUNNER_A, ERR_1), failing(RUNNER_B, ERR_2));

        // when
        RunReport report = SESSION.runAll(runners);

        // then
        assertEquals(TWO_RUNNERS, report.failCount());
        assertEquals(EXPECTED_ZERO, report.okCount());
        assertTrue(report.hasFailures());
    }

    @Test
    void runAll_whenRunnerFails_detailContainsExceptionMessage() {
        // given
        List<EndpointRunner> runners = List.of(failing(RUNNER_X, EXPECTED_ERROR));

        // when
        RunReport report = SESSION.runAll(runners);

        // then
        var result = report.results().get(FIRST_RESULT);
        assertNotNull(result.detail());
        assertTrue(result.detail().contains(EXPECTED_ERROR));
    }
}
