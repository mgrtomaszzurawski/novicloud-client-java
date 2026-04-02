/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.RaportPracy;
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
class RapPracyClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/rappracy";
    private static final String LIST_FILE = "rappracy/list.json";
    private static final double EXPECTED_WORK_MINUTES = 480.0;
    private static final double EXPECTED_RECEIPTS = 120.0;
    private static final int EXPECTED_LIST_SIZE = 1;
    private static final double EXPECTED_UTARG = 5000.0;
    private static final double EXPECTED_GOTOWKA = 2500.0;
    private static final double EXPECTED_KARTA = 2000.0;
    private static final double EXPECTED_CZEK = 0.0;
    private static final double EXPECTED_BON = 100.0;
    private static final double EXPECTED_PRZELEW = 400.0;
    private static final double EXPECTED_INNA = 0.0;
    private static final double EXPECTED_PARAGONY_WARTOSC = 4800.0;
    private static final double EXPECTED_PARAGONY_POZYCJE = 350.0;
    private static final double EXPECTED_FAKTURY_ILOSC = 5.0;
    private static final double EXPECTED_FAKTURY_WARTOSC = 200.0;
    private static final double EXPECTED_FAKTURY_POZYCJE = 15.0;
    private static final double EXPECTED_STORNO_POZYCJE = 2.0;
    private static final double EXPECTED_STORNO_WARTOSC = 45.0;
    private static final double EXPECTED_PARAGONY_ANUL_ILOSC = 1.0;
    private static final double EXPECTED_PARAGONY_ANUL_WARTOSC = 12.50;

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
        List<RaportPracy> dane = new ArrayList<>();
        client.rapPracy().list(null).forEach(dane::add);

        // then
        assertEquals(EXPECTED_LIST_SIZE, dane.size());

        RaportPracy r = dane.get(FIRST_INDEX);
        assertEquals(EXPECTED_WORK_MINUTES, r.czasPracy());
        assertEquals(EXPECTED_UTARG, r.utarg());
        assertEquals(EXPECTED_GOTOWKA, r.gotowka());
        assertEquals(EXPECTED_KARTA, r.karta());
        assertEquals(EXPECTED_CZEK, r.czek());
        assertEquals(EXPECTED_BON, r.bon());
        assertEquals(EXPECTED_PRZELEW, r.przelew());
        assertEquals(EXPECTED_INNA, r.inna());
        assertEquals(EXPECTED_RECEIPTS, r.paragonyIlosc());
        assertEquals(EXPECTED_PARAGONY_WARTOSC, r.paragonyWartosc());
        assertEquals(EXPECTED_PARAGONY_POZYCJE, r.paragonyPozycje());
        assertEquals(EXPECTED_FAKTURY_ILOSC, r.fakturyIlosc());
        assertEquals(EXPECTED_FAKTURY_WARTOSC, r.fakturyWartosc());
        assertEquals(EXPECTED_FAKTURY_POZYCJE, r.fakturyPozycje());
        assertEquals(EXPECTED_STORNO_POZYCJE, r.stornoPozycje());
        assertEquals(EXPECTED_STORNO_WARTOSC, r.stornoWartosc());
        assertEquals(EXPECTED_PARAGONY_ANUL_ILOSC, r.paragonyAnulowaneIlosc());
        assertEquals(EXPECTED_PARAGONY_ANUL_WARTOSC, r.paragonyAnulowaneWartosc());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int count = client.rapPracy().count(null);

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
        var resource = client.rapPracy();
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
        var resource = client.rapPracy();
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
        var resource = client.rapPracy();
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
        var resource = client.rapPracy();
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
                    "self": "http://localhost:%d/demo/rappracy?start=0",
                    "next": "http://localhost:%d/demo/rappracy?start=2"
                  },
                  "dane": [{"czas_pracy": 480}, {"czas_pracy": 360}]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/rappracy?start=2"
                  },
                  "dane": [{"czas_pracy": 240}]
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
        java.util.List<RaportPracy> all = new java.util.ArrayList<>();
        client.rapPracy().list(null).forEach(all::add);

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
        assertDoesNotThrow(() -> retryClient.rapPracy().count(null));
        verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }
}
