/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.client.model.FormaPlatnRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.FormaPlatn;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudAuthException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudRateLimitException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudServerException;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn.FormaPlatnCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn.FormaPlatnUpdateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class FormyPlatnClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/formyplatn";
    private static final String URL_BY_ID = "/[^/]+/formyplatn/[^/]+";
    private static final String LIST_FILE = "formyplatn/list.json";
    private static final String SINGLE_FILE = "formyplatn/single.json";
    private static final int EXPECTED_SIZE = 3;
    private static final long EXPECTED_FIRST_ID = 1L;

    // Expected field values
    private static final String EXPECTED_NAZWA_CASH = "Cash";
    private static final String EXPECTED_NAZWA_CARD = "Credit Card";
    private static final long EXPECTED_CARD_ID = 2L;
    private static final String CREATE_NAZWA = "Voucher";
    private static final int CREATE_TYP = 4;
    private static final String EXPECTED_CREATED_ID = "9999";
    private static final String UPDATE_NAZWA = "Updated Cash";

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
        List<FormaPlatn> dane = new ArrayList<>();
        client.formyPlatn().list(null).forEach(dane::add);

        // then
        assertEquals(EXPECTED_SIZE, dane.size());

        FormaPlatn cash = dane.get(FIRST_INDEX);
        assertEquals(EXPECTED_FIRST_ID, cash.id());
        assertEquals(EXPECTED_NAZWA_CASH, cash.nazwa());
        assertEquals(FormaPlatn.Typ.VALUE_0, cash.typ());
        assertTrue(cash.reszta());
        assertTrue(cash.aktywny());

        FormaPlatn card = dane.get(SECOND_INDEX);
        assertEquals(EXPECTED_CARD_ID, card.id());
        assertEquals(EXPECTED_NAZWA_CARD, card.nazwa());
        assertEquals(FormaPlatn.Typ.VALUE_1, card.typ());
        assertFalse(card.reszta());

        FormaPlatn transfer = dane.get(THIRD_INDEX);
        assertEquals(FormaPlatn.Typ.VALUE_5, transfer.typ());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int result = client.formyPlatn().count(null);

        // then
        assertEquals(EXPECTED_SIZE, result);
    }

    @Test
    void getById_whenRecordExists_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(TestClients.jsonFile(SINGLE_FILE)));

        // when
        FormaPlatn f = client.formyPlatn().getById(EXPECTED_FIRST_ID);

        // then
        assertNotNull(f);
        assertEquals(EXPECTED_FIRST_ID, f.id());
        assertEquals(EXPECTED_NAZWA_CASH, f.nazwa());
        assertEquals(FormaPlatn.Typ.VALUE_0, f.typ());
        assertTrue(f.reszta());
        assertTrue(f.aktywny());
    }

    @Test
    void getById_whenServerReturnsUnknownTypEnum_returnsRecordWithNullTyp() {
        // given - CF-04: producer-introduced typ value (99) must not break deserialization
        String json = "{\"status\":200,\"status_opis\":\"Ok\",\"dane\":{"
                + "\"id\":1,\"nazwa\":\"future\",\"typ\":99,\"reszta\":true,\"aktywny\":true}}";
        stubFor(get(urlPathMatching(URL_BY_ID)).willReturn(okJson(json)));

        // when
        FormaPlatn f = client.formyPlatn().getById(EXPECTED_FIRST_ID);

        // then
        assertNotNull(f);
        assertNull(f.typ(), "unknown typ code must map to null, not throw");
    }

    @Test
    void create_whenServerAccepts_returnsCreatedId() {
        // given
        stubFor(post(urlPathMatching(URL_LIST))
                .willReturn(aResponse().withStatus(HTTP_CREATED)
                        .withHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .withBody(TestClients.CREATED_JSON)));

        // when
        String id = client.formyPlatn().create(
                FormaPlatnCreateBuilder.builder(CREATE_NAZWA, CREATE_TYP)
                        .reszta(false).build());

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
        assertDoesNotThrow(() -> client.formyPlatn().update(
                FormaPlatnUpdateBuilder.builder(EXPECTED_FIRST_ID)
                        .nazwa(UPDATE_NAZWA)
                        .aktywny(true)
                        .build()));
        verify(SINGLE_REQUEST, putRequestedFor(urlPathMatching(URL_LIST)));
    }

    @Test
    void deleteById_whenRecordExists_completesWithoutError() {
        // given
        stubFor(delete(urlPathMatching(URL_BY_ID))
                .willReturn(okJson(TestClients.OK_JSON)));

        // when / then
        assertDoesNotThrow(() -> client.formyPlatn().deleteById(EXPECTED_FIRST_ID));
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
        var resource = client.formyPlatn();
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
        var resource = client.formyPlatn();
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
        var resource = client.formyPlatn();
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
        var resource = client.formyPlatn();
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
                    "self": "http://localhost:%d/demo/formyplatn?start=0",
                    "next": "http://localhost:%d/demo/formyplatn?start=2"
                  },
                  "dane": [{"id": 1, "nazwa": "Cash"}, {"id": 2, "nazwa": "Card"}]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/formyplatn?start=2"
                  },
                  "dane": [{"id": 3, "nazwa": "Transfer"}]
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
        java.util.List<FormaPlatn> all = new java.util.ArrayList<>();
        client.formyPlatn().list(null).forEach(all::add);

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
        assertDoesNotThrow(() -> retryClient.formyPlatn().count(null));
        verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }
}
