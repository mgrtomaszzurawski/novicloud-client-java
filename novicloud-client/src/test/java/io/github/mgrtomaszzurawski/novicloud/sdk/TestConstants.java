/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk;

import io.github.mgrtomaszzurawski.novicloud.client.ApiException;

/**
 * Shared test constants used across multiple test files.
 */
public final class TestConstants {

    private TestConstants() { }

    public static final String TEST_ACCOUNT = "test-account";
    public static final RetryPolicy NO_RETRY = RetryPolicy.builder().enabled(false).build();

    // -- HTTP status codes --
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_PAYMENT_REQUIRED = 402;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_GONE = 410;
    public static final int HTTP_UNPROCESSABLE = 422;
    public static final int HTTP_RATE_LIMITED = 429;
    public static final int HTTP_SERVER_ERROR = 500;
    public static final int HTTP_SERVICE_UNAVAILABLE = 503;
    public static final int HTTP_NETWORK_ERROR = 0;

    // -- HTTP headers --
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String RETRY_AFTER_HEADER = "Retry-After";

    // -- WireMock scenario names --
    public static final String SCENARIO_PAGINATION = "pagination";
    public static final String SCENARIO_STATE_PAGE2 = "page2";
    public static final String SCENARIO_RETRY = "retry";
    public static final String SCENARIO_STATE_RECOVERED = "recovered";

    // -- WireMock verify counts --
    public static final int SINGLE_REQUEST = 1;
    public static final int RETRY_REQUEST_COUNT = 2;

    // -- common test values --
    public static final String SERVER_ERROR_MESSAGE = "Server Error";
    public static final String RETRY_AFTER_SECONDS = "5";
    public static final long EXPECTED_RETRY_AFTER = 5L;
    public static final int EXPECTED_PAGINATION_SIZE = 3;
    public static final long NON_EXISTENT_ID = 99999L;

    // -- unit test mock values --
    public static final int MOCK_LIST_COUNT = 3;
    public static final int MOCK_REPORTED_SIZE = 50;
    public static final int EXPECTED_ZERO = 0;

    // -- collection indexes --
    public static final int FIRST_INDEX = 0;
    public static final int SECOND_INDEX = 1;
    public static final int THIRD_INDEX = 2;
    public static final int FOURTH_INDEX = 3;

    /** Creates a standard server error ApiException for tests. */
    public static ApiException apiServerError() {
        return new ApiException(HTTP_SERVER_ERROR, SERVER_ERROR_MESSAGE);
    }
}
