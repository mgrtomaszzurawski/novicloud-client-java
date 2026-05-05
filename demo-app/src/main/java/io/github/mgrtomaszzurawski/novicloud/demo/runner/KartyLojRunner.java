/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner;

import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.demo.config.SoftDeleteIds;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.CreatesTestRecord;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.KartaLojalnosciowa;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunnerHelper.*;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj.KartyLojClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj.KartaLojCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj.KartaLojQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj.KartaLojUpdateBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import java.util.Iterator;

public final class KartyLojRunner implements EndpointRunner, CreatesTestRecord {

    private static final Logger LOG = LoggerFactory.getLogger(KartyLojRunner.class);
    private static final String ENDPOINT = "kartyloj";
    private static final String IDS_KEY = "kartyloj.kod";
    private final DemoMode mode;
    private final SoftDeleteIds ids;
    private String createdId;

    public KartyLojRunner(DemoMode mode, SoftDeleteIds ids) {
        this.mode = mode;
        this.ids = ids;
    }

    @Override public String idsKey() { return IDS_KEY; }
    @Override public String createdId() { return createdId; }

    private static final String DEMO_KOD = "SDK-DEMO-LOJ-001";
    private static final int LIST_ALL_LIMIT = 3;
    private static final String DEMO_HOLDER = "SDK Demo Holder";
    private static final String DEMO_EMAIL = "sdk-test@example.com";
    private static final String DEMO_HOLDER_UPDATED = "SDK Demo Holder Updated";
    private static final String FILTER_ALL_STR = "~ZZZZ~";
    private static final String INVALIDATE_DATE = "2099-12-31T00:00:00";
    private static final String FILTER_ALL_DATE = "2099-12-31";

    private static final String LOG_GET_BY_KOD = "[{}] getByKod({}) -> OK";
    private static final String LOG_LIST_FILTERS = "[{}] list(allFilters) -> {} items";
    private static final String LOG_NO_SAVED_KOD = "[{}] No saved kod in demo-soft-delete-ids.properties, skipping";
    private static final String LOG_UD_GET = "[{}] UD: getByKod({}) -> nazwiskoImie='{}', email='{}', uniewazniono='{}'";
    private static final String LOG_UD_CLEARED = "[{}] UD: cleared uniewazniono for kod={}";
    private static final String LOG_UD_UPDATE = "[{}] UD: update(kod={}, nazwiskoImie='{}') -> OK";
    private static final String LOG_UD_INVALIDATE = "[{}] UD: invalidate(kod={}, uniewazniono='{}') -> OK";
    private static final String LOG_UD_GET_AFTER = "[{}] UD: getByKod({}) after invalidate -> uniewazniono='{}'";
    private static final String LOG_UD_RESTORED = "[{}] UD: restored original state kod={}";
    private static final String LOG_UD_PASSED = "[{}] UD: update-invalidate cycle PASSED (soft-delete via uniewazniono)";
    private static final String LOG_CREATE_ONCE = "[{}] CREATE-ONCE: create(kod='{}', nazwiskoImie='{}', email='{}') -> id={}";
    private static final String LOG_VERIFY = "[{}] UD: verify {} = '{}' (expected '{}') -> {}";
    private static final String ERR_VERIFY_FMT = "[%s] %s: expected %s but got %s";
    private static final String VERIFY_OK = "OK";
    private static final String VERIFY_MISMATCH = "MISMATCH";
    private static final String FIELD_NAZWISKO_IMIE = "nazwiskoImie";

    @Override
    public String name() { return ENDPOINT; }

    @Override
    public void run(NoviCloudClient client) throws NoviCloudException {
        boolean read = mode != DemoMode.CREATE_SOFT;
        boolean create = mode == DemoMode.CREATE_SOFT;
        boolean updateInvalidate = mode == DemoMode.CRUD_ALL;
        KartyLojClient api = client.kartyLoj();
        runCount(api, read);
        runGetByKod(api, read);
        runListAll(api, read);
        runListPageAllFilters(api, read);
        runCreateOnce(api, create);
        runUpdateInvalidate(api, updateInvalidate);
    }

    private void runCount(KartyLojClient api, boolean enabled) {
        if (!enabled) { return; }
        logCount(LOG, ENDPOINT, api.count(null));
    }

