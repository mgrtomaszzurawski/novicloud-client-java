/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.client.model.StawkaVatRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.StawkaVat;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudAuthException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudRateLimitException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudServerException;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat.StawkaVatCreateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class StawkiVatClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/stawkivat";
    private static final String URL_BY_ID = "/[^/]+/stawkivat/[^/]+";
    private static final String LIST_FILE = "stawkivat/list.json";
    private static final String SINGLE_FILE = "stawkivat/single.json";
    private static final int EXPECTED_LIST_SIZE = 5;
    private static final int EXPECTED_EXEMPT_ID = -1;
    private static final int EXPECTED_ZERO_ID = 0;
    private static final int EXPECTED_23PCT_ID = 2300;
    private static final int EXPECTED_LAST_INDEX = 4;

    // -- expected field values --
    private static final String EXPECTED_OPIS_EXEMPT = "exempt";
    private static final String EXPECTED_OPIS_23PCT = "23 pct";

    // -- create / delete test data --
    private static final int CREATE_VAT_ID = 9999;
    private static final String CREATE_OPIS = "test rate";
    private static final String CREATE_ETYKIETA = "G";
    private static final String EXPECTED_CREATED_ID = "9999";
    private static final long DELETE_ID = 9999L;
    private static final int EXPECTED_RETRY_VERIFY_COUNT = 2;

    private NoviCloudClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        client = TestClients.create(wm);
    }

    // -----------------------------------------------------------------------
    // Basic happy-path tests
    // -----------------------------------------------------------------------

    @Test
    void list_whenServerReturnsData_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        List<StawkaVat> dane = new ArrayList<>();
        client.stawkiVat().list(null).forEach(dane::add);

        // then
        assertNotNull(dane);
        assertEquals(EXPECTED_LIST_SIZE, dane.size());

        StawkaVat exempt = dane.get(FIRST_INDEX);
        assertEquals(EXPECTED_EXEMPT_ID, exempt.id());
        assertEquals(StawkaVat.Etykieta.D, exempt.etykieta());
        assertEquals(EXPECTED_OPIS_EXEMPT, exempt.opis());

        StawkaVat pct23 = dane.get(EXPECTED_LAST_INDEX);
        assertEquals(EXPECTED_23PCT_ID, pct23.id());
        assertEquals(StawkaVat.Etykieta.A, pct23.etykieta());
        assertEquals(EXPECTED_OPIS_23PCT, pct23.opis());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int count = client.stawkiVat().count(null);

        // then
        assertEquals(EXPECTED_LIST_SIZE, count);
    }

    @Test
    void getById_whenRecordExists_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(TestClients.jsonFile(SINGLE_FILE)));

        // when
        StawkaVat sv = client.stawkiVat().getById((long) EXPECTED_23PCT_ID);

        // then
        assertNotNull(sv);
        assertEquals(EXPECTED_23PCT_ID, sv.id());
        assertEquals(StawkaVat.Etykieta.A, sv.etykieta());
        assertEquals(EXPECTED_OPIS_23PCT, sv.opis());
    }

    @Test
    void getById_whenServerReturnsUnknownEtykietaEnum_returnsRecordWithNullEtykieta() {
        // given - CF-04: unknown etykieta letter (e.g. "Z") must not break deserialization
        String json = "{\"status\":200,\"status_opis\":\"Ok\",\"dane\":{"
                + "\"id\":2300,\"opis\":\"23%\",\"etykieta\":\"Z\"}}";
        stubFor(get(urlPathMatching(URL_BY_ID)).willReturn(okJson(json)));

        // when
        StawkaVat sv = client.stawkiVat().getById((long) EXPECTED_23PCT_ID);

        // then
        assertNotNull(sv);
        assertNull(sv.etykieta(), "unknown etykieta code must map to null, not throw");
    }

    @Test
    void create_whenServerAccepts_returnsCreatedId() {
        // given
        stubFor(post(urlPathMatching(URL_LIST))
                .willReturn(aResponse()
                        .withStatus(HTTP_CREATED)
                        .withHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .withBody(TestClients.CREATED_JSON)));

        // when
        StawkaVatCreateBuilder draft = StawkaVatCreateBuilder.builder(CREATE_VAT_ID)
                .opis(CREATE_OPIS)
                .etykieta(CREATE_ETYKIETA)
                .build();
        String id = client.stawkiVat().create(draft);

        // then
        assertEquals(EXPECTED_CREATED_ID, id);
        verify(SINGLE_REQUEST, postRequestedFor(urlPathMatching(URL_LIST)));
    }

    @Test
    void deleteById_whenRecordExists_completesWithoutError() {
        // given
        stubFor(delete(urlPathMatching(URL_BY_ID))
                .willReturn(okJson(TestClients.OK_JSON)));

        // when / then
        assertDoesNotThrow(() -> client.stawkiVat().deleteById(DELETE_ID));
        verify(SINGLE_REQUEST, deleteRequestedFor(urlPathMatching(URL_BY_ID)));
    }

    // -----------------------------------------------------------------------
    // Retry (4-point plan #1)
    // -----------------------------------------------------------------------

    @Test
    void list_whenFirstCallReturns500_retriesAndReturnsData(WireMockRuntimeInfo wm) {
        // given
        NoviCloudClient retryClient = TestClients.withRetry(wm);

        stubFor(get(urlPathMatching(URL_LIST))
                .inScenario(SCENARIO_RETRY)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(serverError())
                .willSetStateTo(SCENARIO_STATE_RECOVERED));

        stubFor(get(urlPathMatching(URL_LIST))
                .inScenario(SCENARIO_RETRY)
                .whenScenarioStateIs(SCENARIO_STATE_RECOVERED)
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        List<StawkaVat> items = new ArrayList<>();
        retryClient.stawkiVat().list(null).forEach(items::add);

        // then
        assertNotNull(items);
        assertEquals(EXPECTED_LIST_SIZE, items.size());
        verify(EXPECTED_RETRY_VERIFY_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }

    // -----------------------------------------------------------------------
    // Exception hierarchy (4-point plan #2)
    // -----------------------------------------------------------------------

    @Test
    void count_whenServerReturns401_throwsAuthException() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(unauthorized()));

        // when / then
        var resource = client.stawkiVat();
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
        var resource = client.stawkiVat();
        NoviCloudNotFoundException ex = assertThrows(NoviCloudNotFoundException.class,
                () -> resource.getById(NON_EXISTENT_ID));
        assertEquals(HTTP_NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getById_whenServerReturns200WithNullDane_throwsNotFoundException() {
        // given - hard-delete endpoint may return HTTP 200 with dane=null per ADR-033
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(okJson("{\"status\":200,\"status_opis\":\"Ok\",\"dane\":null}")));

        // when / then
        var resource = client.stawkiVat();
        NoviCloudNotFoundException ex = assertThrows(NoviCloudNotFoundException.class,
                () -> resource.getById(NON_EXISTENT_ID));
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
        var resource = client.stawkiVat();
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
        var resource = client.stawkiVat();
        NoviCloudServerException ex = assertThrows(NoviCloudServerException.class,
                () -> resource.count(null));
        assertEquals(HTTP_SERVER_ERROR, ex.getStatusCode());
    }

    // -----------------------------------------------------------------------
    // Pagination (4-point plan #4)
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
                    "self": "http://localhost:%d/demo/stawkivat?start=0",
                    "next": "http://localhost:%d/demo/stawkivat?start=2"
                  },
                  "dane": [
                    { "id": -1, "etykieta": "D", "opis": "exempt" },
                    { "id": 0,  "etykieta": "D", "opis": "zero" }
                  ]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/stawkivat?start=2"
                  },
                  "dane": [
                    { "id": 2300, "etykieta": "A", "opis": "23 pct" }
                  ]
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
        List<StawkaVat> all = new ArrayList<>();
        client.stawkiVat().list(null).forEach(all::add);

        // then
        assertEquals(EXPECTED_PAGINATION_SIZE, all.size());
        assertEquals(EXPECTED_EXEMPT_ID, all.get(FIRST_INDEX).id());
        assertEquals(EXPECTED_ZERO_ID, all.get(SECOND_INDEX).id());
        assertEquals(EXPECTED_23PCT_ID, all.get(THIRD_INDEX).id());
    }
}
