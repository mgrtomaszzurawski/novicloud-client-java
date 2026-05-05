/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kraj;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje.KrajeClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje.KrajQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje.KrajCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje.KrajUpdateBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.Iterator;

public final class KrajeRunner implements EndpointRunner {

    private static final Logger LOG = LoggerFactory.getLogger(KrajeRunner.class);
    private static final String ENDPOINT = "kraje";
    private final DemoMode mode;

    public KrajeRunner(DemoMode mode) {
        this.mode = mode;
    }

    private static final int LIST_ALL_LIMIT = 3;
    private static final String DEMO_NAZWA = "SDK-TEST Kraj";
    private static final String DEMO_KOD = "XZ";
    private static final String DEMO_NAZWA_UPDATED = "SDK-TEST Kraj Updated";
    private static final String DEMO_KOD_UPDATED = "XY";
    private static final String FILTER_ALL_STR = "~ZZZZ~";
    private static final String FILTER_ALL_ID = "999999";

    private static final String LOG_LIST_FILTERS = "[{}] list(allFilters) -> {} items";
    private static final String LOG_CUD_CREATE = "[{}] CUD: create(nazwa='{}', kod='{}') -> id={}";
    private static final String LOG_CUD_GET_BY_ID = "[{}] CUD: getById({}) -> nazwa='{}', kod='{}', waluta='{}'";
    private static final String LOG_CUD_UPDATE = "[{}] CUD: update({}, nazwa='{}', kod='{}') -> OK";
    private static final String LOG_CUD_GET_AFTER_UPDATE = "[{}] CUD: getById({}) after update -> nazwa='{}', kod='{}'";
    private static final String LOG_CUD_DELETE = "[{}] CUD: deleteById({}) -> OK";
    private static final String LOG_CUD_HARD_DELETE = "[{}] CUD: getById({}) after delete -> null (HARD-DELETE confirmed)";
    private static final String LOG_CUD_SOFT_DELETE = "[{}] CUD: getById({}) after delete -> record present (SOFT-DELETE: record still exists)";
    private static final String LOG_CUD_PASSED = "[{}] CUD: create-update-delete cycle PASSED";
    private static final String LOG_CLEANUP = "[{}] CUD: cleanup leftover id={} nazwa='{}'";
    private static final String LOG_VERIFY = "[{}] CUD: verify {} = '{}' (expected '{}') -> {}";
    private static final String ERR_NULL_ID_FMT = "[%s] create returned null id";
    private static final String ERR_VERIFY_FMT = "[%s] %s: expected %s but got %s";
    private static final String VERIFY_OK = "OK";
    private static final String VERIFY_MISMATCH = "MISMATCH";
    private static final String FIELD_NAZWA = "nazwa";
    private static final String FIELD_KOD = "kod";

    @Override
    public String name() { return ENDPOINT; }

    @Override
    public void run(NoviCloudClient client) throws NoviCloudException {
        boolean read = mode != DemoMode.CREATE_SOFT;
        boolean crud = mode == DemoMode.CRUD_SAFE || mode == DemoMode.CRUD_ALL;
        KrajeClient api = client.kraje();
        runCount(api, read);
        runGetById(api, read);
        runListAll(api, read);
        runListPageAllFilters(api, read);
        runCreateUpdateDelete(api, crud);
    }

