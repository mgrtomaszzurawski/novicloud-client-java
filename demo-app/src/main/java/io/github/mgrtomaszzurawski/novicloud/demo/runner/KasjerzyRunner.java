/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kasjer;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasjerzy.KasjerzyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasjerzy.KasjerQueryBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.Iterator;

public final class KasjerzyRunner implements EndpointRunner {

    private static final Logger LOG = LoggerFactory.getLogger(KasjerzyRunner.class);
    private static final String ENDPOINT = "kasjerzy";
    private final DemoMode mode;

    public KasjerzyRunner(DemoMode mode) {
        this.mode = mode;
    }

    private static final int LIST_ALL_LIMIT = 3;
    private static final String FILTER_ALL_STR = "~ZZZZ~";
    private static final String FILTER_ALL_ID = "999999";
    private static final String LOG_LIST_FILTERS = "[{}] list(allFilters) -> {} items";

    @Override
    public String name() { return ENDPOINT; }

    @Override
    public void run(NoviCloudClient client) throws NoviCloudException {
        boolean read = mode != DemoMode.CREATE_SOFT;
        KasjerzyClient api = client.kasjerzy();
        runCount(api, read);
        runGetById(api, read);
        runListAll(api, read);
        runListPageAllFilters(api, read);
    }

    private void runCount(KasjerzyClient api, boolean enabled) {
        if (!enabled) {
            logModeSkip(LOG, ENDPOINT, mode.name());
            return;
        }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private void runGetById(KasjerzyClient api, boolean enabled) {
        if (!enabled) { return; }
        PagedResult<Kasjer> result = api.list(null);
        Iterator<Kasjer> iterator = result.iterator();
        if (iterator.hasNext()) {
            Long firstId = iterator.next().id();
            api.getById(firstId);
            logGetById(LOG, ENDPOINT, firstId);
        }
    }

    private void runListAll(KasjerzyClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<Kasjer> result = api.list(null);
        Iterator<Kasjer> listAllIterator = result.iterator();
        while (listAllIterator.hasNext() && iterated < LIST_ALL_LIMIT) { listAllIterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListPageAllFilters(KasjerzyClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(KasjerQueryBuilder.builder()
                .id(FILTER_ALL_ID)
                .nazwisko(FILTER_ALL_STR)
                .kodKasjera(FILTER_ALL_STR)
                .aktywny(false)
                .build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }
}
