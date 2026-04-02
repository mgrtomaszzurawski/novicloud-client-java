/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Dokument;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.dokumenty.DokumentyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.dokumenty.DokumentQueryBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.Iterator;

public final class DokumentyRunner implements EndpointRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DokumentyRunner.class);
    private static final String ENDPOINT = "dokumenty";
    private final DemoMode mode;

    public DokumentyRunner(DemoMode mode) {
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
        DokumentyClient api = client.dokumenty();
        runCount(api, read);
        runGetById(api, read);
        runListAll(api, read);
        //runProbeFilters(api); // ADR-031: probe testing done
        runListPageAllFilters(api, read);
    }

    private void runCount(DokumentyClient api, boolean enabled) {
        if (!enabled) {
            logModeSkip(LOG, ENDPOINT, mode.name());
            return;
        }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private void runGetById(DokumentyClient api, boolean enabled) {
        if (!enabled) { return; }
        PagedResult<Dokument> result = api.list(null);
        Iterator<Dokument> iterator = result.iterator();
        if (iterator.hasNext()) {
            Long firstId = iterator.next().id();
            api.getById(firstId);
            logGetById(LOG, ENDPOINT, firstId);
        }
    }

    private void runListAll(DokumentyClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<Dokument> result = api.list(null);
        Iterator<Dokument> listAllIterator = result.iterator();
        while (listAllIterator.hasNext() && iterated < LIST_ALL_LIMIT) { listAllIterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListPageAllFilters(DokumentyClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(DokumentQueryBuilder.builder()
                .id(FILTER_ALL_ID)
                .typDok(FILTER_ALL_ID)
                .dataWystawienia(FILTER_ALL_DATE)
                .dataWplywu(FILTER_ALL_DATE)
                .dataWykonania(FILTER_ALL_DATE)
                .nrDok(FILTER_ALL_STR)
                .sklepId(FILTER_ALL_ID)
                // ADR-031: .sklepOdbId(FILTER_ALL_ID) - par_niewlasciwe
                .kontrahentId(FILTER_ALL_ID)
                .platnikId(FILTER_ALL_ID)
                .kasaId(FILTER_ALL_ID)
                .kasjerId(FILTER_ALL_ID)
                .storno(false)
                .build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }
}