    private void runCount(KrajeClient api, boolean enabled) {
        if (!enabled) { return; }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private void runGetById(KrajeClient api, boolean enabled) {
        if (!enabled) { return; }
        PagedResult<Kraj> result = api.list(null);
        Iterator<Kraj> iterator = result.iterator();
        if (iterator.hasNext()) {
            Long firstId = iterator.next().id();
            api.getById(firstId);
            logGetById(LOG, ENDPOINT, firstId);
        }
    }

    private void runListAll(KrajeClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<Kraj> result = api.list(null);
        Iterator<Kraj> iterator = result.iterator();
        while (iterator.hasNext() && iterated < LIST_ALL_LIMIT) { iterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListPageAllFilters(KrajeClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(KrajQueryBuilder.builder()
                .fts(FILTER_ALL_STR).id(FILTER_ALL_ID).nazwa(FILTER_ALL_STR)
                .kod(FILTER_ALL_STR).walutaId(FILTER_ALL_ID).build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }

    private void runCreateUpdateDelete(KrajeClient api, boolean enabled) {
        if (!enabled) {
            logModeSkip(LOG, ENDPOINT, mode.name());
            return;
        }
        cleanupByNazwa(api, DEMO_NAZWA);
        cleanupByNazwa(api, DEMO_NAZWA_UPDATED);
        String createdId = api.create(KrajCreateBuilder.builder(DEMO_NAZWA, DEMO_KOD).build());
        LOG.info(LOG_CUD_CREATE, ENDPOINT, DEMO_NAZWA, DEMO_KOD, createdId);
        if (createdId == null) { throw new AssertionError(String.format(ERR_NULL_ID_FMT, ENDPOINT)); }
        long id = Long.parseLong(createdId);
        var fetched = api.getById(id);
        String fetchedNazwa = fetched.nazwa();
        String fetchedKod = fetched.kod();
        String fetchedWalutaId = fetched.walutaId();
        LOG.info(LOG_CUD_GET_BY_ID, ENDPOINT, id, fetchedNazwa, fetchedKod, fetchedWalutaId);
        verifyField(ENDPOINT, FIELD_NAZWA, DEMO_NAZWA, fetchedNazwa);
        verifyField(ENDPOINT, FIELD_KOD, DEMO_KOD, fetchedKod);
        api.update(KrajUpdateBuilder.builder(id).nazwa(DEMO_NAZWA_UPDATED).kod(DEMO_KOD_UPDATED).build());
        LOG.info(LOG_CUD_UPDATE, ENDPOINT, id, DEMO_NAZWA_UPDATED, DEMO_KOD_UPDATED);
        var updated = api.getById(id);
        String updatedNazwa = updated.nazwa();
        String updatedKod = updated.kod();
        LOG.info(LOG_CUD_GET_AFTER_UPDATE, ENDPOINT, id, updatedNazwa, updatedKod);
        verifyField(ENDPOINT, FIELD_NAZWA, DEMO_NAZWA_UPDATED, updatedNazwa);
        verifyField(ENDPOINT, FIELD_KOD, DEMO_KOD_UPDATED, updatedKod);
        api.deleteById(id);
        LOG.info(LOG_CUD_DELETE, ENDPOINT, id);
        verifyDeleteBehavior(api, id);
        LOG.info(LOG_CUD_PASSED, ENDPOINT);
    }

    private void cleanupByNazwa(KrajeClient api, String nazwa) {
        PagedResult<Kraj> result = api.list(KrajQueryBuilder.builder().nazwa(nazwa).build());
        for (var item : result) {
            if (nazwa.equals(item.nazwa())) {
                LOG.warn(LOG_CLEANUP, ENDPOINT, item.id(), nazwa);
                api.deleteById(item.id());
            }
        }
    }

    private void verifyDeleteBehavior(KrajeClient api, Long id) {
        try {
            api.getById(id);
            LOG.info(LOG_CUD_SOFT_DELETE, ENDPOINT, id);
        } catch (NoviCloudNotFoundException expected) {
            LOG.info(LOG_CUD_HARD_DELETE, ENDPOINT, id);
        }
    }

    private static void verifyField(String endpoint, String field, Object expected, Object actual) {
        boolean ok = String.valueOf(expected).equals(String.valueOf(actual));
        LOG.info(LOG_VERIFY, endpoint, field, actual, expected, ok ? VERIFY_OK : VERIFY_MISMATCH);
        if (!ok) { throw new AssertionError(String.format(ERR_VERIFY_FMT, endpoint, field, expected, actual)); }
    }
}
