/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kontrahent;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudAuthException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudRateLimitException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudServerException;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci.KontrahentCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci.KontrahentUpdateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class KontrahenciClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/kontrahenci";
    private static final String URL_BY_ID = "/[^/]+/kontrahenci/[^/]+";
    private static final String LIST_FILE = "kontrahenci/list.json";
    private static final String SINGLE_FILE = "kontrahenci/single.json";
    // Expected field values
    private static final long EXPECTED_ID = 2L;
    private static final int EXPECTED_LIST_SIZE = 1;
    private static final String EXPECTED_NIP = "1234567890";
    private static final String EXPECTED_NAZWA = "Acme Corp";
    private static final String EXPECTED_SKROT = "ACME";
    private static final String EXPECTED_ULICA = "Main Street";
    private static final String EXPECTED_NR_DOMU = "10";
    private static final String EXPECTED_NR_LOKALU = "5";
    private static final String EXPECTED_ULICA_I_NUMER = "Main Street 10/5";
    private static final String EXPECTED_KOD_POCZT = "00-001";
    private static final String EXPECTED_MIASTO = "Warsaw";

    // Create/update test values
    private static final String CREATE_NAZWA = "New Client";
    private static final String CREATE_NIP = "9876543210";
    private static final String CREATE_SKROT = "NEW";
    private static final String CREATE_ULICA = "Broadway";
    private static final String CREATE_NR_DOMU = "1";
    private static final String CREATE_KOD_POCZT = "00-100";
    private static final String CREATE_MIASTO = "Krakow";
    private static final String EXPECTED_CREATED_ID = "9999";
    private static final String UPDATE_NAZWA = "Updated Corp";
    private static final String UPDATE_TELEFON = "123456789";
    private static final String UPDATE_EMAIL = "contact@acme.com";

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
        List<Kontrahent> dane = new ArrayList<>();
        client.kontrahenci().list(null).forEach(dane::add);

        // then
        assertEquals(EXPECTED_LIST_SIZE, dane.size());

        Kontrahent k = dane.get(FIRST_INDEX);
        assertEquals(EXPECTED_ID, k.id());
        assertEquals(EXPECTED_NIP, k.nip());
        assertEquals(EXPECTED_NAZWA, k.nazwa());
        assertEquals(EXPECTED_SKROT, k.skrot());
        assertEquals(EXPECTED_ULICA, k.ulica());
        assertEquals(EXPECTED_NR_DOMU, k.nrDomu());
        assertEquals(EXPECTED_NR_LOKALU, k.nrLokalu());
        assertEquals(EXPECTED_ULICA_I_NUMER, k.ulicaINumer());
        assertEquals(EXPECTED_KOD_POCZT, k.kodPoczt());
        assertEquals(EXPECTED_MIASTO, k.poczta());
        assertEquals(EXPECTED_MIASTO, k.miasto());
        assertTrue(k.aktywny());
        assertFalse(k.dostawca());
        assertTrue(k.staly());
        assertFalse(k.producent());
        assertTrue(k.odbiorca());
        assertFalse(k.osoba());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int result = client.kontrahenci().count(null);

        // then
        assertEquals(EXPECTED_LIST_SIZE, result);
    }

    @Test
    void getById_whenRecordExists_deserializesAllFields() {
        // given
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(TestClients.jsonFile(SINGLE_FILE)));

        // when
        Kontrahent k = client.kontrahenci().getById(EXPECTED_ID);

        // then
        assertNotNull(k);
        assertEquals(EXPECTED_ID, k.id());
        assertEquals(EXPECTED_NAZWA, k.nazwa());
        assertEquals(EXPECTED_NIP, k.nip());
        assertEquals(EXPECTED_SKROT, k.skrot());
        assertEquals(EXPECTED_ULICA, k.ulica());
        assertEquals(EXPECTED_NR_DOMU, k.nrDomu());
        assertEquals(EXPECTED_NR_LOKALU, k.nrLokalu());
        assertEquals(EXPECTED_KOD_POCZT, k.kodPoczt());
        assertEquals(EXPECTED_MIASTO, k.miasto());
        assertTrue(k.aktywny());
        assertFalse(k.dostawca());
        assertTrue(k.staly());
        assertFalse(k.producent());
        assertTrue(k.odbiorca());
        assertFalse(k.osoba());
    }

    @Test
    void create_whenServerAccepts_returnsCreatedId() {
        // given
        stubFor(post(urlPathMatching(URL_LIST))
                .willReturn(aResponse().withStatus(HTTP_CREATED)
                        .withHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .withBody(TestClients.CREATED_JSON)));

        // when
        String id = client.kontrahenci().create(
                KontrahentCreateBuilder.builder(CREATE_NAZWA)
                        .nip(CREATE_NIP)
                        .skrot(CREATE_SKROT)
                        .ulica(CREATE_ULICA)
                        .nrDomu(CREATE_NR_DOMU)
                        .kodPoczt(CREATE_KOD_POCZT)
                        .miasto(CREATE_MIASTO)
                        .aktywny(true)
                        .staly(true)
                        .odbiorca(true)
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
        assertDoesNotThrow(() -> client.kontrahenci().update(
                KontrahentUpdateBuilder.builder(EXPECTED_ID)
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
        assertDoesNotThrow(() -> client.kontrahenci().deleteById(EXPECTED_ID));
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
        var resource = client.kontrahenci();
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
        var resource = client.kontrahenci();
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
        var resource = client.kontrahenci();
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
        var resource = client.kontrahenci();
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
                    "self": "http://localhost:%d/demo/kontrahenci?start=0",
                    "next": "http://localhost:%d/demo/kontrahenci?start=2"
                  },
                  "dane": [{"id": 1, "nazwa": "A"}, {"id": 2, "nazwa": "B"}]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/kontrahenci?start=2"
                  },
                  "dane": [{"id": 3, "nazwa": "C"}]
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
        java.util.List<Kontrahent> all = new java.util.ArrayList<>();
        client.kontrahenci().list(null).forEach(all::add);

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
        assertDoesNotThrow(() -> retryClient.kontrahenci().count(null));
        verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }
}
