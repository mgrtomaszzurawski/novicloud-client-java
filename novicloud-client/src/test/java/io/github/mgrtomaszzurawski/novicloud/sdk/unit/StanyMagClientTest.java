/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.unit;

import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.api.StanyMagApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseStanyMagListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.StanMagRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stanymag.StanyMagClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.stanymag.StanyMagClientImpl;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockConstructionWithAnswer;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;

class StanyMagClientTest {
    private static final long VALID_ID = 1L;

    private StanyMagClient client() {
        return new StanyMagClientImpl(new ApiClient(), TEST_ACCOUNT, NO_RETRY);
    }

    @Test
    void listByTowar_whenTowarIdIsNull_throwsIllegalArgument() {
        // given
        var client = client();

        // when / then
        assertThrows(IllegalArgumentException.class, () -> client.listByTowar(null, null));
    }

    @Test
    void getByTowarAndSklep_whenTowarIdIsNull_throwsIllegalArgument() {
        // given
        var client = client();

        // when / then
        assertThrows(IllegalArgumentException.class,
                () -> client.getByTowarAndSklep(null, VALID_ID, null));
    }

    @Test
    void getByTowarAndSklep_whenSklepIdIsNull_throwsIllegalArgument() {
        // given
        var client = client();

        // when / then
        assertThrows(IllegalArgumentException.class,
                () -> client.getByTowarAndSklep(VALID_ID, null, null));
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
        ApiResponseStanyMagListRaw response = new ApiResponseStanyMagListRaw();
        response.setDane(List.of(new StanMagRaw(), new StanMagRaw(), new StanMagRaw()));

        // when
        try (MockedConstruction<StanyMagApi> mc = mockConstructionWithAnswer(StanyMagApi.class,
                inv -> response)) {
            int result = new StanyMagClientImpl(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(MOCK_LIST_COUNT, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenServerReturnsEmptyResponse_returnsZero() {
        // given
        ApiResponseStanyMagListRaw response = new ApiResponseStanyMagListRaw();

        // when
        try (MockedConstruction<StanyMagApi> mc = mockConstructionWithAnswer(StanyMagApi.class,
                inv -> response)) {
            int result = new StanyMagClientImpl(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(EXPECTED_ZERO, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenServerIncludesSizeField_returnsSizeValue() {
        // given
        ApiResponseStanyMagListRaw response = new ApiResponseStanyMagListRaw();
        response.setSize(MOCK_REPORTED_SIZE);

        // when
        try (MockedConstruction<StanyMagApi> mc = mockConstructionWithAnswer(StanyMagApi.class,
                inv -> response)) {
            int result = new StanyMagClientImpl(new ApiClient(), TEST_ACCOUNT, NO_RETRY).count(null);

            // then
            assertEquals(MOCK_REPORTED_SIZE, result);
            assertFalse(mc.constructed().isEmpty());
        }
    }

    @Test
    void count_whenApiThrowsException_wrapsInNoviCloudException() {
        // given
        try (MockedConstruction<StanyMagApi> mc = mockConstructionWithAnswer(StanyMagApi.class,
                inv -> { throw apiServerError(); })) {
            var client = new StanyMagClientImpl(new ApiClient(), TEST_ACCOUNT, NO_RETRY);

            // when / then
            assertThrows(NoviCloudException.class, () -> client.count(null));
            assertFalse(mc.constructed().isEmpty());
        }
    }
}
