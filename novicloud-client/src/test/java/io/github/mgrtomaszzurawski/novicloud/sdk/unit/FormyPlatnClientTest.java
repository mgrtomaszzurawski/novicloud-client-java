/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.unit;

import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.api.FormyPlatnApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseFormyPlatnListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.FormaPlatnRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn.FormyPlatnClient;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockConstructionWithAnswer;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;

class FormyPlatnClientTest {

    private FormyPlatnClient client() {
        return new FormyPlatnClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY);
    }

    @Test
    void getById_whenIdIsNull_throwsIllegalArgument() {
        // given
        var client = client();

        // when / then
        assertThrows(IllegalArgumentException.class, () -> client.getById(null));
    }

    @Test
    void deleteById_whenIdIsNull_throwsIllegalArgument() {
        // given
        var client = client();

        // when / then
        assertThrows(IllegalArgumentException.class, () -> client.deleteById(null));
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
        ApiResponseFormyPlatnListRaw response = new ApiResponseFormyPlatnListRaw();
        response.setDane(List.of(new FormaPlatnRaw(), new FormaPlatnRaw(), new FormaPlatnRaw()));

        // when
        try (MockedConstruction<FormyPlatnApi> mc = mockConstructionWithAnswer(FormyPlatnApi.class,
                inv -> response)) {
            int result = new FormyPlatnClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(MOCK_LIST_COUNT, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenServerReturnsEmptyResponse_returnsZero() {
        // given
        ApiResponseFormyPlatnListRaw response = new ApiResponseFormyPlatnListRaw();

        // when
        try (MockedConstruction<FormyPlatnApi> mc = mockConstructionWithAnswer(FormyPlatnApi.class,
                inv -> response)) {
            int result = new FormyPlatnClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(EXPECTED_ZERO, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenServerIncludesSizeField_returnsSizeValue() {
        // given
        ApiResponseFormyPlatnListRaw response = new ApiResponseFormyPlatnListRaw();
        response.setSize(MOCK_REPORTED_SIZE);

        // when
        try (MockedConstruction<FormyPlatnApi> mc = mockConstructionWithAnswer(FormyPlatnApi.class,
                inv -> response)) {
            int result = new FormyPlatnClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(MOCK_REPORTED_SIZE, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenApiThrowsException_wrapsInNoviCloudException() {
        // given
        try (MockedConstruction<FormyPlatnApi> mc = mockConstructionWithAnswer(FormyPlatnApi.class,
                inv -> { throw apiServerError(); })) {
            var client = new FormyPlatnClient(new ApiClient(), TEST_ACCOUNT, NO_RETRY);

            // when / then
            assertThrows(NoviCloudException.class, () -> client.count(null));
            assertFalse(mc.constructed().isEmpty());
        }
    }
}
