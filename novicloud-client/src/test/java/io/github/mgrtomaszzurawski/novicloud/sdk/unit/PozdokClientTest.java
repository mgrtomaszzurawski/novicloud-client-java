/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.unit;

import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.api.PozdokApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponsePozdokListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.PozycjaDokumentuRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.pozdok.PozdokClient;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockConstructionWithAnswer;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;

class PozdokClientTest {

    private PozdokClient client() {
        return new PozdokClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY);
    }

    @Test
    void getById_whenIdIsNull_throwsIllegalArgument() {
        // given
        var client = client();

        // when / then
        assertThrows(IllegalArgumentException.class, () -> client.getById(null));
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
        ApiResponsePozdokListRaw response = new ApiResponsePozdokListRaw();
        response.setDane(List.of(new PozycjaDokumentuRaw(), new PozycjaDokumentuRaw(), new PozycjaDokumentuRaw()));

        // when
        try (MockedConstruction<PozdokApi> mc = mockConstructionWithAnswer(PozdokApi.class,
                inv -> response)) {
            int result = new PozdokClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(MOCK_LIST_COUNT, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenServerReturnsEmptyResponse_returnsZero() {
        // given
        ApiResponsePozdokListRaw response = new ApiResponsePozdokListRaw();

        // when
        try (MockedConstruction<PozdokApi> mc = mockConstructionWithAnswer(PozdokApi.class,
                inv -> response)) {
            int result = new PozdokClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(EXPECTED_ZERO, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenServerIncludesSizeField_returnsSizeValue() {
        // given
        ApiResponsePozdokListRaw response = new ApiResponsePozdokListRaw();
        response.setSize(MOCK_REPORTED_SIZE);

        // when
        try (MockedConstruction<PozdokApi> mc = mockConstructionWithAnswer(PozdokApi.class,
                inv -> response)) {
            int result = new PozdokClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(MOCK_REPORTED_SIZE, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenApiThrowsException_wrapsInNoviCloudException() {
        // given
        try (MockedConstruction<PozdokApi> mc = mockConstructionWithAnswer(PozdokApi.class,
                inv -> { throw apiServerError(); })) {
            var client = new PozdokClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY);

            // when / then
            assertThrows(NoviCloudException.class, () -> client.count(null));
            assertFalse(mc.constructed().isEmpty());
        }
    }
}
