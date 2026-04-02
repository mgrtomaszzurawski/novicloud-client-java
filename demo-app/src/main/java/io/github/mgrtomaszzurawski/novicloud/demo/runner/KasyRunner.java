/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kasa;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasy.KasyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasy.KasaQueryBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.Iterator;

public final class KasyRunner implements EndpointRunner {

    private static final Logger LOG = LoggerFactory.getLogger(KasyRunner.class);
    private static final String ENDPOINT = "kasy";
    private final DemoMode mode;

    public KasyRunner(DemoMode mode) {
        this.mode = mode;
    }

    private static final int LIST_ALL_LIMIT = 3;
    private static final String FILTER_ALL_STR = "~ZZZZ~";
    private static final String FILTER_ALL_ID = "32766";
    private static final String FILTER_ALL_DATE = "2099-12-31";
    private static final String LOG_LIST_FILTERS = "[{}] list(allFilters) -> {} items";

    @Override
    public String name() { return ENDPOINT; }

    @Override
    public void run(NoviCloudClient client) throws NoviCloudException {
        boolean read = mode != DemoMode.CREATE_SOFT;
        KasyClient api = client.kasy();
        runCount(api, read);
        runGetById(api, read);
        runListAll(api, read);
        //runProbeFilters(api); // ADR-031: probe testing done
        runListPageAllFilters(api, read);
    }

    private void runCount(KasyClient api, boolean enabled) {
        if (!enabled) {
            logModeSkip(LOG, ENDPOINT, mode.name());
            return;
        }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private void runGetById(KasyClient api, boolean enabled) {
        if (!enabled) { return; }
        PagedResult<Kasa> result = api.list(null);
        Iterator<Kasa> iterator = result.iterator();
        if (iterator.hasNext()) {
            Long firstId = iterator.next().id();
            api.getById(firstId);
            logGetById(LOG, ENDPOINT, firstId);
        }
    }

    private void runListAll(KasyClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<Kasa> result = api.list(null);
        Iterator<Kasa> listAllIterator = result.iterator();
        while (listAllIterator.hasNext() && iterated < LIST_ALL_LIMIT) { listAllIterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListPageAllFilters(KasyClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(KasaQueryBuilder.builder()
                .id(FILTER_ALL_ID)
                .nazwa(FILTER_ALL_STR)
                .numer(FILTER_ALL_ID)
                // ADR-031: .ecr(FILTER_ALL_STR) - par_niewlasciwe
                .ostatniaSync(FILTER_ALL_DATE)
                .aktywny(false)
                .build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }
}