    private void runGetByKod(KartyLojClient api, boolean enabled) {
        if (!enabled) { return; }
        PagedResult<KartaLojalnosciowa> result = api.list(null);
        Iterator<KartaLojalnosciowa> iterator = result.iterator();
        if (iterator.hasNext()) {
            String firstKod = iterator.next().kod();
            if (firstKod != null) {
                api.getByKod(firstKod);
                LOG.info(LOG_GET_BY_KOD, ENDPOINT, firstKod);
            }
        }
    }

    private void runListAll(KartyLojClient api, boolean enabled) {
        if (!enabled) { return; }
        int iterated = ITERATION_START;
        PagedResult<KartaLojalnosciowa> result = api.list(null);
        Iterator<KartaLojalnosciowa> listAllIterator = result.iterator();
        while (listAllIterator.hasNext() && iterated < LIST_ALL_LIMIT) { listAllIterator.next(); iterated++; }
        logListAll(LOG, ENDPOINT, iterated);
    }

    private void runListPageAllFilters(KartyLojClient api, boolean enabled) {
        if (!enabled) { return; }
        int count = api.count(KartaLojQueryBuilder.builder()
                .kod(FILTER_ALL_STR)
                .posiadacz(FILTER_ALL_STR)
                .telefon(FILTER_ALL_STR)
                .email(FILTER_ALL_STR)
                .uniewazniono(FILTER_ALL_DATE)
                .build());
        LOG.info(LOG_LIST_FILTERS, ENDPOINT, count);
    }

    private void runUpdateInvalidate(KartyLojClient api, boolean enabled) {
        if (!enabled) {
            logModeSkip(LOG, ENDPOINT, mode.name());
            return;
        }
        String kod = ids.get(IDS_KEY);
        if (kod == null) {
            LOG.warn(LOG_NO_SAVED_KOD, ENDPOINT);
            return;
        }
        var fetched = api.getByKod(kod);
        String fetchedHolder = fetched.nazwiskoImie();
        String fetchedEmail = fetched.email();
        var fetchedInvalidated = fetched.uniewazniono();
        LOG.info(LOG_UD_GET, ENDPOINT, kod, fetchedHolder, fetchedEmail, fetchedInvalidated);
        if (fetchedInvalidated != null) {
            api.update(KartaLojUpdateBuilder.builder(kod).uniewazniono(null).build());
            LOG.info(LOG_UD_CLEARED, ENDPOINT, kod);
        }
        api.update(KartaLojUpdateBuilder.builder(kod).nazwiskoImie(DEMO_HOLDER_UPDATED).build());
        LOG.info(LOG_UD_UPDATE, ENDPOINT, kod, DEMO_HOLDER_UPDATED);
        var updated = api.getByKod(kod);
        verifyField(ENDPOINT, FIELD_NAZWISKO_IMIE, DEMO_HOLDER_UPDATED, updated.nazwiskoImie());
        api.update(KartaLojUpdateBuilder.builder(kod).uniewazniono(INVALIDATE_DATE).build());
        LOG.info(LOG_UD_INVALIDATE, ENDPOINT, kod, INVALIDATE_DATE);
        var invalidated = api.getByKod(kod);
        LOG.info(LOG_UD_GET_AFTER, ENDPOINT, kod, invalidated.uniewazniono());
        api.update(KartaLojUpdateBuilder.builder(kod)
                .nazwiskoImie(fetched.nazwiskoImie())
                .uniewazniono(null)
                .build());
        LOG.info(LOG_UD_RESTORED, ENDPOINT, kod);
        LOG.info(LOG_UD_PASSED, ENDPOINT);
    }

    private static void verifyField(String endpoint, String field, Object expected, Object actual) {
        boolean ok = String.valueOf(expected).equals(String.valueOf(actual));
        LOG.info(LOG_VERIFY, endpoint, field, actual, expected, ok ? VERIFY_OK : VERIFY_MISMATCH);
        if (!ok) {
            throw new AssertionError(String.format(ERR_VERIFY_FMT, endpoint, field, expected, actual));
        }
    }

    private void runCreateOnce(KartyLojClient api, boolean enabled) {
        if (!enabled) { return; }
        this.createdId = api.create(
                KartaLojCreateBuilder.builder(DEMO_KOD).nazwiskoImie(DEMO_HOLDER).email(DEMO_EMAIL).build());
        LOG.info(LOG_CREATE_ONCE, ENDPOINT, DEMO_KOD, DEMO_HOLDER, DEMO_EMAIL, createdId);
        this.createdId = DEMO_KOD;
    }

}
