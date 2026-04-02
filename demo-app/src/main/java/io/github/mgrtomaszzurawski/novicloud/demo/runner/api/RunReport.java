/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class RunReport {

    private static final Logger LOG = LoggerFactory.getLogger(RunReport.class);
    private static final String LOG_RESULT = "{}";
    private static final String LOG_SUMMARY = "--- Summary: {} OK, {} FAIL, {} SKIP ---";
    private static final int ZERO_FAILURES = 0;

    private final List<RunResult> results = new ArrayList<>();

    public void add(RunResult result) {
        results.add(result);
    }

    public List<RunResult> results() {
        return List.copyOf(results);
    }

    public int okCount() {
        return (int) results.stream().filter(result -> result.status() == RunResult.Status.OK).count();
    }

    public int failCount() {
        return (int) results.stream().filter(result -> result.status() == RunResult.Status.FAIL).count();
    }

    public int skipCount() {
        return (int) results.stream().filter(result -> result.status() == RunResult.Status.SKIP).count();
    }

    public boolean hasFailures() {
        return failCount() > ZERO_FAILURES;
    }

    public void print() {
        results.forEach(result -> {
            if (result.status() == RunResult.Status.FAIL) {
                LOG.error(LOG_RESULT, result);
            } else {
                LOG.info(LOG_RESULT, result);
            }
        });
        LOG.info(LOG_SUMMARY, okCount(), failCount(), skipCount());
    }
}
