/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.service;

import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunReport;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

/**
 * Orchestrates demo runners: logs in real time, catches failures, builds a RunReport.
 *
 * <p>Usage:
 * <pre>{@code
 * DemoSession session = new DemoSession(sdk);
 * RunReport report = session.runAll(runners);
 * report.print();
 * }</pre>
 */
public final class DemoSession {

    private static final Logger LOG = LoggerFactory.getLogger(DemoSession.class);
    private static final String LOG_HEADER = "=== {} ===";
    private static final String LOG_DONE = "[{}] done in {} ms";
    private static final String LOG_FAIL = "[{}] FAIL in {} ms: {}";
    private static final String DETAIL_SEPARATOR = ": ";

    private final NoviCloudClient sdk;

    public DemoSession(NoviCloudClient sdk) {
        this.sdk = sdk;
    }

    /**
     * Runs each runner in order. Logs runner start and end in real time.
     * Catches any exception from a runner and records it as FAIL.
     *
     * @param runners ordered list of runners to execute
     * @return aggregated RunReport (one entry per runner)
     */
    public RunReport runAll(List<EndpointRunner> runners) {
        RunReport report = new RunReport();
        for (EndpointRunner runner : runners) {
            String runnerName = runner.name();
            String header = runnerName.toUpperCase(Locale.ROOT);
            LOG.info(LOG_HEADER, header);
            long start = System.currentTimeMillis();
            try {
                runner.run(sdk);
                long ms = System.currentTimeMillis() - start;
                LOG.info(LOG_DONE, runnerName, ms);
                report.add(RunResult.ok(runnerName));
            } catch (Exception | AssertionError e) {
                long ms = System.currentTimeMillis() - start;
                String detail = e.getClass().getSimpleName() + DETAIL_SEPARATOR + e.getMessage();
                LOG.error(LOG_FAIL, runnerName, ms, detail);
                report.add(RunResult.fail(runnerName, detail));
            }
        }
        return report;
    }
}
