/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.demo.config.SoftDeleteIds;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.CreatesTestRecord;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Waluta;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty.WalutyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty.WalutaQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty.WalutaCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty.WalutaUpdateBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.Iterator;

public final class WalutyRunner implements EndpointRunner, CreatesTestRecord {

    private static final Logger LOG = LoggerFactory.getLogger(WalutyRunner.class);
    private static final String ENDPOINT = "waluty";
    private static final String IDS_KEY = "waluty.id";
    private final DemoMode mode;
    private final SoftDeleteIds ids;
    private String createdId;

    public WalutyRunner(DemoMode mode, SoftDeleteIds ids) {
        this.mode = mode;
        this.ids = ids;
    }

    @Override public String idsKey() { return IDS_KEY; }
    @Override public String createdId() { return createdId; }

    private static final String DEMO_KOD = "USD";
    private static final String DEMO_NAZWA = "SDK Demo Waluta";
    private static final String DEMO_NAZWA_UPDATED = DEMO_NAZWA + " Updated";
    private static final int LIST_ALL_LIMIT = 3;
    private static final String FILTER_ALL_STR = "~ZZZZ~";
    private static final String FILTER_ALL_ID = "32766";

    private static final String LOG_LIST_FILTERS = "[{}] list(allFilters) -> {} items";
    private static final String LOG_NO_SAVED_ID = "[{}] No saved ID in demo-soft-delete-ids.properties, skipping";
    private static final String LOG_UD_GET = "[{}] UD: getById({}) -> nazwa='{}', kod='{}', aktywny={}";
    private static final String LOG_UD_REACTIVATED = "[{}] UD: reactivated id={}";
    private static final String LOG_UD_UPDATE = "[{}] UD: update({}, nazwa='{}') -> OK";
    private static final String LOG_UD_DELETE = "[{}] UD: deleteById({}) -> OK";
    private static final String LOG_UD_SOFT_DELETE_OK = "[{}] UD: soft-delete confirmed (aktywny=false)";
    private static final String LOG_UD_RESTORED = "[{}] UD: restored original state id={}";
    private static final String LOG_UD_PASSED = "[{}] UD: update-delete cycle PASSED (soft-delete endpoint)";
    private static final String LOG_CREATE_ONCE = "[{}] CREATE-ONCE: create(nazwa='{}', kod='{}') -> id={}";
    private static final String LOG_VERIFY = "[{}] UD: verify {} = '{}' (expected '{}') -> {}";
    private static final String ERR_VERIFY_FMT = "[%s] %s: expected %s but got %s";
    private static final String VERIFY_OK = "OK";
    private static final String VERIFY_MISMATCH = "MISMATCH";
    private static final String FIELD_NAZWA = "nazwa";
    private static final String FIELD_AKTYWNY = "aktywny";

    @Override
    public String name() { return ENDPOINT; }

    @Override
    public void run(NoviCloudClient client) throws NoviCloudException {
        boolean read = mode != DemoMode.CREATE_SOFT;
        boolean create = mode == DemoMode.CREATE_SOFT;
        boolean updateDelete = mode == DemoMode.CRUD_ALL;
        WalutyClient api = client.waluty();
        runCount(api, read);
        runGetById(api, read);
        runListAll(api, read);
        runListPageAllFilters(api, read);
        runCreateOnce(api, create);
        runUpdateDelete(api, updateDelete);
    }

    private void runCount(WalutyClient api, boolean enabled) {
        if (!enabled) { return; }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private void runGetById(WalutyClient api, boolean enabled) {
        if (!enabled) { return; }
        PagedResult<Waluta> result = api.list(null);
        Iterator<Waluta> iterator = result.iterator();
        if (iterator.hasNext()) {
            Long firstId = iterator.next().id();
            api.getById(firstId);
            logGetById(LOG, ENDPOINT, firstId);
        }
    }

    private void runListAll(WalutyClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<Waluta> result = api.list(null);
        Iterator<Waluta> iterator = result.iterator();
        while (iterator.hasNext() && iterated < LIST_ALL_LIMIT) { iterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListPageAllFilters(WalutyClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(WalutaQueryBuilder.builder()
                .fts(FILTER_ALL_STR).id(FILTER_ALL_ID).nazwa(FILTER_ALL_STR)
                .kod(FILTER_ALL_STR).kurs(FILTER_ALL_ID).aktywny(false)
                .build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }

    private void runUpdateDelete(WalutyClient api, boolean enabled) {
        if (!enabled) {
            logModeSkip(LOG, ENDPOINT, mode.name());
            return;
        }
        String savedId = ids.get(IDS_KEY);
        if (savedId == null) {
            LOG.warn(LOG_NO_SAVED_ID, ENDPOINT);
            return;
        }
        long id = Long.parseLong(savedId);
        var fetched = api.getById(id);
        LOG.info(LOG_UD_GET, ENDPOINT, id, fetched.nazwa(), fetched.kod(), fetched.aktywny());
        if (fetched.aktywny() != null && !fetched.aktywny()) {
            api.update(WalutaUpdateBuilder.builder(id).aktywny(true).build());
            LOG.info(LOG_UD_REACTIVATED, ENDPOINT, id);
        }
        api.update(WalutaUpdateBuilder.builder(id).nazwa(DEMO_NAZWA_UPDATED).build());
        LOG.info(LOG_UD_UPDATE, ENDPOINT, id, DEMO_NAZWA_UPDATED);
        var updated = api.getById(id);
        verifyField(ENDPOINT, FIELD_NAZWA, DEMO_NAZWA_UPDATED, updated.nazwa());
        api.deleteById(id);
        LOG.info(LOG_UD_DELETE, ENDPOINT, id);
        var deleted = api.getById(id);
        verifyField(ENDPOINT, FIELD_AKTYWNY, false, deleted.aktywny());
        LOG.info(LOG_UD_SOFT_DELETE_OK, ENDPOINT);
        api.update(WalutaUpdateBuilder.builder(id).nazwa(fetched.nazwa()).aktywny(true).build());
        LOG.info(LOG_UD_RESTORED, ENDPOINT, id);
        LOG.info(LOG_UD_PASSED, ENDPOINT);
    }

    private static void verifyField(String endpoint, String field, Object expected, Object actual) {
        boolean ok = String.valueOf(expected).equals(String.valueOf(actual));
        LOG.info(LOG_VERIFY, endpoint, field, actual, expected, ok ? VERIFY_OK : VERIFY_MISMATCH);
        if (!ok) { throw new AssertionError(String.format(ERR_VERIFY_FMT, endpoint, field, expected, actual)); }
    }

    private void runCreateOnce(WalutyClient api, boolean enabled) {
        if (!enabled) { return; }
        this.createdId = api.create(WalutaCreateBuilder.builder(DEMO_NAZWA, DEMO_KOD).build());
        LOG.info(LOG_CREATE_ONCE, ENDPOINT, DEMO_NAZWA, DEMO_KOD, createdId);
    }

}
