/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.StawkaVat;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat.StawkiVatClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat.StawkaVatQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat.StawkaVatCreateBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.Iterator;

public final class StawkiVatRunner implements EndpointRunner {

    private static final Logger LOG = LoggerFactory.getLogger(StawkiVatRunner.class);
    private static final String ENDPOINT = "stawkivat";
    private final DemoMode mode;

    public StawkiVatRunner(DemoMode mode) {
        this.mode = mode;
    }

    private static final int LIST_ALL_LIMIT = 10;
    private static final String FILTER_ALL_ID = "32766";
    private static final int DEMO_ID = 9999;
    private static final String DEMO_OPIS = "99.99% SDK-TEST";
    private static final String DEMO_ETYKIETA = "G";

    private static final String LOG_LIST_FILTERS = "[{}] list(allFilters) -> {} items";
    private static final String LOG_CUD_CREATE = "[{}] CUD: create(id={}, opis='{}', etykieta='{}') -> returned id={}";
    private static final String LOG_CUD_GET_BY_ID = "[{}] CUD: getById({}) -> id={}, opis='{}', etykieta='{}'";
    private static final String LOG_CUD_OPIS = "[{}] CUD: opis sent='{}', received='{}' (server auto-formats)";
    private static final String LOG_CUD_DELETE = "[{}] CUD: deleteById({}) -> OK";
    private static final String LOG_CUD_PASSED = "[{}] CUD: create-delete cycle PASSED (no update - ADR-022)";
    private static final String LOG_CUD_HARD_DELETE = "[{}] CUD: getById({}) after delete -> null (HARD-DELETE confirmed)";
    private static final String LOG_CUD_SOFT_DELETE = "[{}] CUD: getById({}) after delete -> record present (SOFT-DELETE: record still exists)";
    private static final String LOG_CLEANUP = "[{}] CUD: cleanup - test record id={} exists from previous run";
    private static final String LOG_VERIFY = "[{}] CUD: verify {} = {} (expected {}) -> {}";
    private static final String ERR_VERIFY_FMT = "[%s] %s: expected %s but got %s";
    private static final String VERIFY_OK = "OK";
    private static final String VERIFY_MISMATCH = "MISMATCH";
    private static final String FIELD_ID = "id";
    private static final String FIELD_ETYKIETA = "etykieta";

    @Override
    public String name() { return ENDPOINT; }

    @Override
    public void run(NoviCloudClient client) throws NoviCloudException {
        boolean read = mode != DemoMode.CREATE_SOFT;
        boolean crud = mode == DemoMode.CRUD_SAFE || mode == DemoMode.CRUD_ALL;
        StawkiVatClient api = client.stawkiVat();
        runCount(api, read);
        runGetById(api, read);
        runListAll(api, read);
        runListPageAllFilters(api, read);
        runCreateAndDelete(api, crud);
    }

    private void runCount(StawkiVatClient api, boolean enabled) {
        if (!enabled) { return; }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private void runGetById(StawkiVatClient api, boolean enabled) {
        if (!enabled) { return; }
        PagedResult<StawkaVat> result = api.list(null);
        Iterator<StawkaVat> iterator = result.iterator();
        if (iterator.hasNext()) {
            Integer firstId = iterator.next().id();
            if (firstId != null) {
                api.getById(firstId.longValue());
                logGetById(LOG, ENDPOINT, firstId.longValue());
            }
        }
    }

    private void runListAll(StawkiVatClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<StawkaVat> result = api.list(null);
        Iterator<StawkaVat> iterator = result.iterator();
        while (iterator.hasNext() && iterated < LIST_ALL_LIMIT) { iterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListPageAllFilters(StawkiVatClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(StawkaVatQueryBuilder.builder().id(FILTER_ALL_ID).build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }

    private void runCreateAndDelete(StawkiVatClient api, boolean enabled) {
        if (!enabled) {
            logModeSkip(LOG, ENDPOINT, mode.name());
            return;
        }
        cleanupIfExists(api, (long) DEMO_ID);
        String createdId = api.create(StawkaVatCreateBuilder.builder(DEMO_ID)
                .opis(DEMO_OPIS).etykieta(DEMO_ETYKIETA).build());
        LOG.info(LOG_CUD_CREATE, ENDPOINT, DEMO_ID, DEMO_OPIS, DEMO_ETYKIETA, createdId);
        StawkaVat fetched = api.getById((long) DEMO_ID);
        Integer fetchedId = fetched.id();
        String fetchedOpis = fetched.opis();
        var fetchedEtykieta = fetched.etykieta();
        LOG.info(LOG_CUD_GET_BY_ID, ENDPOINT, DEMO_ID, fetchedId, fetchedOpis, fetchedEtykieta);
        verifyField(ENDPOINT, FIELD_ID, DEMO_ID, fetchedId);
        LOG.info(LOG_CUD_OPIS, ENDPOINT, DEMO_OPIS, fetchedOpis);
        verifyField(ENDPOINT, FIELD_ETYKIETA, DEMO_ETYKIETA,
                fetchedEtykieta != null ? fetchedEtykieta.code() : null);
        api.deleteById((long) DEMO_ID);
        LOG.info(LOG_CUD_DELETE, ENDPOINT, DEMO_ID);
        verifyDeleteBehavior(api, (long) DEMO_ID);
        LOG.info(LOG_CUD_PASSED, ENDPOINT);
    }

    private void cleanupIfExists(StawkiVatClient api, Long id) {
        try {
            api.getById(id);
            LOG.warn(LOG_CLEANUP, ENDPOINT, id);
            api.deleteById(id);
        } catch (NoviCloudNotFoundException expected) {
            // record does not exist - nothing to clean up
        }
    }

    private void verifyDeleteBehavior(StawkiVatClient api, Long id) {
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
