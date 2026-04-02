/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.PozycjaDokumentu;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.pozdok.PozdokClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.pozdok.PozdokQueryBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.Iterator;

public final class PozdokRunner implements EndpointRunner {

    private static final Logger LOG = LoggerFactory.getLogger(PozdokRunner.class);
    private static final String ENDPOINT = "pozdok";
    private final DemoMode mode;

    public PozdokRunner(DemoMode mode) {
        this.mode = mode;
    }

    private static final int LIST_ALL_LIMIT = 3;
    private static final String FILTER_ALL_STR = "~ZZZZ~";
    private static final String FILTER_ALL_ID = "32766";
    private static final String LOG_LIST_FILTERS = "[{}] list(allFilters) -> {} items";

    @Override
    public String name() { return ENDPOINT; }

    @Override
    public void run(NoviCloudClient client) throws NoviCloudException {
        boolean read = mode != DemoMode.CREATE_SOFT;
        PozdokClient api = client.pozdok();
        runCount(api, read);
        runGetById(api, read);
        runListAll(api, read);
        //runProbeFilters(api); // ADR-031: probe testing done
        runListPageAllFilters(api, read);
    }

    private void runCount(PozdokClient api, boolean enabled) {
        if (!enabled) {
            logModeSkip(LOG, ENDPOINT, mode.name());
            return;
        }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private void runGetById(PozdokClient api, boolean enabled) {
        if (!enabled) { return; }
        PagedResult<PozycjaDokumentu> result = api.list(null);
        Iterator<PozycjaDokumentu> iterator = result.iterator();
        if (iterator.hasNext()) {
            Long firstId = iterator.next().id();
            api.getById(firstId);
            logGetById(LOG, ENDPOINT, firstId);
        }
    }

    private void runListAll(PozdokClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<PozycjaDokumentu> result = api.list(null);
        Iterator<PozycjaDokumentu> iterator = result.iterator();
        while (iterator.hasNext() && iterated < LIST_ALL_LIMIT) { iterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListPageAllFilters(PozdokClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(PozdokQueryBuilder.builder()
                .dokumentId(FILTER_ALL_ID)
                .dokumentNrDok(FILTER_ALL_STR)
                .dokumentKontrahentId(FILTER_ALL_ID)
                .dokumentPlatnikId(FILTER_ALL_ID)
                .dokumentSklepId(FILTER_ALL_ID)
                .dokumentKasaId(FILTER_ALL_ID)
                .dokumentKasjerId(FILTER_ALL_ID)
                .towarId(FILTER_ALL_ID)
                .nrPozycji(FILTER_ALL_ID)
                .build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }
}
