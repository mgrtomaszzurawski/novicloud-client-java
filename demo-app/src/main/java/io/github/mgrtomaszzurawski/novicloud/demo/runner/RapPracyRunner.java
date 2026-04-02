/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.RaportPracy;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy.RapPracyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy.RapPracyQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy.RapPracyGroup;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.Iterator;

public final class RapPracyRunner implements EndpointRunner {

    private static final Logger LOG = LoggerFactory.getLogger(RapPracyRunner.class);
    private static final String ENDPOINT = "rappracy";
    private final DemoMode mode;

    public RapPracyRunner(DemoMode mode) {
        this.mode = mode;
    }

    private static final int LIST_ALL_LIMIT = 3;
    private static final String FILTER_ALL_ID = "32766";
    private static final String FILTER_ALL_DATE = "2099-12-31";
    private static final String LOG_LIST_FILTERS = "[{}] list(allFilters) -> {} items";

    @Override
    public String name() { return ENDPOINT; }

    @Override
    public void run(NoviCloudClient client) throws NoviCloudException {
        boolean read = mode != DemoMode.CREATE_SOFT;
        RapPracyClient api = client.rapPracy();
        runCount(api, read);
        runListAll(api, read);
        //runProbeFilters(api); // ADR-031: probe testing done
        runListPageAllFilters(api, read);
    }

    private void runCount(RapPracyClient api, boolean enabled) {
        if (!enabled) {
            logModeSkip(LOG, ENDPOINT, mode.name());
            return;
        }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private void runListAll(RapPracyClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<RaportPracy> result = api.list(null);
        Iterator<RaportPracy> iterator = result.iterator();
        while (iterator.hasNext() && iterated < LIST_ALL_LIMIT) { iterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListPageAllFilters(RapPracyClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(RapPracyQueryBuilder.builder()
                .dataPocz(FILTER_ALL_DATE).dataKonc(FILTER_ALL_DATE)
                .grupowanie(RapPracyGroup.SKLEP)
                .sklepId(FILTER_ALL_ID).kasaId(FILTER_ALL_ID).kasjerId(FILTER_ALL_ID)
                .build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }
}
