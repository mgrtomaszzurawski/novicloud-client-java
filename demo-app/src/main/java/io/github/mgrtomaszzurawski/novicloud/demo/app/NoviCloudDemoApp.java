/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.app;

import io.github.mgrtomaszzurawski.novicloud.demo.config.AppProperties;
import io.github.mgrtomaszzurawski.novicloud.demo.config.Credentials;
import io.github.mgrtomaszzurawski.novicloud.demo.config.DemoMode;
import io.github.mgrtomaszzurawski.novicloud.demo.config.PropertiesLoader;
import io.github.mgrtomaszzurawski.novicloud.demo.config.SoftDeleteIds;
import io.github.mgrtomaszzurawski.novicloud.demo.config.SoftDeleteIdsCollector;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.AsortyRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.CreatesTestRecord;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.DokumentyRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.EndpointRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.FormyPlatnRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.JmiaryRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.KartyLojRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.KasjerzyRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.KasyRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.KontrahenciRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.KrajeRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.PozdokRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.RapPracyRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.RapSprzedRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.api.RunReport;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.SklepyRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.SprzedazRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.StanyMagRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.StawkiVatRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.TowaryRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.runner.WalutyRunner;
import io.github.mgrtomaszzurawski.novicloud.demo.service.DemoSession;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class NoviCloudDemoApp {

    private static final Logger LOG = LoggerFactory.getLogger(NoviCloudDemoApp.class);
    private static final String PROPERTIES_FILE = "application.properties";
    private static final String ERR_MISSING_CREDENTIALS =
            "Missing credentials. Set NOVICLOUD_ACCOUNT_NAME and NOVICLOUD_PASSWORD.";
    private static final String MOCK_ACCOUNT = "test";
    private static final Path SOFT_DELETE_IDS_FILE = Path.of("demo-soft-delete-ids.properties");
    private static final String ERR_MODE_NOT_SET =
            "demo.mode is not set. Must be one of: READ_ONLY, CRUD_SAFE, CREATE_SOFT, CRUD_ALL";
    private static final String ERR_MODE_INVALID_FMT =
            "Invalid demo.mode='%s'. Must be one of: READ_ONLY, CRUD_SAFE, CREATE_SOFT, CRUD_ALL";
    private static final String MOCK_EMPTY_PASSWORD = "";
    private static final int EXIT_FAILURE = 1;
    private static final String ERR_IDS_FILE_EXISTS =
            " already exists. Delete the file manually to create new test records.";
    private static final String ERR_IDS_EXISTING_SUFFIX = " Existing IDs: ";
    private static final String ERR_IDS_FILE_NOT_FOUND =
            " not found. Run with demo.mode=CREATE_SOFT first to create test records.";
    private static final String ERR_IDS_LOAD_FAILED = "Failed to load ";
    private static final String LOG_DEMO_MODE = "Demo mode: {}";
    private static final String LOG_LOADED_IDS = "Loaded soft-delete IDs: {}";
    private static final String LOG_NO_RECORDS_CREATED = "No test records were created - nothing to save";
    private static final String LOG_SAVED_IDS = "Saved soft-delete IDs to {}: {}";
    private static final String LOG_SAVE_FAILED = "Failed to save {}: {}";
    private static final String LOG_MOCK_BASE_URL = "[MOCK] Base URL: {}  account: {}";

    private NoviCloudDemoApp() {
    }

    public static void main(String[] args) {
        AppProperties properties = AppProperties.from(PropertiesLoader.load(PROPERTIES_FILE));
        DemoMode mode = parseMode(properties.demoMode());
        LOG.info(LOG_DEMO_MODE, mode);

        Credentials credentials = properties.credentials();
        String baseUrl = properties.baseUrl();
        if ((baseUrl == null || baseUrl.isBlank()) && !credentials.isValid()) {
            LOG.error(ERR_MISSING_CREDENTIALS);
            System.exit(EXIT_FAILURE);
        }

        SoftDeleteIds ids = loadIds(mode);
        boolean failed;
        try (NoviCloudClient client = buildClient(credentials, baseUrl)) {
            List<EndpointRunner> runners = List.of(
                    new AsortyRunner(mode),
                    new JmiaryRunner(mode),
                    new StawkiVatRunner(mode),
                    new KrajeRunner(mode),
                    new DokumentyRunner(mode),
                    new PozdokRunner(mode),
                    new SprzedazRunner(mode),
                    new RapSprzedRunner(mode),
                    new RapPracyRunner(mode),
                    new TowaryRunner(mode, ids),
                    new WalutyRunner(mode, ids),
                    new KontrahenciRunner(mode, ids),
                    new SklepyRunner(mode, ids),
                    new FormyPlatnRunner(mode, ids),
                    new KasyRunner(mode),
                    new KasjerzyRunner(mode),
                    new KartyLojRunner(mode, ids),
                    new StanyMagRunner(mode)
            );

            RunReport report = new DemoSession(client).runAll(runners);

            report.print();

            if (mode == DemoMode.CREATE_SOFT) {
                collectAndSaveIds(runners);
            }

            failed = report.hasFailures();
        }

        if (failed) {
            System.exit(EXIT_FAILURE);
        }
    }

    private static DemoMode parseMode(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException(ERR_MODE_NOT_SET);
        }
        try {
            return DemoMode.valueOf(raw.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(String.format(ERR_MODE_INVALID_FMT, raw), e);
        }
    }

    private static SoftDeleteIds loadIds(DemoMode mode) {
        if (mode == DemoMode.CREATE_SOFT) {
            if (Files.exists(SOFT_DELETE_IDS_FILE)) {
                String msg = SOFT_DELETE_IDS_FILE + ERR_IDS_FILE_EXISTS;
                try {
                    SoftDeleteIds existing = SoftDeleteIds.load(SOFT_DELETE_IDS_FILE);
                    msg += ERR_IDS_EXISTING_SUFFIX + existing.summary();
                } catch (IOException ignored) {
                    // file exists but unreadable - the message above is enough
                }
                throw new IllegalStateException(msg);
            }
            return null;
        }
        if (mode == DemoMode.CRUD_ALL) {
            if (!Files.exists(SOFT_DELETE_IDS_FILE)) {
                throw new IllegalStateException(SOFT_DELETE_IDS_FILE + ERR_IDS_FILE_NOT_FOUND);
            }
            try {
                SoftDeleteIds ids = SoftDeleteIds.load(SOFT_DELETE_IDS_FILE);
                String summary = ids.summary();
                LOG.info(LOG_LOADED_IDS, summary);
                return ids;
            } catch (IOException e) {
                throw new IllegalStateException(ERR_IDS_LOAD_FAILED + SOFT_DELETE_IDS_FILE, e);
            }
        }
        return null;
    }

    private static void collectAndSaveIds(List<EndpointRunner> runners) {
        SoftDeleteIdsCollector collector = new SoftDeleteIdsCollector();
        for (EndpointRunner runner : runners) {
            if (runner instanceof CreatesTestRecord testRecord && testRecord.createdId() != null) {
                collector.put(testRecord.idsKey(), testRecord.createdId());
            }
        }
        if (collector.isEmpty()) {
            LOG.warn(LOG_NO_RECORDS_CREATED);
            return;
        }
        try {
            collector.save(SOFT_DELETE_IDS_FILE);
            String summary = collector.summary();
            LOG.info(LOG_SAVED_IDS, SOFT_DELETE_IDS_FILE, summary);
        } catch (IOException e) {
            LOG.error(LOG_SAVE_FAILED, SOFT_DELETE_IDS_FILE, e.getMessage());
        }
    }

    private static NoviCloudClient buildClient(Credentials credentials, String baseUrl) {
        boolean mockMode = baseUrl != null && !baseUrl.isBlank();
        String accountName = credentials.accountName();
        String password = credentials.password().isBlank() ? MOCK_EMPTY_PASSWORD : credentials.password();

        if (mockMode) {
            LOG.info(LOG_MOCK_BASE_URL, baseUrl, MOCK_ACCOUNT);
            return NoviCloudClient.builder().baseUrl(baseUrl).build(MOCK_ACCOUNT, MOCK_EMPTY_PASSWORD);
        }
        return NoviCloudClient.create(accountName, password);
    }
}
