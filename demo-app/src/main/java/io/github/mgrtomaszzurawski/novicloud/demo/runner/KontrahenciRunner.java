/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.demo.config.SoftDeleteIds;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.CreatesTestRecord;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kontrahent;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci.KontrahenciClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci.KontrahentQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci.KontrahentCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci.KontrahentUpdateBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.Iterator;

public final class KontrahenciRunner implements EndpointRunner, CreatesTestRecord {

    private static final Logger LOG = LoggerFactory.getLogger(KontrahenciRunner.class);
    private static final String ENDPOINT = "kontrahenci";
    private static final String IDS_KEY = "kontrahenci.id";
    private final DemoMode mode;
    private final SoftDeleteIds ids;
    private String createdId;

    public KontrahenciRunner(DemoMode mode, SoftDeleteIds ids) {
        this.mode = mode;
        this.ids = ids;
    }

    @Override public String idsKey() { return IDS_KEY; }
    @Override public String createdId() { return createdId; }

    private static final int LIST_ALL_LIMIT = 3;
    private static final String DEMO_NAZWA = "SDK Demo Kontrahent";
    private static final String DEMO_NAZWA_UPDATED = "SDK Demo Kontrahent Updated";
    private static final String FILTER_ALL_STR = "~ZZZZ~";
    private static final String FILTER_ALL_ID = "32766";

    private static final String LOG_LIST_FILTERS = "[{}] list(allFilters) -> {} items";
    private static final String LOG_NO_SAVED_ID = "[{}] No saved ID in demo-soft-delete-ids.properties, skipping";
    private static final String LOG_UD_GET = "[{}] UD: getById({}) -> nazwa='{}', aktywny={}";
    private static final String LOG_UD_REACTIVATED = "[{}] UD: reactivated id={}";
    private static final String LOG_UD_UPDATE = "[{}] UD: update({}, nazwa='{}') -> OK";
    private static final String LOG_UD_DELETE = "[{}] UD: deleteById({}) -> OK";
    private static final String LOG_UD_SOFT_DELETE_OK = "[{}] UD: soft-delete confirmed (aktywny=false)";
    private static final String LOG_UD_RESTORED = "[{}] UD: restored original state id={}";
    private static final String LOG_UD_PASSED = "[{}] UD: update-delete cycle PASSED (soft-delete endpoint)";
    private static final String LOG_CREATE_ONCE = "[{}] CREATE-ONCE: create(nazwa='{}') -> id={}";
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
        KontrahenciClient api = client.kontrahenci();
        runCount(api, read);
        runGetById(api, read);
        runListAll(api, read);
        runListPageAllFilters(api, read);
        runCreateOnce(api, create);
        runUpdateDelete(api, updateDelete);
    }

    private void runCount(KontrahenciClient api, boolean enabled) {
        if (!enabled) { return; }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private void runGetById(KontrahenciClient api, boolean enabled) {
        if (!enabled) { return; }
        PagedResult<Kontrahent> result = api.list(null);
        Iterator<Kontrahent> iterator = result.iterator();
        if (iterator.hasNext()) {
            Long firstId = iterator.next().id();
            api.getById(firstId);
            logGetById(LOG, ENDPOINT, firstId);
        }
    }

    private void runListAll(KontrahenciClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<Kontrahent> result = api.list(null);
        Iterator<Kontrahent> iterator = result.iterator();
        while (iterator.hasNext() && iterated < LIST_ALL_LIMIT) { iterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListPageAllFilters(KontrahenciClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(KontrahentQueryBuilder.builder()
                .fts(FILTER_ALL_STR).id(FILTER_ALL_ID).nazwa(FILTER_ALL_STR)
                .nip(FILTER_ALL_STR).skrot(FILTER_ALL_STR).ulica(FILTER_ALL_STR)
                .nrDomu(FILTER_ALL_STR).nrLokalu(FILTER_ALL_STR).kodPoczt(FILTER_ALL_STR)
                .poczta(FILTER_ALL_STR).miasto(FILTER_ALL_STR).telefon(FILTER_ALL_STR)
                .email(FILTER_ALL_STR).krajId(FILTER_ALL_ID)
                .aktywny(false).dostawca(false).staly(false).producent(false).odbiorca(false)
                .build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }

    private void runUpdateDelete(KontrahenciClient api, boolean enabled) {
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
        Boolean fetchedAktywny = fetched.aktywny();
        LOG.info(LOG_UD_GET, ENDPOINT, id, fetchedNazwa, fetchedAktywny);
        if (fetchedAktywny != null && !fetchedAktywny) {
            api.update(KontrahentUpdateBuilder.builder(id).aktywny(true).build());
            LOG.info(LOG_UD_REACTIVATED, ENDPOINT, id);
        }
        api.update(KontrahentUpdateBuilder.builder(id).nazwa(DEMO_NAZWA_UPDATED).build());
        LOG.info(LOG_UD_UPDATE, ENDPOINT, id, DEMO_NAZWA_UPDATED);
        var updated = api.getById(id);
        verifyField(ENDPOINT, FIELD_NAZWA, DEMO_NAZWA_UPDATED, updated.nazwa());
        api.deleteById(id);
        LOG.info(LOG_UD_DELETE, ENDPOINT, id);
        var deleted = api.getById(id);
        verifyField(ENDPOINT, FIELD_AKTYWNY, false, deleted.aktywny());
        LOG.info(LOG_UD_SOFT_DELETE_OK, ENDPOINT);
        api.update(KontrahentUpdateBuilder.builder(id).nazwa(fetched.nazwa()).aktywny(true).build());
        LOG.info(LOG_UD_RESTORED, ENDPOINT, id);
        LOG.info(LOG_UD_PASSED, ENDPOINT);
    }

    private static void verifyField(String endpoint, String field, Object expected, Object actual) {
        boolean ok = String.valueOf(expected).equals(String.valueOf(actual));
        LOG.info(LOG_VERIFY, endpoint, field, actual, expected, ok ? VERIFY_OK : VERIFY_MISMATCH);
        if (!ok) { throw new AssertionError(String.format(ERR_VERIFY_FMT, endpoint, field, expected, actual)); }
    }

    private void runCreateOnce(KontrahenciClient api, boolean enabled) {
        if (!enabled) { return; }
        this.createdId = api.create(KontrahentCreateBuilder.builder(DEMO_NAZWA).build());
        LOG.info(LOG_CREATE_ONCE, ENDPOINT, DEMO_NAZWA, createdId);
    }

}
