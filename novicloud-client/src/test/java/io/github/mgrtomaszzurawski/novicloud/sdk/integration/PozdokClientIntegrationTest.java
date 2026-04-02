/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.PozycjaDokumentu;
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
class PozdokClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/pozdok";
    private static final String URL_BY_ID = "/[^/]+/pozdok/[^/]+";
    private static final String LIST_FILE = "pozdok/list.json";
    private static final String SINGLE_FILE = "pozdok/single.json";
    private static final long EXPECTED_ID = 1000001L;
    private static final int EXPECTED_VAT = 2300;
    private static final int EXPECTED_LIST_SIZE = 1;
    private static final String EXPECTED_DOKUMENT_ID = "1";
    private static final String EXPECTED_TOWAR_ID = "2499";
    private static final int EXPECTED_NR_POZYCJI = 1;
    private static final double EXPECTED_ILOSC = 3.0;
    private static final double EXPECTED_C_PRZED_RAB_NETTO = 10.0;
    private static final double EXPECTED_C_PRZED_RAB_BRUTTO = 12.30;
    private static final double EXPECTED_C_PO_RAB_NETTO = 9.0;
    private static final double EXPECTED_C_PO_RAB_BRUTTO = 11.07;
    private static final double EXPECTED_RABAT_KWOTA = 1.23;
    private static final double EXPECTED_W_NETTO = 27.0;
    private static final double EXPECTED_W_PODATEK = 6.21;
    private static final double EXPECTED_W_BRUTTO = 33.21;
    private static final double EXPECTED_ROZL_NETTO = 27.0;
    private static final double EXPECTED_ROZL_PODATEK = 6.21;
    private static final double EXPECTED_ROZL_BRUTTO = 33.21;

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
        List<PozycjaDokumentu> dane = new ArrayList<>();
        client.pozdok().list(null).forEach(dane::add);

        // then
        assertEquals(EXPECTED_LIST_SIZE, dane.size());

        PozycjaDokumentu p = dane.get(FIRST_INDEX);
        assertEquals(EXPECTED_ID, p.id());
        assertNotNull(p.dokumentId());
        assertEquals(EXPECTED_DOKUMENT_ID, p.dokumentId());
        assertNotNull(p.towarId());
        assertEquals(EXPECTED_TOWAR_ID, p.towarId());
        assertEquals(EXPECTED_NR_POZYCJI, p.nrPozycji());
        assertEquals(EXPECTED_ILOSC, p.ilosc());
        assertEquals(EXPECTED_VAT, p.stawkaVat());
        assertEquals(EXPECTED_C_PRZED_RAB_NETTO, p.cPrzedRabNetto());
        assertEquals(EXPECTED_C_PRZED_RAB_BRUTTO, p.cPrzedRabBrutto());
        assertEquals(EXPECTED_C_PO_RAB_NETTO, p.cPoRabNetto());
        assertEquals(EXPECTED_C_PO_RAB_BRUTTO, p.cPoRabBrutto());
        assertEquals(EXPECTED_RABAT_KWOTA, p.rabatKwota());
        assertEquals(EXPECTED_W_NETTO, p.wNetto());
        assertEquals(EXPECTED_W_PODATEK, p.wPodatek());
        assertEquals(EXPECTED_W_BRUTTO, p.wBrutto());
        assertEquals(EXPECTED_ROZL_NETTO, p.rozlNetto());
        assertEquals(EXPECTED_ROZL_PODATEK, p.rozlPodatek());
        assertEquals(EXPECTED_ROZL_BRUTTO, p.rozlBrutto());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int count = client.pozdok().count(null);

        // then
        assertEquals(EXPECTED_LIST_SIZE, count);
    }

    @Test
    void getById_whenRecordExists_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(TestClients.jsonFile(SINGLE_FILE)));

        // when
        PozycjaDokumentu p = client.pozdok().getById(EXPECTED_ID);

        // then
        assertNotNull(p);
        assertEquals(EXPECTED_ID, p.id());
        assertNotNull(p.dokumentId());
        assertNotNull(p.towarId());
        assertEquals(EXPECTED_NR_POZYCJI, p.nrPozycji());
        assertEquals(EXPECTED_ILOSC, p.ilosc());
        assertEquals(EXPECTED_VAT, p.stawkaVat());
        assertEquals(EXPECTED_C_PRZED_RAB_NETTO, p.cPrzedRabNetto());
        assertEquals(EXPECTED_C_PRZED_RAB_BRUTTO, p.cPrzedRabBrutto());
        assertEquals(EXPECTED_C_PO_RAB_NETTO, p.cPoRabNetto());
        assertEquals(EXPECTED_C_PO_RAB_BRUTTO, p.cPoRabBrutto());
        assertEquals(EXPECTED_RABAT_KWOTA, p.rabatKwota());
        assertEquals(EXPECTED_W_NETTO, p.wNetto());
        assertEquals(EXPECTED_W_PODATEK, p.wPodatek());
        assertEquals(EXPECTED_W_BRUTTO, p.wBrutto());
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
        var resource = client.pozdok();
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
        var resource = client.pozdok();
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
        var resource = client.pozdok();
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
        var resource = client.pozdok();
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
                    "self": "http://localhost:%d/demo/pozdok?start=0",
                    "next": "http://localhost:%d/demo/pozdok?start=2"
                  },
                  "dane": [{"id": 1, "nr_pozycji": 1}, {"id": 2, "nr_pozycji": 2}]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/pozdok?start=2"
                  },
                  "dane": [{"id": 3, "nr_pozycji": 3}]
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
        java.util.List<PozycjaDokumentu> all = new java.util.ArrayList<>();
        client.pozdok().list(null).forEach(all::add);

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
        assertDoesNotThrow(() -> retryClient.pozdok().count(null));
        verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }
}
