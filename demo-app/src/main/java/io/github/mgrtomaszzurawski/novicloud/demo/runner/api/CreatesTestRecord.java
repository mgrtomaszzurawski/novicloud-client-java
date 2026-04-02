/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner.api;

/**
 * Implemented by soft-delete runners that create test records in CREATE_SOFT mode.
 *
 * <p>After {@link EndpointRunner#run} completes, the main app collects created IDs
 * via {@link #createdId()} and saves them to the properties file.
 */
public interface CreatesTestRecord {

    /** Properties file key for this endpoint (e.g. "towary.id", "kartyloj.kod"). */
    String idsKey();

    /** ID of the created test record, or {@code null} if not created. */
    String createdId();
}
