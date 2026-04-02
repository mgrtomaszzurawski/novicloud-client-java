/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Jmiary;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary.JmiaryClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary.JmiaryQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary.JmiaryCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary.JmiaryUpdateBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.Iterator;

public final class JmiaryRunner implements EndpointRunner {

    private static final Logger LOG = LoggerFactory.getLogger(JmiaryRunner.class);
    private static final String ENDPOINT = "jmiary";
    private final DemoMode mode;

    public JmiaryRunner(DemoMode mode) {
        this.mode = mode;
    }

    private static final int LIST_ALL_LIMIT = 3;
    private static final String DEMO_NAZWA = "SDK-TEST Jmiara";
    private static final String DEMO_NAZWA_UPDATED = "SDK-TEST Jmiary Updated";
    private static final int DEMO_PRECYZJA = 2;
    private static final int DEMO_PRECYZJA_UPDATED = 0;
    private static final String FILTER_ALL_STR = "~ZZZZ~";
    private static final String FILTER_ALL_ID = "32766";

    private static final String LOG_LIST_FILTERS = "[{}] list(allFilters) -> {} items";
    private static final String LOG_CUD_CREATE = "[{}] CUD: create(nazwa='{}', precyzja={}) -> id={}";
    private static final String LOG_CUD_GET_BY_ID = "[{}] CUD: getById({}) -> nazwa='{}', precyzja={}";
    private static final String LOG_CUD_UPDATE = "[{}] CUD: update({}, nazwa='{}', precyzja={}) -> OK";
    private static final String LOG_CUD_GET_AFTER_UPDATE = "[{}] CUD: getById({}) after update -> nazwa='{}', precyzja={}";
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
    private static final String FIELD_PRECYZJA = "precyzja";

    @Override
    public String name() { return ENDPOINT; }

    @Override
    public void run(NoviCloudClient client) throws NoviCloudException {
        boolean read = mode != DemoMode.CREATE_SOFT;
        boolean crud = mode == DemoMode.CRUD_SAFE || mode == DemoMode.CRUD_ALL;
        JmiaryClient api = client.jmiary();
        runCount(api, read);
        runGetById(api, read);
        runListAll(api, read);
        runListPageAllFilters(api, read);
        runCreateUpdateDelete(api, crud);
    }

    private void runCount(JmiaryClient api, boolean enabled) {
        if (!enabled) { return; }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private void runGetById(JmiaryClient api, boolean enabled) {
        if (!enabled) { return; }
        PagedResult<Jmiary> result = api.list(null);
        Iterator<Jmiary> iterator = result.iterator();
        if (iterator.hasNext()) {
            Long firstId = iterator.next().id();
            api.getById(firstId);
            logGetById(LOG, ENDPOINT, firstId);
        }
    }

    private void runListAll(JmiaryClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<Jmiary> result = api.list(null);
        Iterator<Jmiary> listAllIterator = result.iterator();
        while (listAllIterator.hasNext() && iterated < LIST_ALL_LIMIT) { listAllIterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListPageAllFilters(JmiaryClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(JmiaryQueryBuilder.builder()
                .fts(FILTER_ALL_STR)
                .id(FILTER_ALL_ID)
                .nazwa(FILTER_ALL_STR)
                .precyzja(FILTER_ALL_ID)
                .build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }

    private void runCreateUpdateDelete(JmiaryClient api, boolean enabled) {
        if (!enabled) {
            logModeSkip(LOG, ENDPOINT, mode.name());
            return;
        }
        // 0. Cleanup leftover from previous failed run
        cleanupByNazwa(api, DEMO_NAZWA);
        cleanupByNazwa(api, DEMO_NAZWA_UPDATED);

        // 1. Create with all fields
        String createdId = api.create(JmiaryCreateBuilder.builder(DEMO_NAZWA)
                .precyzja(DEMO_PRECYZJA).build());
        LOG.info(LOG_CUD_CREATE, ENDPOINT, DEMO_NAZWA, DEMO_PRECYZJA, createdId);
        if (createdId == null) {
            throw new AssertionError(String.format(ERR_NULL_ID_FMT, ENDPOINT));
        }
        long id = Long.parseLong(createdId);

        // 2. Verify via getById
        var fetched = api.getById(id);
        LOG.info(LOG_CUD_GET_BY_ID, ENDPOINT, id, fetched.nazwa(), fetched.precyzja());
        verifyField(ENDPOINT, FIELD_NAZWA, DEMO_NAZWA, fetched.nazwa());
        verifyField(ENDPOINT, FIELD_PRECYZJA, DEMO_PRECYZJA, fetched.precyzja());

        // 3. Update all mutable fields
        api.update(JmiaryUpdateBuilder.builder(id)
                .nazwa(DEMO_NAZWA_UPDATED).precyzja(DEMO_PRECYZJA_UPDATED).build());
        LOG.info(LOG_CUD_UPDATE, ENDPOINT, id, DEMO_NAZWA_UPDATED, DEMO_PRECYZJA_UPDATED);

        // 4. Verify update
        var updated = api.getById(id);
        LOG.info(LOG_CUD_GET_AFTER_UPDATE, ENDPOINT, id, updated.nazwa(), updated.precyzja());
        verifyField(ENDPOINT, FIELD_NAZWA, DEMO_NAZWA_UPDATED, updated.nazwa());
        verifyField(ENDPOINT, FIELD_PRECYZJA, DEMO_PRECYZJA_UPDATED, updated.precyzja());

        // 5. Delete
        api.deleteById(id);
        LOG.info(LOG_CUD_DELETE, ENDPOINT, id);

        // 6. Verify hard-delete
        verifyDeleteBehavior(api, id);
        LOG.info(LOG_CUD_PASSED, ENDPOINT);
    }

    private void cleanupByNazwa(JmiaryClient api, String nazwa) {
        PagedResult<Jmiary> result = api.list(JmiaryQueryBuilder.builder().nazwa(nazwa).build());
        for (var item : result) {
            if (nazwa.equals(item.nazwa())) {
                LOG.warn(LOG_CLEANUP, ENDPOINT, item.id(), nazwa);
                api.deleteById(item.id());
            }
        }
    }

    private void verifyDeleteBehavior(JmiaryClient api, Long id) {
        var response = api.getById(id);
        if (response == null) {
            LOG.info(LOG_CUD_HARD_DELETE, ENDPOINT, id);
        } else {
            LOG.info(LOG_CUD_SOFT_DELETE, ENDPOINT, id);
        }
    }

    private static void verifyField(String endpoint, String field, Object expected, Object actual) {
        boolean ok = String.valueOf(expected).equals(String.valueOf(actual));
        LOG.info(LOG_VERIFY, endpoint, field, actual, expected, ok ? VERIFY_OK : VERIFY_MISMATCH);
        if (!ok) {
            throw new AssertionError(String.format(ERR_VERIFY_FMT, endpoint, field, expected, actual));
        }
    }
}
