/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.demo.config.SoftDeleteIds;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.CreatesTestRecord;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.FormaPlatn;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn.FormyPlatnClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn.FormaPlatnCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn.FormaPlatnQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn.FormaPlatnUpdateBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.Iterator;

public final class FormyPlatnRunner implements EndpointRunner, CreatesTestRecord {

    private static final Logger LOG = LoggerFactory.getLogger(FormyPlatnRunner.class);
    private static final String ENDPOINT = "formyplatn";
    private static final String IDS_KEY = "formyplatn.id";
    private final DemoMode mode;
    private final SoftDeleteIds ids;
    private String createdId;

    public FormyPlatnRunner(DemoMode mode, SoftDeleteIds ids) {
        this.mode = mode;
        this.ids = ids;
    }

    @Override public String idsKey() { return IDS_KEY; }
    @Override public String createdId() { return createdId; }

    private static final int LIST_ALL_LIMIT = 3;
    private static final String DEMO_NAZWA = "SDK-TEST Forma";
    private static final String DEMO_NAZWA_UPDATED = "SDK-TEST Forma Updated";
    private static final int DEMO_TYP = 1;
    private static final int DEMO_TYP_UPDATED = 2;
    private static final boolean DEMO_RESZTA = false;
    private static final boolean DEMO_RESZTA_UPDATED = true;
    private static final String FILTER_ALL_ID = "32766";

    private static final String LOG_LIST_FILTERS = "[{}] list(allFilters) -> {} items";
    private static final String LOG_NO_SAVED_ID = "[{}] No saved ID in demo-soft-delete-ids.properties, skipping";
    private static final String LOG_UD_GET = "[{}] UD: getById({}) -> nazwa='{}', typ={}, reszta={}, aktywny={}";
    private static final String LOG_UD_REACTIVATED = "[{}] UD: reactivated id={}";
    private static final String LOG_UD_UPDATE = "[{}] UD: update({}, nazwa='{}', typ={}, reszta={}) -> OK";
    private static final String LOG_UD_DELETE = "[{}] UD: deleteById({}) -> OK";
    private static final String LOG_UD_SOFT_DELETE_OK = "[{}] UD: soft-delete confirmed (aktywny=false)";
    private static final String LOG_UD_RESTORED = "[{}] UD: restored original state id={}";
    private static final String LOG_UD_PASSED = "[{}] UD: update-delete cycle PASSED (soft-delete endpoint)";
    private static final String LOG_CREATE_ONCE = "[{}] CREATE-ONCE: create(nazwa='{}', typ={}, reszta={}) -> id={}";
    private static final String LOG_VERIFY = "[{}] CUD: verify {} = '{}' (expected '{}') -> {}";
    private static final String ERR_VERIFY_FMT = "[%s] %s: expected %s but got %s";
    private static final String VERIFY_OK = "OK";
    private static final String VERIFY_MISMATCH = "MISMATCH";
    private static final String FIELD_NAZWA = "nazwa";
    private static final String FIELD_TYP = "typ";
    private static final String FIELD_RESZTA = "reszta";
    private static final String FIELD_AKTYWNY = "aktywny";

    @Override
    public String name() { return ENDPOINT; }

    @Override
    public void run(NoviCloudClient client) throws NoviCloudException {
        boolean read = mode != DemoMode.CREATE_SOFT;
        boolean create = mode == DemoMode.CREATE_SOFT;
        boolean updateDelete = mode == DemoMode.CRUD_ALL;
        FormyPlatnClient api = client.formyPlatn();
        runCount(api, read);
        runGetById(api, read);
        runListAll(api, read);
        runListPageAllFilters(api, read);
        runCreateOnce(api, create);
        runUpdateDelete(api, updateDelete);
    }

