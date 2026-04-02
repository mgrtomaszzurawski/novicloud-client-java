/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.RaportSprzedazy;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudAuthException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudRateLimitException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class RapSprzedClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/rapsprzed";
    private static final String LIST_FILE = "rapsprzed/list.json";
    // Expected field values
    private static final int EXPECTED_LIST_SIZE = 1;
    private static final double EXPECTED_SPRZ_NETTO = 1000.43;
    private static final double EXPECTED_SPRZ_BRUTTO = 1230.53;
    private static final double EXPECTED_SPRZ_ZAK_NETTO = 600.41;
    private static final double EXPECTED_SPRZ_ZAK_BRUTTO = 738.50;
    private static final double EXPECTED_MARZA_NETTO = 400.02;
    private static final double EXPECTED_MARZA_BRUTTO = 492.03;
    private static final double EXPECTED_MARZA_PROC_NETTO = 40.0;
    private static final double EXPECTED_MARZA_PROC_BRUTTO = 40.0;
    private static final double EXPECTED_NARZUT_PROC_NETTO = 66.67;
    private static final double EXPECTED_NARZUT_PROC_BRUTTO = 66.67;
    private static final double EXPECTED_RABAT = 50.13;
    private static final double EXPECTED_RABAT_PROC = 3.92;

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
        List<RaportSprzedazy> dane = new ArrayList<>();
        client.rapSprzed().list(null).forEach(dane::add);

        // then
        assertEquals(EXPECTED_LIST_SIZE, dane.size());

        RaportSprzedazy r = dane.get(FIRST_INDEX);
        assertEquals(EXPECTED_SPRZ_NETTO, r.sprzNetto());
        assertEquals(EXPECTED_SPRZ_BRUTTO, r.sprzBrutto());
        assertEquals(EXPECTED_SPRZ_ZAK_NETTO, r.sprzZakNetto());
        assertEquals(EXPECTED_SPRZ_ZAK_BRUTTO, r.sprzZakBrutto());
        assertEquals(EXPECTED_MARZA_NETTO, r.marzaNetto());
        assertEquals(EXPECTED_MARZA_BRUTTO, r.marzaBrutto());
        assertEquals(EXPECTED_MARZA_PROC_NETTO, r.marzaProcNetto());
        assertEquals(EXPECTED_MARZA_PROC_BRUTTO, r.marzaProcBrutto());
        assertEquals(EXPECTED_NARZUT_PROC_NETTO, r.narzutProcNetto());
        assertEquals(EXPECTED_NARZUT_PROC_BRUTTO, r.narzutProcBrutto());
        assertEquals(EXPECTED_RABAT, r.rabat());
        assertEquals(EXPECTED_RABAT_PROC, r.rabatProc());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int count = client.rapSprzed().count(null);

        // then
        assertEquals(EXPECTED_LIST_SIZE, count);
    }

    // -----------------------------------------------------------------------
    // Error scenarios (T-14)
    // -----------------------------------------------------------------------

    @Test
    void count_whenServerReturns401_throwsAuthException() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(unauthorized()));

        // when / then
        var resource = client.rapSprzed();
        NoviCloudAuthException ex = assertThrows(NoviCloudAuthException.class,
                () -> resource.count(null));
        assertEquals(HTTP_UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void getById_whenServerReturns404_throwsNotFoundException() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(notFound()));

        // when / then
        var resource = client.rapSprzed();
        NoviCloudNotFoundException ex = assertThrows(NoviCloudNotFoundException.class,
                () -> resource.count(null));
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
        var resource = client.rapSprzed();
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
        var resource = client.rapSprzed();
        NoviCloudServerException ex = assertThrows(NoviCloudServerException.class,
                () -> resource.count(null));
        assertEquals(HTTP_SERVER_ERROR, ex.getStatusCode());
    }

    // -----------------------------------------------------------------------
    // Pagination (T-15)
    // -----------------------------------------------------------------------

    @Test
    void list_whenMultiplePages_iteratesAllPages(WireMockRuntimeInfo wm) {
        // given
        int port = wm.getHttpPort();
        String page1 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 0, "on_page": 2,
                  "links": {
                    "self": "http://localhost:%d/demo/rapsprzed?start=0",
                    "next": "http://localhost:%d/demo/rapsprzed?start=2"
                  },
                  "dane": [{"sprz_brutto": 100.0}, {"sprz_brutto": 200.0}]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/rapsprzed?start=2"
                  },
                  "dane": [{"sprz_brutto": 300.0}]
                }""".formatted(port);

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
        java.util.List<RaportSprzedazy> all = new java.util.ArrayList<>();
        client.rapSprzed().list(null).forEach(all::add);

        // then
        assertEquals(EXPECTED_PAGINATION_SIZE, all.size());
    }

    // -----------------------------------------------------------------------
    // Retry (T-16)
    // -----------------------------------------------------------------------

    @Test
    void list_whenFirstCallReturns500_retriesAndReturnsData(WireMockRuntimeInfo wm) {
        // given
        var retryClient = TestClients.withRetry(wm);

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
        assertDoesNotThrow(() -> retryClient.rapSprzed().count(null));
        verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }
}
