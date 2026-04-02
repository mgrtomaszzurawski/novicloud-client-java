/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunResult;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RunResultTest {

    private static final String LABEL = "my-label";
    private static final String DETAIL_FAIL = "status=401 unauthorized";
    private static final String DETAIL_SKIP = "no data available";
    private static final String STATUS_OK = "[OK  ]";
    private static final String STATUS_FAIL = "[FAIL]";
    private static final String STATUS_SKIP = "[SKIP]";

    @Test
    void ok_whenCreated_hasStatusOkAndLabel() {
        // given / when
        RunResult r = RunResult.ok(LABEL);

        // then
        assertEquals(RunResult.Status.OK, r.status());
        assertEquals(LABEL, r.label());
        assertNull(r.detail());
    }

    @Test
    void fail_whenCreated_hasStatusFailAndDetail() {
        // given / when
        RunResult r = RunResult.fail(LABEL, DETAIL_FAIL);

        // then
        assertEquals(RunResult.Status.FAIL, r.status());
        assertEquals(LABEL, r.label());
        assertEquals(DETAIL_FAIL, r.detail());
    }

    @Test
    void skip_whenCreated_hasStatusSkipAndDetail() {
        // given / when
        RunResult r = RunResult.skip(LABEL, DETAIL_SKIP);

        // then
        assertEquals(RunResult.Status.SKIP, r.status());
        assertEquals(LABEL, r.label());
        assertEquals(DETAIL_SKIP, r.detail());
    }

    @Test
    void toString_whenCalled_containsStatusTagAndLabel() {
        // given
        RunResult ok = RunResult.ok(LABEL);
        RunResult fail = RunResult.fail(LABEL, DETAIL_FAIL);
        RunResult skip = RunResult.skip(LABEL, DETAIL_SKIP);

        // when / then
        assertTrue(ok.toString().contains(STATUS_OK));
        assertTrue(ok.toString().contains(LABEL));
        assertTrue(fail.toString().contains(STATUS_FAIL));
        assertTrue(fail.toString().contains(DETAIL_FAIL));
        assertTrue(skip.toString().contains(STATUS_SKIP));
    }
}
