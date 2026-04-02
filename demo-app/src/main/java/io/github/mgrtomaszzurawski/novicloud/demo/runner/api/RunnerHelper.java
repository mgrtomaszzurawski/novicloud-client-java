/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner.api;

import org.slf4j.Logger;

public final class RunnerHelper {

    public static final int ITERATION_START = 0;
    public static final int FIRST_INDEX = 0;

    private static final String LOG_COUNT = "[{}] count -> {}";
    private static final String LOG_GET_BY_ID = "[{}] getById({}) -> OK";
    private static final String LOG_CREATE = "[{}] create -> id={}";
    private static final String LOG_UPDATE = "[{}] update({}) -> OK";
    private static final String LOG_DELETE = "[{}] delete({}) -> OK";
    private static final String LOG_LIST_ALL = "[{}] listAll -> iterated {} items";
    private static final String LOG_SKIPPED = "[{}] {} -> skipped ({})";
    private static final String LOG_MODE_SKIP = "[{}] [SKIP] mode={}";

    private RunnerHelper() {}

    public static void logCount(Logger log, String endpoint, int count) {
        log.info(LOG_COUNT, endpoint, count);
    }

    public static void logGetById(Logger log, String endpoint, long id) {
        log.info(LOG_GET_BY_ID, endpoint, id);
    }

    public static void logCreate(Logger log, String endpoint, String createdId) {
        log.info(LOG_CREATE, endpoint, createdId);
    }

    public static void logUpdate(Logger log, String endpoint, long id) {
        log.info(LOG_UPDATE, endpoint, id);
    }

    public static void logDelete(Logger log, String endpoint, long id) {
        log.info(LOG_DELETE, endpoint, id);
    }

    public static void logListAll(Logger log, String endpoint, int iterated) {
        log.info(LOG_LIST_ALL, endpoint, iterated);
    }

    public static void logSkipped(Logger log, String endpoint, String operation, String reason) {
        log.info(LOG_SKIPPED, endpoint, operation, reason);
    }

    public static void logModeSkip(Logger log, String endpoint, String mode) {
        log.info(LOG_MODE_SKIP, endpoint, mode);
    }
}
