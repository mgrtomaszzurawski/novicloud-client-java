/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.config;

/**
 * Run mode for the demo application.
 *
 * <p>Controls which operations each runner executes. Set via
 * {@code demo.mode} in {@code application.properties}.
 *
 * @see AppProperties#demoMode()
 */
public enum DemoMode {

    /** All runners execute GET operations only. CRUD operations logged as [SKIP]. */
    READ_ONLY,

    /** Hard-delete endpoints run full CRUD cycle. Soft-delete endpoints GET only. */
    CRUD_SAFE,

    /** Creates one test record per soft-delete endpoint, saves IDs to file. */
    CREATE_SOFT,

    /** Full CRUD on all writable endpoints. Soft-delete uses saved IDs from CREATE_SOFT. */
    CRUD_ALL
}
