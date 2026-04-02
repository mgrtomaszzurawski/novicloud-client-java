/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Sklep;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudAuthException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudRateLimitException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudServerException;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy.SklepCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy.SklepUpdateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class SklepyClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/sklepy";
    private static final String URL_BY_ID = "/[^/]+/sklepy/[^/]+";
    private static final String LIST_FILE = "sklepy/list.json";
    private static final String SINGLE_FILE = "sklepy/single.json";

    private static final int EXPECTED_SIZE = 1;
    private static final long EXPECTED_ID = 1L;
    private static final int EXPECTED_NUMER = 1;

    // -- expected field values --
    private static final String EXPECTED_NIP = "0000000000";
    private static final String EXPECTED_NAZWA = "Main Store";
    private static final String EXPECTED_SKROT = "MAIN";
    private static final String EXPECTED_ULICA = "Market Square";
    private static final String EXPECTED_NR_DOMU = "1";
    private static final String EXPECTED_NR_LOKALU = "1";
    private static final String EXPECTED_ULICA_I_NUMER = "Market Square 1/1";
    private static final String EXPECTED_KOD_POCZT = "00-000";
    private static final String EXPECTED_MIASTO = "Warsaw";

    // -- create / update test data --
    private static final String CREATE_NAZWA = "Branch Store";
    private static final int CREATE_NUMER = 2;
    private static final String CREATE_NIP = "1111111111";
    private static final String CREATE_SKROT = "BRANCH";
    private static final String CREATE_ULICA = "Side Street";
    private static final String CREATE_NR_DOMU = "5";
    private static final String CREATE_KOD_POCZT = "30-001";
    private static final String CREATE_MIASTO = "Krakow";
    private static final String EXPECTED_CREATED_ID = "9999";

    private static final String UPDATE_NAZWA = "Updated Store";
    private static final String UPDATE_TELEFON = "111222333";
    private static final String UPDATE_EMAIL = "store@example.com";

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
        List<Sklep> dane = new ArrayList<>();
        client.sklepy().list(null).forEach(dane::add);

        // then
        assertEquals(EXPECTED_SIZE, dane.size());

        Sklep s = dane.get(FIRST_INDEX);
        assertEquals(EXPECTED_ID, s.id());
        assertEquals(EXPECTED_NIP, s.nip());
        assertEquals(EXPECTED_NAZWA, s.nazwa());
        assertEquals(EXPECTED_SKROT, s.skrot());
        assertEquals(EXPECTED_NUMER, s.numer());
        assertEquals(EXPECTED_ULICA, s.ulica());
        assertEquals(EXPECTED_NR_DOMU, s.nrDomu());
        assertEquals(EXPECTED_NR_LOKALU, s.nrLokalu());
        assertEquals(EXPECTED_ULICA_I_NUMER, s.ulicaINumer());
        assertEquals(EXPECTED_KOD_POCZT, s.kodPoczt());
        assertEquals(EXPECTED_MIASTO, s.poczta());
        assertEquals(EXPECTED_MIASTO, s.miasto());
        assertTrue(s.aktywny());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int count = client.sklepy().count(null);

        // then
        assertEquals(EXPECTED_SIZE, count);
    }

    @Test
    void getById_whenRecordExists_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(TestClients.jsonFile(SINGLE_FILE)));

        // when
        Sklep s = client.sklepy().getById(EXPECTED_ID);

        // then
        assertNotNull(s);
        assertEquals(EXPECTED_ID, s.id());
        assertEquals(EXPECTED_NIP, s.nip());
        assertEquals(EXPECTED_NAZWA, s.nazwa());
        assertEquals(EXPECTED_SKROT, s.skrot());
        assertEquals(EXPECTED_NUMER, s.numer());
        assertEquals(EXPECTED_ULICA, s.ulica());
        assertEquals(EXPECTED_NR_DOMU, s.nrDomu());
        assertEquals(EXPECTED_NR_LOKALU, s.nrLokalu());
        assertEquals(EXPECTED_KOD_POCZT, s.kodPoczt());
        assertEquals(EXPECTED_MIASTO, s.miasto());
        assertTrue(s.aktywny());
    }

    @Test
    void create_whenServerAccepts_returnsCreatedId() {
        // given
        stubFor(post(urlPathMatching(URL_LIST))
                .willReturn(aResponse().withStatus(HTTP_CREATED)
                        .withHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .withBody(TestClients.CREATED_JSON)));

        // when
        String id = client.sklepy().create(
                SklepCreateBuilder.builder(CREATE_NAZWA, CREATE_NUMER)
                        .nip(CREATE_NIP)
                        .skrot(CREATE_SKROT)
                        .ulica(CREATE_ULICA)
                        .nrDomu(CREATE_NR_DOMU)
                        .kodPoczt(CREATE_KOD_POCZT)
                        .miasto(CREATE_MIASTO)
                        .aktywny(true)
                        .build());

        // then
        assertEquals(EXPECTED_CREATED_ID, id);
        verify(SINGLE_REQUEST, postRequestedFor(urlPathMatching(URL_LIST)));
    }

    @Test
    void update_doesNotThrow() {
        // given
        stubFor(put(urlPathMatching(URL_LIST))
                .willReturn(okJson(TestClients.OK_JSON)));

        // when / then
        assertDoesNotThrow(() -> client.sklepy().update(
                SklepUpdateBuilder.builder(EXPECTED_ID)
                        .nazwa(UPDATE_NAZWA)
                        .telefon(UPDATE_TELEFON)
                        .email(UPDATE_EMAIL)
                        .build()));
        verify(SINGLE_REQUEST, putRequestedFor(urlPathMatching(URL_LIST)));
    }

    @Test
    void deleteById_whenRecordExists_completesWithoutError() {
        // given
        stubFor(delete(urlPathMatching(URL_BY_ID))
                .willReturn(okJson(TestClients.OK_JSON)));

        // when / then
        assertDoesNotThrow(() -> client.sklepy().deleteById(EXPECTED_ID));
        verify(SINGLE_REQUEST, deleteRequestedFor(urlPathMatching(URL_BY_ID)));
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
        var resource = client.sklepy();
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
        var resource = client.sklepy();
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
        var resource = client.sklepy();
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
        var resource = client.sklepy();
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
                    "self": "http://localhost:%d/demo/sklepy?start=0",
                    "next": "http://localhost:%d/demo/sklepy?start=2"
                  },
                  "dane": [{"id": 1, "nazwa": "S1"}, {"id": 2, "nazwa": "S2"}]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/sklepy?start=2"
                  },
                  "dane": [{"id": 3, "nazwa": "S3"}]
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
        java.util.List<Sklep> all = new java.util.ArrayList<>();
        client.sklepy().list(null).forEach(all::add);

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
        assertDoesNotThrow(() -> retryClient.sklepy().count(null));
        verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }
}
