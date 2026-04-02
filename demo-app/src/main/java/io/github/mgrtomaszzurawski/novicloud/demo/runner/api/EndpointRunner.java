/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner.api;

import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;

/**
 * Contract for a per-endpoint integration runner.
 *
 * <p>Each implementation demonstrates client usage for one API resource.
 * Runners call client methods directly (like a service class would), log
 * progress in real time, and throw on unrecoverable errors. Orchestration
 * and result recording are handled by {@link io.github.mgrtomaszzurawski.novicloud.demo.service.DemoSession}.
 */
public interface EndpointRunner {

    /** Short resource name used in RunReport labels (e.g. "towary", "kontrahenci"). */
    String name();

    /**
     * Execute all client operations for this endpoint.
     * Throws on failure; {@code DemoSession} catches it and records FAIL.
     * For intentional skips, log a message and return normally.
     */
    void run(NoviCloudClient client) throws NoviCloudException;
}
