/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.KartaLojalnosciowa;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudAuthException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudRateLimitException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudServerException;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj.KartaLojCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj.KartaLojUpdateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class KartyLojClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/f-karty-loj";
    private static final String URL_BY_ID = "/[^/]+/f-karty-loj/[^/]+";
    private static final String LIST_FILE = "kartyloj/list.json";
    private static final String SINGLE_FILE = "kartyloj/single.json";
    private static final int EXPECTED_SIZE = 1;
    private static final int EXPECTED_TYP = 1;

    // Expected field values
    private static final String EXPECTED_KOD = "LOJ-001";
    private static final String EXPECTED_POSIADACZ = "Jan Kowalski";
    private static final String EXPECTED_NAZWISKO_IMIE = "Jan Kowalski";
    private static final String EXPECTED_TELEFON = "500100200";
    private static final String EXPECTED_EMAIL = "jan@example.com";

    // Create/update test values
    private static final String CREATE_KOD = "LOJ-002";
    private static final String CREATE_NAZWISKO_IMIE = "Anna Nowak";
    private static final String CREATE_TELEFON = "600300400";
    private static final String CREATE_EMAIL = "anna@example.com";
    private static final String CREATE_PLEC = "K";
    private static final String EXPECTED_CREATED_ID = "9999";
    private static final String UPDATE_TELEFON = "111222333";

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
        List<KartaLojalnosciowa> dane = new ArrayList<>();
        client.kartyLoj().list(null).forEach(dane::add);

        // then
        assertEquals(EXPECTED_SIZE, dane.size());

        KartaLojalnosciowa kl = dane.get(FIRST_INDEX);
        assertEquals(EXPECTED_KOD, kl.kod());
        assertEquals(EXPECTED_TYP, kl.typ());
        assertNotNull(kl.waznaOd());
        assertNotNull(kl.waznaDo());
        assertEquals(EXPECTED_POSIADACZ, kl.posiadacz());
        assertEquals(EXPECTED_NAZWISKO_IMIE, kl.nazwiskoImie());
        assertEquals(EXPECTED_TELEFON, kl.telefon());
        assertEquals(EXPECTED_EMAIL, kl.email());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int result = client.kartyLoj().count(null);

        // then
        assertEquals(EXPECTED_SIZE, result);
    }

    @Test
    void getByKod_whenRecordExists_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(TestClients.jsonFile(SINGLE_FILE)));

        // when
        KartaLojalnosciowa kl = client.kartyLoj().getByKod(EXPECTED_KOD);

        // then
        assertNotNull(kl);
        assertEquals(EXPECTED_KOD, kl.kod());
        assertEquals(EXPECTED_TYP, kl.typ());
        assertEquals(EXPECTED_NAZWISKO_IMIE, kl.nazwiskoImie());
        assertEquals(EXPECTED_TELEFON, kl.telefon());
        assertEquals(EXPECTED_EMAIL, kl.email());
    }

    @Test
    void create_whenServerAccepts_returnsCreatedId() {
        // given
        stubFor(post(urlPathMatching(URL_LIST))
                .willReturn(aResponse().withStatus(HTTP_CREATED)
                        .withHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .withBody(TestClients.CREATED_JSON)));

        // when
        String id = client.kartyLoj().create(
                KartaLojCreateBuilder.builder(CREATE_KOD)
                        .typ(EXPECTED_TYP)
                        .nazwiskoImie(CREATE_NAZWISKO_IMIE)
                        .telefon(CREATE_TELEFON)
                        .email(CREATE_EMAIL)
                        .plec(CREATE_PLEC)
                        .build());

        // then
        assertEquals(EXPECTED_CREATED_ID, id);
        verify(SINGLE_REQUEST, postRequestedFor(urlPathMatching(URL_LIST)));
    }

    @Test
    void update_whenRecordExists_completesWithoutError() {
        // given
        stubFor(put(urlPathMatching(URL_LIST))
                .willReturn(okJson(TestClients.OK_JSON)));

        // when / then
        assertDoesNotThrow(() -> client.kartyLoj().update(
                KartaLojUpdateBuilder.builder(EXPECTED_KOD)
                        .telefon(UPDATE_TELEFON)
                        .build()));
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
        var resource = client.kartyLoj();
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
        var resource = client.kartyLoj();
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
        var resource = client.kartyLoj();
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
        var resource = client.kartyLoj();
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
                    "self": "http://localhost:%d/demo/f-karty-loj?start=0",
                    "next": "http://localhost:%d/demo/f-karty-loj?start=2"
                  },
                  "dane": [{"kod": "K1"}, {"kod": "K2"}]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/f-karty-loj?start=2"
                  },
                  "dane": [{"kod": "K3"}]
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
        java.util.List<KartaLojalnosciowa> all = new java.util.ArrayList<>();
        client.kartyLoj().list(null).forEach(all::add);

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
        assertDoesNotThrow(() -> retryClient.kartyLoj().count(null));
        verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }
}
