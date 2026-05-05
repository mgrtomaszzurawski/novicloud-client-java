/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.unit;

import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.api.KasjerzyApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseKasjerzyListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.KasjerRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasjerzy.KasjerzyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.kasjerzy.KasjerzyClientImpl;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockConstructionWithAnswer;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;

class KasjerzyClientTest {

    private KasjerzyClient client() {
        return new KasjerzyClientImpl(new ApiClient(), TEST_ACCOUNT, NO_RETRY);
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
        ApiResponseKasjerzyListRaw response = new ApiResponseKasjerzyListRaw();
        response.setDane(List.of(new KasjerRaw(), new KasjerRaw(), new KasjerRaw()));

        // when
        try (MockedConstruction<KasjerzyApi> mc = mockConstructionWithAnswer(KasjerzyApi.class,
                inv -> response)) {
            int result = new KasjerzyClientImpl(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(MOCK_LIST_COUNT, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenServerReturnsEmptyResponse_returnsZero() {
        // given
        ApiResponseKasjerzyListRaw response = new ApiResponseKasjerzyListRaw();

        // when
        try (MockedConstruction<KasjerzyApi> mc = mockConstructionWithAnswer(KasjerzyApi.class,
                inv -> response)) {
            int result = new KasjerzyClientImpl(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(EXPECTED_ZERO, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenServerIncludesSizeField_returnsSizeValue() {
        // given
        ApiResponseKasjerzyListRaw response = new ApiResponseKasjerzyListRaw();
        response.setSize(MOCK_REPORTED_SIZE);

        // when
        try (MockedConstruction<KasjerzyApi> mc = mockConstructionWithAnswer(KasjerzyApi.class,
                inv -> response)) {
            int result = new KasjerzyClientImpl(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(MOCK_REPORTED_SIZE, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenApiThrowsException_wrapsInNoviCloudException() {
        // given
        try (MockedConstruction<KasjerzyApi> mc = mockConstructionWithAnswer(KasjerzyApi.class,
                inv -> { throw apiServerError(); })) {
            var client = new KasjerzyClientImpl(new ApiClient(), TEST_ACCOUNT, NO_RETRY);

            // when / then
            assertThrows(NoviCloudException.class, () -> client.count(null));
            assertFalse(mc.constructed().isEmpty());
        }
    }
}