    private void runCount(FormyPlatnClient api, boolean enabled) {
        if (!enabled) { return; }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private void runGetById(FormyPlatnClient api, boolean enabled) {
        if (!enabled) { return; }
        PagedResult<FormaPlatn> result = api.list(null);
        Iterator<FormaPlatn> iterator = result.iterator();
        if (iterator.hasNext()) {
            Long firstId = iterator.next().id();
            api.getById(firstId);
            logGetById(LOG, ENDPOINT, firstId);
        }
    }

    private void runListAll(FormyPlatnClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<FormaPlatn> result = api.list(null);
        Iterator<FormaPlatn> listAllIterator = result.iterator();
        while (listAllIterator.hasNext() && iterated < LIST_ALL_LIMIT) { listAllIterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListPageAllFilters(FormyPlatnClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(FormaPlatnQueryBuilder.builder()
                .id(FILTER_ALL_ID)
                .build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }

    private void runUpdateDelete(FormyPlatnClient api, boolean enabled) {
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
        String fetchedNazwa = fetched.nazwa();
        var fetchedTyp = fetched.typ();
        Boolean fetchedReszta = fetched.reszta();
        Boolean fetchedAktywny = fetched.aktywny();
        LOG.info(LOG_UD_GET, ENDPOINT, id, fetchedNazwa, fetchedTyp, fetchedReszta, fetchedAktywny);
        if (fetchedAktywny != null && !fetchedAktywny) {
            api.update(FormaPlatnUpdateBuilder.builder(id).aktywny(true).build());
            LOG.info(LOG_UD_REACTIVATED, ENDPOINT, id);
        }
        api.update(FormaPlatnUpdateBuilder.builder(id)
                .nazwa(DEMO_NAZWA_UPDATED).typ(DEMO_TYP_UPDATED).reszta(DEMO_RESZTA_UPDATED).build());
        LOG.info(LOG_UD_UPDATE, ENDPOINT, id, DEMO_NAZWA_UPDATED, DEMO_TYP_UPDATED, DEMO_RESZTA_UPDATED);
        var updated = api.getById(id);
        verifyField(ENDPOINT, FIELD_NAZWA, DEMO_NAZWA_UPDATED, updated.nazwa());
        verifyField(ENDPOINT, FIELD_TYP, DEMO_TYP_UPDATED,
                updated.typ() != null ? updated.typ().code() : null);
        verifyField(ENDPOINT, FIELD_RESZTA, DEMO_RESZTA_UPDATED, updated.reszta());
        api.deleteById(id);
        LOG.info(LOG_UD_DELETE, ENDPOINT, id);
        var deleted = api.getById(id);
        verifyField(ENDPOINT, FIELD_AKTYWNY, false, deleted.aktywny());
        LOG.info(LOG_UD_SOFT_DELETE_OK, ENDPOINT);
        api.update(FormaPlatnUpdateBuilder.builder(id)
                .nazwa(fetched.nazwa())
                .typ(fetched.typ() != null ? fetched.typ().code() : null)
                .reszta(fetched.reszta())
                .aktywny(true).build());
        LOG.info(LOG_UD_RESTORED, ENDPOINT, id);
        LOG.info(LOG_UD_PASSED, ENDPOINT);
    }

    private void runCreateOnce(FormyPlatnClient api, boolean enabled) {
        if (!enabled) { return; }
        this.createdId = api.create(FormaPlatnCreateBuilder.builder(DEMO_NAZWA, DEMO_TYP)
                .reszta(DEMO_RESZTA).build());
        LOG.info(LOG_CREATE_ONCE, ENDPOINT, DEMO_NAZWA, DEMO_TYP, DEMO_RESZTA, createdId);
    }

    private static void verifyField(String endpoint, String field, Object expected, Object actual) {
        boolean ok = String.valueOf(expected).equals(String.valueOf(actual));
        LOG.info(LOG_VERIFY, endpoint, field, actual, expected, ok ? VERIFY_OK : VERIFY_MISMATCH);
        if (!ok) {
            throw new AssertionError(String.format(ERR_VERIFY_FMT, endpoint, field, expected, actual));
        }
    }
}
