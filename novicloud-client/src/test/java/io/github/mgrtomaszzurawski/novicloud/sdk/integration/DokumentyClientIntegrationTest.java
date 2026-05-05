/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudAuthException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudRateLimitException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudServerException;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Dokument;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class DokumentyClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/dokumenty";
    private static final String URL_BY_ID = "/[^/]+/dokumenty/[^/]+";
    private static final String LIST_FILE = "dokumenty/list.json";
    private static final String SINGLE_FILE = "dokumenty/single.json";
    private static final int TYP_DOK = 14;
    private static final int EXPECTED_SIZE = 1;
    private static final long EXPECTED_FIRST_ID = 1L;

    // Expected field values
    private static final String EXPECTED_NR_DOK = "DOK/001/2026";
    private static final String EXPECTED_SKLEP_ID = "1";
    private static final String EXPECTED_KASJER_ID = "2";
    private static final double EXPECTED_NETTO = 100.0;
    private static final double EXPECTED_PODATEK = 23.0;
    private static final double EXPECTED_BRUTTO = 123.0;
    private static final int EXPECTED_ROZBICIE_VAT_COUNT = 1;
    private static final int EXPECTED_VAT_RATE = 2300;
    private static final int EXPECTED_PLATNOSCI_COUNT = 1;
    private static final String EXPECTED_PLATNOSC_KOD_WALUTY = "PLN";
    private static final String EXPECTED_KOREKTY_FIRST_ID = "11";
    private static final String EXPECTED_FAKTURY_FIRST_ID = "12";
    private static final String EXPECTED_DOK_MAGAZYNOWE_FIRST_ID = "13";
    private static final String EXPECTED_PARAGONY_FIRST_ID = "14";
    private static final String EXPECTED_ROZLICZANY_DOK_ID = "100";
    private static final String EXPECTED_POZYCJE_ID = "1";

    // HTTP status codes

    private NoviCloudClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        client = TestClients.create(wm);
    }

    @Test
    void list_whenServerReturnsData_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        List<Dokument> dane = new ArrayList<>();
        client.dokumenty().list(null).forEach(dane::add);

        // then
        assertEquals(EXPECTED_SIZE, dane.size());

        Dokument d = dane.get(FIRST_INDEX);
        assertEquals(EXPECTED_FIRST_ID, d.id());
        assertEquals(TYP_DOK, d.typDok());
        assertNotNull(d.dataWystawienia());
        assertEquals(EXPECTED_NR_DOK, d.nrDok());
        assertNotNull(d.sklepId());
        assertEquals(EXPECTED_SKLEP_ID, d.sklepId());
        assertNotNull(d.kasjerId());
        assertEquals(EXPECTED_KASJER_ID, d.kasjerId());
        assertEquals(EXPECTED_NETTO, d.netto());
        assertEquals(EXPECTED_PODATEK, d.podatek());
        assertEquals(EXPECTED_BRUTTO, d.brutto());
        assertNotNull(d.pozycjeLink());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int result = client.dokumenty().count(null);

        // then
        assertEquals(EXPECTED_SIZE, result);
    }

    @Test
    void getById_whenRecordExists_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(TestClients.jsonFile(SINGLE_FILE)));

        // when
        Dokument d = client.dokumenty().getById(EXPECTED_FIRST_ID);

        // then
        assertNotNull(d);
        assertEquals(EXPECTED_FIRST_ID, d.id());
        assertEquals(TYP_DOK, d.typDok());
        assertNotNull(d.dataWystawienia());
        assertEquals(EXPECTED_NR_DOK, d.nrDok());
        assertNotNull(d.sklepId());
        assertNotNull(d.kasjerId());
        assertEquals(EXPECTED_NETTO, d.netto());
        assertEquals(EXPECTED_PODATEK, d.podatek());
        assertEquals(EXPECTED_BRUTTO, d.brutto());
        assertEquals(EXPECTED_ROZBICIE_VAT_COUNT, d.rozbicieVat().size());
        assertEquals(EXPECTED_VAT_RATE, d.rozbicieVat().get(FIRST_INDEX).stawka());
        assertEquals(EXPECTED_PLATNOSCI_COUNT, d.platnosci().size());
        assertEquals(EXPECTED_PLATNOSC_KOD_WALUTY, d.platnosci().get(FIRST_INDEX).kodWaluty());
        assertEquals(EXPECTED_KOREKTY_FIRST_ID, d.korektyIds().get(FIRST_INDEX));
        assertEquals(EXPECTED_FAKTURY_FIRST_ID, d.fakturyIds().get(FIRST_INDEX));
        assertEquals(EXPECTED_DOK_MAGAZYNOWE_FIRST_ID, d.dokMagazynoweIds().get(FIRST_INDEX));
        assertEquals(EXPECTED_PARAGONY_FIRST_ID, d.paragonyIds().get(FIRST_INDEX));
        assertEquals(EXPECTED_ROZLICZANY_DOK_ID, d.dokRozliczane().get(FIRST_INDEX).dokumentId());
        assertEquals(EXPECTED_POZYCJE_ID, d.pozycjeId());
        assertNotNull(d.pozycjeUrl());
    }

    // -----------------------------------------------------------------------
    // Error scenarios (F-06)
    // -----------------------------------------------------------------------

    @Test
    void count_whenServerReturns401_throwsAuthException() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(unauthorized()));

        // when / then
        var resource = client.dokumenty();
        NoviCloudAuthException ex = assertThrows(NoviCloudAuthException.class,
                () -> resource.count(null));
        assertEquals(HTTP_UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void getById_whenServerReturns404_throwsNotFoundException() {
        // given
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(notFound()));

        // when / then
        var resource = client.dokumenty();
        NoviCloudNotFoundException ex = assertThrows(NoviCloudNotFoundException.class,
                () -> resource.getById(NON_EXISTENT_ID));
        assertEquals(HTTP_NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void count_whenServerReturns429_throwsRateLimitException() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(aResponse()
                        .withStatus(HTTP_RATE_LIMITED)
                        .withHeader(RETRY_AFTER_HEADER, RETRY_AFTER_SECONDS)));

        // when / then
        var resource = client.dokumenty();
        NoviCloudRateLimitException ex = assertThrows(NoviCloudRateLimitException.class,
                () -> resource.count(null));
        assertEquals(HTTP_RATE_LIMITED, ex.getStatusCode());
        assertEquals(EXPECTED_RETRY_AFTER, ex.getRetryAfterSeconds());
    }

    @Test
    void count_whenServerReturns500_throwsServerException() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(serverError()));

        // when / then
        var resource = client.dokumenty();
        NoviCloudServerException ex = assertThrows(NoviCloudServerException.class,
                () -> resource.count(null));
        assertEquals(HTTP_SERVER_ERROR, ex.getStatusCode());
    }

    // -----------------------------------------------------------------------
    // Pagination (T-15)
    // -----------------------------------------------------------------------

    @Test
    void list_whenMultiplePages_iteratesAllPages(WireMockRuntimeInfo wm) {
        int port = wm.getHttpPort();
        String page1 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 0, "on_page": 2,
                  "links": {
                    "self": "http://localhost:%d/demo/dokumenty?start=0",
                    "next": "http://localhost:%d/demo/dokumenty?start=2"
                  },
                  "dane": [{"id": 1, "nr_dok": "D1"}, {"id": 2, "nr_dok": "D2"}]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/dokumenty?start=2"
                  },
                  "dane": [{"id": 3, "nr_dok": "D3"}]
                }""".formatted(port);

        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .inScenario(SCENARIO_PAGINATION)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(okJson(page1))
                .willSetStateTo(SCENARIO_STATE_PAGE2));

        stubFor(get(urlPathMatching(URL_LIST))
                .inScenario(SCENARIO_PAGINATION)
                .whenScenarioStateIs(SCENARIO_STATE_PAGE2)
                .willReturn(okJson(page2)));

        // when
        java.util.List<Dokument> all = new java.util.ArrayList<>();
        client.dokumenty().list(null).forEach(all::add);

        // then
        assertEquals(EXPECTED_PAGINATION_SIZE, all.size());
    }

    // -----------------------------------------------------------------------
    // Retry (T-16)
    // -----------------------------------------------------------------------

    @Test
    void list_whenFirstCallReturns500_retriesAndReturnsData(WireMockRuntimeInfo wm) {
        var retryClient = TestClients.withRetry(wm);

        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .inScenario(SCENARIO_RETRY)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(serverError())
                .willSetStateTo(SCENARIO_STATE_RECOVERED));

        stubFor(get(urlPathMatching(URL_LIST))
                .inScenario(SCENARIO_RETRY)
                .whenScenarioStateIs(SCENARIO_STATE_RECOVERED)
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when / then
        assertDoesNotThrow(() -> retryClient.dokumenty().count(null));
        verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }
}
