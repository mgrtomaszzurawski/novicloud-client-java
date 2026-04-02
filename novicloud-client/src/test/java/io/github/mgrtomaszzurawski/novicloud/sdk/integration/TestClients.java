/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.integration;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.APPLICATION_JSON;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.CONTENT_TYPE_HEADER;
import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.HTTP_OK;

final class TestClients {

    static final String ACCOUNT = "demo";
    static final String PASSWORD = "test";
    static final int RETRY_MAX_ATTEMPTS = 3;

    static final String CREATED_JSON = """
            {"status":201,"status_opis":"Ok","dane":{"id":"9999",\
            "link":"http://localhost/demo/resource/9999"}}""";

    static final String OK_JSON = """
            {"status":200,"status_opis":"Ok"}""";

    private TestClients() { }

    static ResponseDefinitionBuilder jsonFile(String path) {
        return aResponse()
                .withStatus(HTTP_OK)
                .withHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                .withBodyFile(path);
    }

    static NoviCloudClient create(WireMockRuntimeInfo wm) {
        return NoviCloudClient.builder()
                .baseUrl(wm.getHttpBaseUrl())
                .retryPolicy(RetryPolicy.builder().enabled(false).build())
                .build(ACCOUNT, PASSWORD);
    }

    static NoviCloudClient withRetry(WireMockRuntimeInfo wm) {
        return NoviCloudClient.builder()
                .baseUrl(wm.getHttpBaseUrl())
                .retryPolicy(RetryPolicy.builder()
                        .maxAttempts(RETRY_MAX_ATTEMPTS)
                        .backoffStrategy(RetryPolicy.BackoffStrategy.FIXED)
                        .build())
                .build(ACCOUNT, PASSWORD);
    }
}
