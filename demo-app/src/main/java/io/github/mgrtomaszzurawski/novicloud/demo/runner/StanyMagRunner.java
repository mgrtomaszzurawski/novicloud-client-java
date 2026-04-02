/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.StanMag;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stanymag.StanyMagClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stanymag.StanMagQueryBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class StanyMagRunner implements EndpointRunner {

    private static final Logger LOG = LoggerFactory.getLogger(StanyMagRunner.class);
    private static final String ENDPOINT = "stanymag";
    private final DemoMode mode;

    public StanyMagRunner(DemoMode mode) {
        this.mode = mode;
    }

    private static final int LIST_ALL_LIMIT = 3;
    private static final String OP_LIST_BY_TOWAR = "listByTowar";
    private static final String OP_GET_BY_TOWAR_SKLEP = "getByTowarAndSklep";
    private static final String SKIP_NO_TOWAR_ID = "no towar id from list";
    private static final String SKIP_NO_TOWAR_SKLEP_PAIR = "no towar/sklep id pair";
    private static final String FILTER_ALL_ID = "999999";
    private static final String FILTER_ALL_DATE = "2099-12-31";

    private static final String LOG_LIST_SAMPLE = "[{}] listSample -> {} items";
    private static final String LOG_LIST_BY_TOWAR = "[{}] listByTowar({}) -> OK";
    private static final String LOG_GET_BY_TOWAR_SKLEP = "[{}] getByTowarAndSklep({}, {}) -> OK";
    private static final String LOG_LIST_FILTERS = "[{}] list(allFilters) -> {} items";
    private static final String LOG_NON_NUMERIC_ID = "Non-numeric ID value: {}";

    @Override
    public String name() { return ENDPOINT; }

    @Override
    public void run(NoviCloudClient client) throws NoviCloudException {
        boolean read = mode != DemoMode.CREATE_SOFT;
        StanyMagClient api = client.stanyMag();
        runCount(api, read);
        List<StanMag> items = runListSample(api, read);
        runListAll(api, read);
        Long firstTowarId = extractTowarId(items);
        Long firstSklepId = extractSklepId(items);
        runListByTowar(api, firstTowarId, read);
        runGetByTowarAndSklep(api, firstTowarId, firstSklepId, read);
        runListPageAllFilters(api, read);
    }

    private void runCount(StanyMagClient api, boolean enabled) {
        if (!enabled) {
            logModeSkip(LOG, ENDPOINT, mode.name());
            return;
        }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private List<StanMag> runListSample(StanyMagClient api, boolean enabled) {
        if (!enabled) { return List.of(); }
        List<StanMag> items = new ArrayList<>();
        PagedResult<StanMag> result = api.list(null);
        Iterator<StanMag> iterator = result.iterator();
        while (iterator.hasNext() && items.size() < LIST_ALL_LIMIT) {
            items.add(iterator.next());
        }
        LOG.info(LOG_LIST_SAMPLE, ENDPOINT, items.size());
        return items;
    }

    private void runListAll(StanyMagClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<StanMag> result = api.list(null);
        Iterator<StanMag> iterator = result.iterator();
        while (iterator.hasNext() && iterated < LIST_ALL_LIMIT) { iterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListByTowar(StanyMagClient api, Long firstTowarId, boolean enabled) {
        if (!enabled) { return; }
        if (firstTowarId != null) {
            api.listByTowar(firstTowarId, null);
            LOG.info(LOG_LIST_BY_TOWAR, ENDPOINT, firstTowarId);
        } else {
            logSkipped(LOG, ENDPOINT, OP_LIST_BY_TOWAR, SKIP_NO_TOWAR_ID);
        }
    }

    private void runGetByTowarAndSklep(StanyMagClient api, Long firstTowarId, Long firstSklepId, boolean enabled) {
        if (!enabled) { return; }
        if (firstTowarId != null && firstSklepId != null) {
            api.getByTowarAndSklep(firstTowarId, firstSklepId, null);
            LOG.info(LOG_GET_BY_TOWAR_SKLEP, ENDPOINT, firstTowarId, firstSklepId);
        } else {
            logSkipped(LOG, ENDPOINT, OP_GET_BY_TOWAR_SKLEP, SKIP_NO_TOWAR_SKLEP_PAIR);
        }
    }

    private void runListPageAllFilters(StanyMagClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(StanMagQueryBuilder.builder()
                .towarId(FILTER_ALL_ID).sklepId(FILTER_ALL_ID).naDzien(FILTER_ALL_DATE)
                .build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }

    private Long extractTowarId(List<StanMag> items) {
        if (items == null || items.isEmpty()) { return null; }
        return parseLongOrNull(items.get(FIRST_INDEX).towarId());
    }

    private Long extractSklepId(List<StanMag> items) {
        if (items == null || items.isEmpty()) { return null; }
        return parseLongOrNull(items.get(FIRST_INDEX).sklepId());
    }

    private static Long parseLongOrNull(String value) {
        if (value == null) { return null; }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            LOG.debug(LOG_NON_NUMERIC_ID, value);
            return null;
        }
    }
}
