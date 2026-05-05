/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Sprzedaz;
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
class SprzedazClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/sprzedaz";
    private static final String URL_BY_ID = "/[^/]+/sprzedaz/[^/]+";
    private static final String LIST_FILE = "sprzedaz/list.json";
    private static final String SINGLE_FILE = "sprzedaz/single.json";

    private static final int EXPECTED_SIZE = 1;
    private static final long EXPECTED_ID = 4000001L;
    private static final int TYP_PARAGON = 21;
    private static final int VAT_EXEMPT = -1;

    // -- expected field values --
    private static final String EXPECTED_NR_DOK = "PAR/001/2026";
    private static final String EXPECTED_NR_SYSTEMOWY = "SYS-001";
    private static final String EXPECTED_NR_FISKALNY = "FISK-001";
    private static final String EXPECTED_NR_RAP_DOB = "RAP-001";
    private static final double EXPECTED_ILOSC = 2.0;
    private static final double EXPECTED_CENA = 7.15;
    private static final double EXPECTED_CENA_PRZED_RAB = 8.00;
    private static final double EXPECTED_BRUTTO = 14.30;
    private static final double EXPECTED_PODATEK = 0.0;
    private static final double EXPECTED_RABAT = 1.70;
    private static final String EXPECTED_TOWAR_ID = "294";
    private static final String EXPECTED_SKLEP_ID = "1";
    private static final String EXPECTED_KASA_ID = "1";
    private static final String EXPECTED_KASJER_ID = "2";
    private static final int EXPECTED_PLATNOSCI_COUNT = 1;
    private static final String EXPECTED_PLATNOSC_KOD_WALUTY = "PLN";
    private static final double EXPECTED_PLATNOSC_KWOTA = 14.3;

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
        List<Sprzedaz> dane = new ArrayList<>();
        client.sprzedaz().list(null).forEach(dane::add);

        // then
        assertEquals(EXPECTED_SIZE, dane.size());

        Sprzedaz s = dane.get(FIRST_INDEX);
        assertEquals(EXPECTED_ID, s.id());
        assertNotNull(s.data());
        assertEquals(EXPECTED_NR_DOK, s.nrDok());
        assertEquals(TYP_PARAGON, s.typDok());
        assertEquals(EXPECTED_NR_SYSTEMOWY, s.nrSystemowy());
        assertEquals(EXPECTED_NR_FISKALNY, s.nrFiskalny());
        assertEquals(EXPECTED_NR_RAP_DOB, s.nrRapDob());
        assertEquals(EXPECTED_ILOSC, s.ilosc());
        assertEquals(EXPECTED_CENA, s.cena());
        assertEquals(EXPECTED_CENA_PRZED_RAB, s.cenaPrzedRab());
        assertEquals(VAT_EXEMPT, s.stawkaVat());
        assertEquals(EXPECTED_BRUTTO, s.brutto());
        assertEquals(EXPECTED_PODATEK, s.podatek());
        assertEquals(EXPECTED_RABAT, s.rabat());
        assertEquals(EXPECTED_TOWAR_ID, s.towarId());
        assertEquals(EXPECTED_SKLEP_ID, s.sklepId());
        assertEquals(EXPECTED_KASA_ID, s.kasaId());
        assertEquals(EXPECTED_KASJER_ID, s.kasjerId());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int count = client.sprzedaz().count(null);

        // then
        assertEquals(EXPECTED_SIZE, count);
    }

    @Test
    void getById_whenRecordExists_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(TestClients.jsonFile(SINGLE_FILE)));

        // when
        Sprzedaz s = client.sprzedaz().getById(EXPECTED_ID);

        // then
        assertNotNull(s);
        assertEquals(EXPECTED_ID, s.id());
        assertNotNull(s.data());
        assertEquals(EXPECTED_NR_DOK, s.nrDok());
        assertEquals(TYP_PARAGON, s.typDok());
        assertEquals(EXPECTED_NR_SYSTEMOWY, s.nrSystemowy());
        assertEquals(EXPECTED_ILOSC, s.ilosc());
        assertEquals(EXPECTED_CENA, s.cena());
        assertEquals(VAT_EXEMPT, s.stawkaVat());
        assertEquals(EXPECTED_BRUTTO, s.brutto());
        assertEquals(EXPECTED_PODATEK, s.podatek());
        assertNotNull(s.towarId());
        assertNotNull(s.sklepId());
        assertEquals(EXPECTED_PLATNOSCI_COUNT, s.platnosci().size());
        assertEquals(EXPECTED_PLATNOSC_KOD_WALUTY, s.platnosci().get(FIRST_INDEX).kodWaluty());
        assertEquals(EXPECTED_PLATNOSC_KWOTA, s.platnosci().get(FIRST_INDEX).kwota());
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
        var resource = client.sprzedaz();
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
        var resource = client.sprzedaz();
        NoviCloudNotFoundException ex = assertThrows(NoviCloudNotFoundException.class,
                () -> resource.count(null));
        assertEquals(HTTP_NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getById_whenServerReturns200WithNullDane_throwsNotFoundException() {
        // given - read-only endpoint may return HTTP 200 with dane=null when no record matches
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(okJson("{\"status\":200,\"status_opis\":\"Ok\",\"dane\":null}")));

        // when / then
        var resource = client.sprzedaz();
        NoviCloudNotFoundException ex = assertThrows(NoviCloudNotFoundException.class,
                () -> resource.getById(EXPECTED_ID));
        assertEquals(HTTP_OK, ex.getStatusCode());
    }

    @Test
    void count_whenServerReturns429_throwsRateLimitException() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(aResponse()
                        .withStatus(HTTP_RATE_LIMITED)
                        .withHeader(RETRY_AFTER_HEADER, RETRY_AFTER_SECONDS)));

        // when / then
        var resource = client.sprzedaz();
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
        var resource = client.sprzedaz();
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
                    "self": "http://localhost:%d/demo/sprzedaz?start=0",
                    "next": "http://localhost:%d/demo/sprzedaz?start=2"
                  },
                  "dane": [{"id": 1, "nr_dok": "P1"}, {"id": 2, "nr_dok": "P2"}]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/sprzedaz?start=2"
                  },
                  "dane": [{"id": 3, "nr_dok": "P3"}]
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
        java.util.List<Sprzedaz> all = new java.util.ArrayList<>();
        client.sprzedaz().list(null).forEach(all::add);

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
        assertDoesNotThrow(() -> retryClient.sprzedaz().count(null));
        verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }
}
