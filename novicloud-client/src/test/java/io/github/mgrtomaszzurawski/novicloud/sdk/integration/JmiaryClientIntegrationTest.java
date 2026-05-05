/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.client.model.JmiaryRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Jmiary;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudAuthException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudRateLimitException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudServerException;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary.JmiaryCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary.JmiaryUpdateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class JmiaryClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/jmiary";
    private static final String URL_BY_ID = "/[^/]+/jmiary/[^/]+";
    private static final String LIST_FILE = "jmiary/list.json";
    private static final String SINGLE_FILE = "jmiary/single.json";
    private static final int EXPECTED_SIZE = 2;
    private static final long EXPECTED_FIRST_ID = 1L;
    private static final int UPDATE_PRECYZJA = 0;

    // Expected field values
    private static final String EXPECTED_NAZWA_SZT = "szt";
    private static final String EXPECTED_NAZWA_KG = "kg";
    private static final long EXPECTED_KG_ID = 2L;
    private static final String CREATE_NAZWA = "litr";
    private static final int CREATE_PRECYZJA = 2;
    private static final String EXPECTED_CREATED_ID = "9999";
    private static final String UPDATE_NAZWA = "pieces";

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
        List<Jmiary> dane = new ArrayList<>();
        client.jmiary().list(null).forEach(dane::add);

        // then
        assertEquals(EXPECTED_SIZE, dane.size());

        Jmiary szt = dane.get(FIRST_INDEX);
        assertEquals(EXPECTED_FIRST_ID, szt.id());
        assertEquals(EXPECTED_NAZWA_SZT, szt.nazwa());
        assertEquals(Jmiary.Precyzja.VALUE_0, szt.precyzja());

        Jmiary kg = dane.get(SECOND_INDEX);
        assertEquals(EXPECTED_KG_ID, kg.id());
        assertEquals(EXPECTED_NAZWA_KG, kg.nazwa());
        assertEquals(Jmiary.Precyzja.VALUE_3, kg.precyzja());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int result = client.jmiary().count(null);

        // then
        assertEquals(EXPECTED_SIZE, result);
    }

    @Test
    void getById_whenRecordExists_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(TestClients.jsonFile(SINGLE_FILE)));

        // when
        Jmiary j = client.jmiary().getById(EXPECTED_FIRST_ID);

        // then
        assertNotNull(j);
        assertEquals(EXPECTED_FIRST_ID, j.id());
        assertEquals(EXPECTED_NAZWA_SZT, j.nazwa());
        assertEquals(Jmiary.Precyzja.VALUE_0, j.precyzja());
    }

    @Test
    void getById_whenServerReturnsUnknownPrecyzjaEnum_returnsRecordWithNullPrecyzja() {
        // given - CF-04: producer-introduced precyzja value (99) must not break deserialization
        String json = "{\"status\":200,\"status_opis\":\"Ok\",\"dane\":{"
                + "\"id\":1,\"nazwa\":\"sztuka\",\"precyzja\":99}}";
        stubFor(get(urlPathMatching(URL_BY_ID)).willReturn(okJson(json)));

        // when
        Jmiary j = client.jmiary().getById(EXPECTED_FIRST_ID);

        // then
        assertNotNull(j);
        assertNull(j.precyzja(), "unknown precyzja code must map to null, not throw");
    }

    @Test
    void create_whenServerAccepts_returnsCreatedId() {
        // given
        stubFor(post(urlPathMatching(URL_LIST))
                .willReturn(aResponse().withStatus(HTTP_CREATED)
                        .withHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .withBody(TestClients.CREATED_JSON)));

        // when
        String id = client.jmiary().create(
                JmiaryCreateBuilder.builder(CREATE_NAZWA).precyzja(CREATE_PRECYZJA).build());

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
        assertDoesNotThrow(() -> client.jmiary().update(
                JmiaryUpdateBuilder.builder(EXPECTED_FIRST_ID).nazwa(UPDATE_NAZWA).precyzja(UPDATE_PRECYZJA).build()));
        verify(SINGLE_REQUEST, putRequestedFor(urlPathMatching(URL_LIST)));
    }

    @Test
    void deleteById_whenRecordExists_completesWithoutError() {
        // given
        stubFor(delete(urlPathMatching(URL_BY_ID))
                .willReturn(okJson(TestClients.OK_JSON)));

        // when / then
        assertDoesNotThrow(() -> client.jmiary().deleteById(EXPECTED_FIRST_ID));
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
        var resource = client.jmiary();
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
        var resource = client.jmiary();
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
        var resource = client.jmiary();
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
        var resource = client.jmiary();
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
                    "self": "http://localhost:%d/demo/jmiary?start=0",
                    "next": "http://localhost:%d/demo/jmiary?start=2"
                  },
                  "dane": [{"id": 1, "nazwa": "szt"}, {"id": 2, "nazwa": "kg"}]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/jmiary?start=2"
                  },
                  "dane": [{"id": 3, "nazwa": "l"}]
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
        java.util.List<Jmiary> all = new java.util.ArrayList<>();
        client.jmiary().list(null).forEach(all::add);

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
        assertDoesNotThrow(() -> retryClient.jmiary().count(null));
        verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }
}
