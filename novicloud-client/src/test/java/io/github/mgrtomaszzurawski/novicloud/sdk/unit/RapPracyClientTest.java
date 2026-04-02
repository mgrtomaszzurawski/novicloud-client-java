/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.unit;

import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.api.RapPracyApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseRapPracyListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.RaportPracyRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy.RapPracyClient;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockConstructionWithAnswer;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;

class RapPracyClientTest {

    private RapPracyClient client() {
        return new RapPracyClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY);
    }

    @Test
    void list_whenQueryIsNull_returnsPagedResult() {
        // when
        var result = client().list(null);

        // then
        assertNotNull(result);
    }

    @Test
    void count_whenServerOmitsSizeField_returnsListLength() {
        // given
        ApiResponseRapPracyListRaw response = new ApiResponseRapPracyListRaw();
        response.setDane(List.of(new RaportPracyRaw(), new RaportPracyRaw(), new RaportPracyRaw()));

        // when
        try (MockedConstruction<RapPracyApi> mc = mockConstructionWithAnswer(RapPracyApi.class,
                inv -> response)) {
            int result = new RapPracyClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(MOCK_LIST_COUNT, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenServerReturnsEmptyResponse_returnsZero() {
        // given
        ApiResponseRapPracyListRaw response = new ApiResponseRapPracyListRaw();

        // when
        try (MockedConstruction<RapPracyApi> mc = mockConstructionWithAnswer(RapPracyApi.class,
                inv -> response)) {
            int result = new RapPracyClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(EXPECTED_ZERO, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenServerIncludesSizeField_returnsSizeValue() {
        // given
        ApiResponseRapPracyListRaw response = new ApiResponseRapPracyListRaw();
        response.setSize(MOCK_REPORTED_SIZE);

        // when
        try (MockedConstruction<RapPracyApi> mc = mockConstructionWithAnswer(RapPracyApi.class,
                inv -> response)) {
            int result = new RapPracyClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(MOCK_REPORTED_SIZE, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenApiThrowsException_wrapsInNoviCloudException() {
        // given
        try (MockedConstruction<RapPracyApi> mc = mockConstructionWithAnswer(RapPracyApi.class,
                inv -> { throw apiServerError(); })) {
            var client = new RapPracyClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY);

            // when / then
            assertThrows(NoviCloudException.class, () -> client.count(null));
            assertFalse(mc.constructed().isEmpty());
        }
    }
}
