/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.StanMag;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudAuthException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudRateLimitException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudServerException;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stanymag.StanMagUpdateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class StanyMagClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/stanymag";
    private static final String URL_LIST_EXACT = "/[^/]+/stanymag$";
    private static final String URL_BY_TOWAR = "/[^/]+/stanymag/[^/]+$";
    private static final String URL_BY_TOWAR_AND_SKLEP = "/[^/]+/stanymag/[^/]+/[^/]+$";
    private static final String BY_TOWAR_AND_SKLEP_FILE = "stanymag/by-towar-and-sklep.json";
    private static final String BY_TOWAR_FILE = "stanymag/by-towar.json";
    private static final String LIST_FILE = "stanymag/list.json";
    private static final int EXPECTED_SIZE = 2;

    // -- expected field values --
    private static final long EXPECTED_TOWAR_ID = 1L;
    private static final long EXPECTED_SKLEP_ID = 1L;
    private static final double EXPECTED_ILOSC = 15.0;
    private static final double EXPECTED_ZAK_NETTO = 10.0;
    private static final double EXPECTED_ZAK_BRUTTO = 12.30;
    private static final double EXPECTED_SPRZED_NETTO = 20.0;
    private static final double EXPECTED_SPRZED_BRUTTO = 24.60;

    // -- update test data --
    private static final String UPDATE_TOWAR_ID = "1";
    private static final String UPDATE_SKLEP_ID = "1";
    private static final double UPDATE_ILOSC = 25.0;

    private NoviCloudClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        client = TestClients.create(wm);
    }

    @Test
    void list_whenServerReturnsData_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_LIST_EXACT))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        List<StanMag> dane = new ArrayList<>();
        client.stanyMag().list(null).forEach(dane::add);

        // then
        assertEquals(EXPECTED_SIZE, dane.size());

        StanMag sm = dane.get(FIRST_INDEX);
        assertNotNull(sm.towarId());
        assertNotNull(sm.sklepId());
        assertEquals(EXPECTED_ILOSC, sm.ilosc());
        assertEquals(EXPECTED_ZAK_NETTO, sm.wCZakNetto());
        assertEquals(EXPECTED_ZAK_BRUTTO, sm.wCZakBrutto());
        assertEquals(EXPECTED_SPRZED_NETTO, sm.wCSprzedNetto());
        assertEquals(EXPECTED_SPRZED_BRUTTO, sm.wCSprzedBrutto());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST_EXACT))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int count = client.stanyMag().count(null);

        // then
        assertEquals(EXPECTED_SIZE, count);
    }

    @Test
    void listByTowar_whenTowarExists_deserializesAllRecords() {
        // given
        stubFor(get(urlPathMatching(URL_BY_TOWAR))
                .willReturn(TestClients.jsonFile(BY_TOWAR_FILE)));

        // when
        List<StanMag> dane = client.stanyMag().listByTowar(EXPECTED_TOWAR_ID, null);

        // then
        assertEquals(EXPECTED_SIZE, dane.size());
        assertNotNull(dane.get(FIRST_INDEX).towarId());
        assertNotNull(dane.get(FIRST_INDEX).sklepId());
        assertNotNull(dane.get(SECOND_INDEX).sklepId());
    }

    @Test
    void getByTowarAndSklep_whenRecordExists_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_BY_TOWAR_AND_SKLEP))
                .willReturn(TestClients.jsonFile(BY_TOWAR_AND_SKLEP_FILE)));

        // when
        StanMag sm = client.stanyMag().getByTowarAndSklep(EXPECTED_TOWAR_ID, EXPECTED_SKLEP_ID, null);

        // then
        assertNotNull(sm);
        assertNotNull(sm.towarId());
        assertNotNull(sm.sklepId());
        assertEquals(EXPECTED_ILOSC, sm.ilosc());
    }

    @Test
    void update_doesNotThrow() {
        // given
        stubFor(put(urlPathMatching(URL_LIST))
                .willReturn(okJson(TestClients.OK_JSON)));

        // when / then
        assertDoesNotThrow(() -> client.stanyMag().update(
                StanMagUpdateBuilder.builder(UPDATE_TOWAR_ID, UPDATE_SKLEP_ID, UPDATE_ILOSC).build()));
        verify(SINGLE_REQUEST, putRequestedFor(urlPathMatching(URL_LIST)));
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
        var resource = client.stanyMag();
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
        var resource = client.stanyMag();
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
        var resource = client.stanyMag();
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
        var resource = client.stanyMag();
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
                    "self": "http://localhost:%d/demo/stanymag?start=0",
                    "next": "http://localhost:%d/demo/stanymag?start=2"
                  },
                  "dane": [{"ilosc": 10.0}, {"ilosc": 20.0}]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/stanymag?start=2"
                  },
                  "dane": [{"ilosc": 30.0}]
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
        java.util.List<StanMag> all = new java.util.ArrayList<>();
        client.stanyMag().list(null).forEach(all::add);

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
        assertDoesNotThrow(() -> retryClient.stanyMag().count(null));
        verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }
}
