/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Towar;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudAuthException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudRateLimitException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudServerException;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarCenaWSklepie;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarKodDodatkowy;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarSkladnik;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarSkladnikTowar;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarUpdateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class TowaryClientIntegrationTest {

    private static final String URL_LIST = "/[^/]+/towary";
    private static final String URL_BY_ID = "/[^/]+/towary/[^/]+";
    private static final String LIST_FILE = "towary/list.json";
    private static final String SINGLE_FILE = "towary/single.json";
    private static final int EXPECTED_SIZE = 2;
    private static final int VAT_8_PCT = 800;
    private static final int VAT_23_PCT = 2300;

    // -- expected field values (Product Alpha) --
    private static final long EXPECTED_ALPHA_ID = 2L;
    private static final long EXPECTED_BETA_ID = 3L;
    private static final String EXPECTED_KOD = "5901234567890";
    private static final String EXPECTED_CKU = "CKU-001";
    private static final String EXPECTED_NAZWA = "Product Alpha";
    private static final double EXPECTED_CENA_EW = 10.63;
    private static final double EXPECTED_CENA_DET = 16.9;
    private static final double EXPECTED_CENA_HURT = 129.9;
    private static final double EXPECTED_CENA_NOC = 129.9;
    private static final double EXPECTED_CENA_DOD = 129.9;
    private static final String EXPECTED_GTU = "GTU_01";
    private static final double EXPECTED_MASA_WL = 0.5;
    private static final int EXPECTED_KODY_DOD_COUNT = 2;
    private static final String EXPECTED_KOD_DOD_FIRST = "5901234567891";
    private static final int EXPECTED_CENY_W_SKLEPACH_COUNT = 2;
    private static final String EXPECTED_CENA_SKLEP_1_ID = "1";
    private static final double EXPECTED_CENA_SKLEP_1_DET = 17.5;
    private static final int EXPECTED_SKLADNIKI_COUNT = 1;
    private static final String EXPECTED_SKLADNIK_NAZWA = "Main";
    private static final String EXPECTED_SKLADNIK_TOWAR_ID = "100";

    // -- create / update test data --
    private static final String CREATE_KOD = "1234567890";
    private static final String CREATE_NAZWA = "New Product";
    private static final double CREATE_CENA_DET = 19.99;
    private static final String CREATE_JM_ID = "1";
    private static final String CREATE_ASORT_ID = "1";
    private static final String EXPECTED_CREATED_ID = "9999";

    private static final String UPDATE_NAZWA = "Updated Product";
    private static final double UPDATE_CENA_DET = 25.0;

    // -- pagination test expected IDs --
    private static final long EXPECTED_PAGE_ID_1 = 1L;
    private static final long EXPECTED_PAGE_ID_2 = 2L;
    private static final long EXPECTED_PAGE_ID_3 = 3L;
    private static final int EXPECTED_FETCH_FROM_OFFSET = 2;
    private static final int EXPECTED_FETCH_FROM_SIZE = 1;
    private static final int EXPECTED_SEEK_OFFSET = 2;
    private static final String SCENARIO_FETCH_FROM = "fetchFrom";
    private static final String SCENARIO_SEEK = "seek";
    private static final String SCENARIO_STATE_READY = "ready";

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
        List<Towar> dane = new ArrayList<>();
        client.towary().list(null).forEach(dane::add);

        // then
        assertEquals(EXPECTED_SIZE, dane.size());

        Towar alpha = dane.get(FIRST_INDEX);
        assertEquals(EXPECTED_ALPHA_ID, alpha.id());
        assertEquals(EXPECTED_KOD, alpha.kod());
        assertEquals(EXPECTED_CKU, alpha.cku());
        assertEquals(EXPECTED_NAZWA, alpha.nazwa());
        assertEquals(VAT_8_PCT, alpha.stawkaVat());
        assertFalse(alpha.akcyzowy());
        assertNotNull(alpha.typ());
        assertEquals(EXPECTED_CENA_EW, alpha.cenaEw());
        assertEquals(EXPECTED_CENA_DET, alpha.cenaDet());
        assertEquals(EXPECTED_CENA_HURT, alpha.cenaHurt());
        assertEquals(EXPECTED_CENA_NOC, alpha.cenaNoc());
        assertEquals(EXPECTED_CENA_DOD, alpha.cenaDod());
        assertNotNull(alpha.przySprzedazy());
        assertEquals(EXPECTED_GTU, alpha.gtu());
        assertTrue(alpha.aktywny());
        assertEquals(EXPECTED_MASA_WL, alpha.masaWl());
        assertNotNull(alpha.jmId());
        assertNotNull(alpha.asortId());
        assertNotNull(alpha.ostZmiana());

        Towar beta = dane.get(SECOND_INDEX);
        assertEquals(EXPECTED_BETA_ID, beta.id());
        assertEquals(VAT_23_PCT, beta.stawkaVat());
    }

    @Test
    void count_whenServerReturnsData_returnsSizeField() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(TestClients.jsonFile(LIST_FILE)));

        // when
        int count = client.towary().count(null);

        // then
        assertEquals(EXPECTED_SIZE, count);
    }

    @Test
    void getById_whenRecordExists_deserializesScalarFields() {
        // given
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(TestClients.jsonFile(SINGLE_FILE)));

        // when
        Towar t = client.towary().getById(EXPECTED_ALPHA_ID);

        // then
        assertNotNull(t);
        assertEquals(EXPECTED_ALPHA_ID, t.id());
        assertEquals(EXPECTED_KOD, t.kod());
        assertEquals(EXPECTED_CKU, t.cku());
        assertEquals(EXPECTED_NAZWA, t.nazwa());
        assertEquals(VAT_8_PCT, t.stawkaVat());
        assertFalse(t.akcyzowy());
        assertNotNull(t.typ());
        assertEquals(EXPECTED_CENA_EW, t.cenaEw());
        assertEquals(EXPECTED_CENA_DET, t.cenaDet());
        assertEquals(EXPECTED_CENA_HURT, t.cenaHurt());
        assertEquals(EXPECTED_CENA_NOC, t.cenaNoc());
        assertEquals(EXPECTED_CENA_DOD, t.cenaDod());
        assertNotNull(t.przySprzedazy());
        assertEquals(EXPECTED_GTU, t.gtu());
        assertTrue(t.aktywny());
        assertEquals(EXPECTED_MASA_WL, t.masaWl());
        assertNotNull(t.jmId());
        assertNotNull(t.asortId());
        assertNotNull(t.ostZmiana());
    }

    @Test
    void getById_whenRecordExists_deserializesNestedFields() {
        // given - F-12: Towar nested records (kody_dod, ceny_w_sklepach, skladniki)
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(TestClients.jsonFile(SINGLE_FILE)));

        // when
        Towar t = client.towary().getById(EXPECTED_ALPHA_ID);

        // then
        assertEquals(EXPECTED_KODY_DOD_COUNT, t.kodyDod().size());
        assertEquals(EXPECTED_KOD_DOD_FIRST, t.kodyDod().get(FIRST_INDEX).kod());
        assertEquals(EXPECTED_CENY_W_SKLEPACH_COUNT, t.cenyWSklepach().size());
        assertEquals(EXPECTED_CENA_SKLEP_1_ID, t.cenyWSklepach().get(FIRST_INDEX).sklepId());
        assertEquals(EXPECTED_CENA_SKLEP_1_DET, t.cenyWSklepach().get(FIRST_INDEX).cenaDet());
        assertEquals(EXPECTED_SKLADNIKI_COUNT, t.skladniki().size());
        assertEquals(EXPECTED_SKLADNIK_NAZWA, t.skladniki().get(FIRST_INDEX).nazwa());
        assertEquals(EXPECTED_SKLADNIK_TOWAR_ID, t.skladniki().get(FIRST_INDEX).towary().get(FIRST_INDEX).towarId());
    }

    @Test
    void create_whenServerAccepts_returnsCreatedId() {
        // given
        stubFor(post(urlPathMatching(URL_LIST))
                .willReturn(aResponse().withStatus(HTTP_CREATED)
                        .withHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .withBody(TestClients.CREATED_JSON)));

        // when
        String id = client.towary().create(
                TowarCreateBuilder.builder(CREATE_KOD, CREATE_NAZWA)
                        .stawkaVat(VAT_23_PCT)
                        .cenaDet(CREATE_CENA_DET)
                        .aktywny(true)
                        .jmId(CREATE_JM_ID)
                        .asortId(CREATE_ASORT_ID)
                        .build());

        // then
        assertEquals(EXPECTED_CREATED_ID, id);
        verify(SINGLE_REQUEST, postRequestedFor(urlPathMatching(URL_LIST)));
    }

    @Test
    void create_whenNestedListsSet_sendsThemInRequestBody() {
        // given
        stubFor(post(urlPathMatching(URL_LIST))
                .willReturn(aResponse().withStatus(HTTP_CREATED)
                        .withHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .withBody(TestClients.CREATED_JSON)));

        // when
        client.towary().create(
                TowarCreateBuilder.builder(CREATE_KOD, CREATE_NAZWA)
                        .kodyDod(List.of(new TowarKodDodatkowy("5901234123457", 6.0, 2)))
                        .cenyWSklepach(List.of(new TowarCenaWSklepie(
                                "3", null, 14.50, null, null, null, null)))
                        .skladniki(List.of(new TowarSkladnik(
                                "Sos", 2.0, true, false, false,
                                List.of(new TowarSkladnikTowar("42", 1.0, true, null, true)))))
                        .build());

        // then - the nested lists must appear in the POST body with snake_case keys
        verify(SINGLE_REQUEST, postRequestedFor(urlPathMatching(URL_LIST))
                .withRequestBody(matchingJsonPath("$.kody_dod[0].kod", equalTo("5901234123457")))
                .withRequestBody(matchingJsonPath("$.kody_dod[0].poziom_cen", equalTo("2")))
                .withRequestBody(matchingJsonPath("$.ceny_w_sklepach[0].cena_det", equalTo("14.5")))
                .withRequestBody(matchingJsonPath("$.ceny_w_sklepach[0].sklep.id", equalTo("3")))
                .withRequestBody(matchingJsonPath("$.skladniki[0].nazwa", equalTo("Sos")))
                .withRequestBody(matchingJsonPath("$.skladniki[0].towary[0].towar.id", equalTo("42"))));
    }

    @Test
    void update_doesNotThrow() {
        // given
        stubFor(put(urlPathMatching(URL_LIST))
                .willReturn(okJson(TestClients.OK_JSON)));

        // when / then
        assertDoesNotThrow(() -> client.towary().update(
                TowarUpdateBuilder.builder(EXPECTED_ALPHA_ID)
                        .nazwa(UPDATE_NAZWA)
                        .cenaDet(UPDATE_CENA_DET)
                        .build()));
        verify(SINGLE_REQUEST, putRequestedFor(urlPathMatching(URL_LIST)));
    }

    @Test
    void deleteById_whenRecordExists_completesWithoutError() {
        // given
        stubFor(delete(urlPathMatching(URL_BY_ID))
                .willReturn(okJson(TestClients.OK_JSON)));

        // when / then
        assertDoesNotThrow(() -> client.towary().deleteById(EXPECTED_ALPHA_ID));
        verify(SINGLE_REQUEST, deleteRequestedFor(urlPathMatching(URL_BY_ID)));
    }

    // -----------------------------------------------------------------------
    // Error scenarios (F-06)
    // -----------------------------------------------------------------------

    @Test
    void count_whenServerReturns401_throwsAuthException() {
        // given
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(unauthorized()));

        // when / then
        var resource = client.towary();
        NoviCloudAuthException ex = assertThrows(NoviCloudAuthException.class,
                () -> resource.count(null));
        assertEquals(HTTP_UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void count_whenServerReturns402_throwsAccessException() {
        // given - F-04: REST API option not subscribed
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(aResponse().withStatus(HTTP_PAYMENT_REQUIRED)));

        // when / then
        var resource = client.towary();
        var ex = assertThrows(io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudAccessException.class,
                () -> resource.count(null));
        assertEquals(HTTP_PAYMENT_REQUIRED, ex.getStatusCode());
    }

    @Test
    void count_whenConnectionFails_throwsNetworkException() {
        // given - F-02: WireMock fault simulates IOException at the socket level
        stubFor(get(urlPathMatching(URL_LIST))
                .willReturn(aResponse().withFault(com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER)));

        // when / then
        var resource = client.towary();
        var ex = assertThrows(io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNetworkException.class,
                () -> resource.count(null));
        assertNotNull(ex.getCause());
    }

    @Test
    void getById_whenServerReturnsUnknownEnumValue_returnsRecordWithNullEnum() {
        // given - F-03: producer-introduced enum value (typ=99) must not break deserialization
        String json = "{\"status\":200,\"status_opis\":\"Ok\",\"dane\":{"
                + "\"id\":2,\"kod\":\"X\",\"nazwa\":\"Y\",\"typ\":99,\"przy_sprzedazy\":42"
                + "}}";
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(okJson(json)));

        // when
        Towar t = client.towary().getById(EXPECTED_ALPHA_ID);

        // then
        assertNotNull(t);
        assertNull(t.typ(), "unknown typ enum value should map to null, not throw");
        assertNull(t.przySprzedazy(), "unknown przySprzedazy enum value should map to null, not throw");
    }

    @Test
    void getById_whenServerReturns404_throwsNotFoundException() {
        // given
        stubFor(get(urlPathMatching(URL_BY_ID))
                .willReturn(notFound()));

        // when / then
        var resource = client.towary();
        NoviCloudNotFoundException ex = assertThrows(NoviCloudNotFoundException.class,
                () -> resource.getById(NON_EXISTENT_ID));
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
        var resource = client.towary();
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
        var resource = client.towary();
        NoviCloudServerException ex = assertThrows(NoviCloudServerException.class,
                () -> resource.count(null));
        assertEquals(HTTP_SERVER_ERROR, ex.getStatusCode());
    }

    // -----------------------------------------------------------------------
    // PagedResult metadata and random access
    // -----------------------------------------------------------------------

    @Test
    void list_returnsPagedResult_totalCount(WireMockRuntimeInfo wm) {
        // given
        int port = wm.getHttpPort();
        String json = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 2, "start": 0, "on_page": 2,
                  "links": { "self": "http://localhost:%d/demo/towary?content=99&start=0" },
                  "dane": [
                    {"id": 2, "kod": "CODE1", "nazwa": "Product Alpha", "stawka_vat": 800, "aktywny": true},
                    {"id": 3, "kod": "CODE2", "nazwa": "Product Beta",  "stawka_vat": 2300, "aktywny": true}
                  ]
                }""".formatted(port);
        stubFor(get(urlPathMatching(URL_LIST)).willReturn(okJson(json)));

        // when
        PagedResult<Towar> result = client.towary().list(null);

        // then
        assertEquals(EXPECTED_SIZE, result.totalCount());
        assertEquals(EXPECTED_SIZE, result.pageSize());
    }

    @Test
    void list_whenFetchFromOffset_returnsPage(WireMockRuntimeInfo wm) {
        // given
        int port = wm.getHttpPort();
        String page1 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 0, "on_page": 2,
                  "links": { "self": "http://localhost:%d/demo/towary?content=77&start=0" },
                  "dane": [
                    {"id": 1, "kod": "CODE1", "nazwa": "Product 1", "stawka_vat": 2300, "aktywny": true},
                    {"id": 2, "kod": "CODE2", "nazwa": "Product 2", "stawka_vat": 2300, "aktywny": true}
                  ]
                }""".formatted(port);
        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": { "self": "http://localhost:%d/demo/towary?content=77&start=2" },
                  "dane": [
                    {"id": 3, "kod": "CODE3", "nazwa": "Product 3", "stawka_vat": 2300, "aktywny": true}
                  ]
                }""".formatted(port);

        stubFor(get(urlPathMatching(URL_LIST))
                .inScenario(SCENARIO_FETCH_FROM)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(okJson(page1))
                .willSetStateTo(SCENARIO_STATE_READY));
        stubFor(get(urlPathMatching(URL_LIST))
                .inScenario(SCENARIO_FETCH_FROM)
                .whenScenarioStateIs(SCENARIO_STATE_READY)
                .willReturn(okJson(page2)));

        // when
        PagedResult<Towar> result = client.towary().list(null);
        List<Towar> fetched = result.fetchFrom(EXPECTED_FETCH_FROM_OFFSET);

        // then
        assertEquals(EXPECTED_FETCH_FROM_SIZE, fetched.size());
        assertEquals(EXPECTED_PAGE_ID_3, fetched.get(FIRST_INDEX).id());
    }

    @Test
    void list_whenSeekToPosition_startsFromOffset(WireMockRuntimeInfo wm) {
        // given
        int port = wm.getHttpPort();
        String page1 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 0, "on_page": 2,
                  "links": { "self": "http://localhost:%d/demo/towary?content=55&start=0" },
                  "dane": [
                    {"id": 1, "kod": "CODE1", "nazwa": "Product 1", "stawka_vat": 2300, "aktywny": true},
                    {"id": 2, "kod": "CODE2", "nazwa": "Product 2", "stawka_vat": 2300, "aktywny": true}
                  ]
                }""".formatted(port);
        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": { "self": "http://localhost:%d/demo/towary?content=55&start=2" },
                  "dane": [
                    {"id": 3, "kod": "CODE3", "nazwa": "Product 3", "stawka_vat": 2300, "aktywny": true}
                  ]
                }""".formatted(port);

        stubFor(get(urlPathMatching(URL_LIST))
                .inScenario(SCENARIO_SEEK)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(okJson(page1))
                .willSetStateTo(SCENARIO_STATE_READY));
        stubFor(get(urlPathMatching(URL_LIST))
                .inScenario(SCENARIO_SEEK)
                .whenScenarioStateIs(SCENARIO_STATE_READY)
                .willReturn(okJson(page2)));

        // when
        PagedResult<Towar> result = client.towary().list(null);
        result.seek(EXPECTED_SEEK_OFFSET);
        java.util.ListIterator<Towar> it = result.listIterator();

        // then
        assertTrue(it.hasNext());
        assertEquals(EXPECTED_PAGE_ID_3, it.next().id());
        assertFalse(it.hasNext());
    }

    // -----------------------------------------------------------------------
    // Pagination (F-07)
    // -----------------------------------------------------------------------

    @Test
    void list_whenSecondPageReturnsError_preservesErrorBody(WireMockRuntimeInfo wm) {
        // given - CF-04: page-2+ HTTP errors (via LinkFetcher) must preserve the
        // diagnostic body, matching the page-1 generated-call behaviour.
        int port = wm.getHttpPort();
        String page1 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 99, "start": 0, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/towary?start=0",
                    "next": "http://localhost:%d/demo/towary?start=1"
                  },
                  "dane": [
                    {"id": 1, "kod": "CODE1", "nazwa": "Product 1"}
                  ]
                }""".formatted(port, port);
        String errorBody = "{\"status\":400,\"status_opis\":\"Bad request\","
                + "\"dane\":{\"par_niewlasciwe\":[\"unknown_filter\"],\"par_bledna_wart\":[\"start\"]}}";

        stubFor(get(urlPathMatching(URL_LIST))
                .inScenario(SCENARIO_PAGINATION)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(okJson(page1))
                .willSetStateTo(SCENARIO_STATE_PAGE2));
        stubFor(get(urlPathMatching(URL_LIST))
                .inScenario(SCENARIO_PAGINATION)
                .whenScenarioStateIs(SCENARIO_STATE_PAGE2)
                .willReturn(aResponse()
                        .withStatus(HTTP_BAD_REQUEST)
                        .withHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .withBody(errorBody)));

        // when - first page is fine; iterating into page 2 throws with body preserved
        var resource = client.towary();
        var result = resource.list(null);
        var ex = assertThrows(NoviCloudException.class, () -> {
            for (Towar ignored : result) { /* triggers page 2 fetch */ }
        });

        // then
        assertNotNull(ex.getResponseBody(), "LinkFetcher must preserve the error response body");
        assertTrue(ex.getResponseBody().contains("par_niewlasciwe"));
        assertTrue(ex.getErrorDetails().isPresent());
        assertEquals(java.util.List.of("unknown_filter"), ex.getErrorDetails().get().parNiewlasciwe());
    }

    @Test
    void list_whenMultiplePages_iteratesAllPages(WireMockRuntimeInfo wm) {
        // given
        int port = wm.getHttpPort();
        String page1 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 0, "on_page": 2,
                  "links": {
                    "self": "http://localhost:%d/demo/towary?start=0",
                    "next": "http://localhost:%d/demo/towary?start=2"
                  },
                  "dane": [
                    {"id": 1, "kod": "CODE1", "nazwa": "Product 1", "stawka_vat": 2300, "aktywny": true},
                    {"id": 2, "kod": "CODE2", "nazwa": "Product 2", "stawka_vat": 2300, "aktywny": true}
                  ]
                }""".formatted(port, port);

        String page2 = """
                {
                  "status": 200, "status_opis": "Ok",
                  "size": 3, "start": 2, "on_page": 1,
                  "links": {
                    "self": "http://localhost:%d/demo/towary?start=2"
                  },
                  "dane": [
                    {"id": 3, "kod": "CODE3", "nazwa": "Product 3", "stawka_vat": 2300, "aktywny": true}
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
        List<Towar> all = new ArrayList<>();
        client.towary().list(null).forEach(all::add);

        // then
        assertEquals(EXPECTED_PAGINATION_SIZE, all.size());
        assertEquals(EXPECTED_PAGE_ID_1, all.get(FIRST_INDEX).id());
        assertEquals(EXPECTED_PAGE_ID_2, all.get(SECOND_INDEX).id());
        assertEquals(EXPECTED_PAGE_ID_3, all.get(THIRD_INDEX).id());
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
        assertDoesNotThrow(() -> retryClient.towary().count(null));
        verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
    }
}
