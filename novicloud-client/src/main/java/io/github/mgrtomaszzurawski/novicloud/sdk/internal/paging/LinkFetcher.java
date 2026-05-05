/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.internal.paging;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNetworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * Internal helper used by the 18 resource clients to follow {@code links.next}
 * pagination URLs.
 *
 * <p>Lives in a non-exported package since 2.0.0 (see ADR-057). Not part of the
 * public SDK surface.
 */
public final class LinkFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(LinkFetcher.class);
    private static final int HTTP_OK_MIN = 200;
    private static final int HTTP_OK_MAX = 299;
    private static final int HTTP_STATUS_UNKNOWN = 0;
    private static final String ACCEPT_HEADER = "Accept";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ERR_LINK_CALL = "Link call failed";
    private static final String ERR_READ_PAGE = "Failed to read page";
    private static final String ERR_INTERRUPTED = "Request interrupted";
    private static final String LOG_FOLLOWING_LINK = "Following pagination link: {}";

    private LinkFetcher() { }

    /**
     * Fetches and deserializes a page response from an absolute pagination URL.
     *
     * @param link         the absolute URL (from {@code links.next} in a page response)
     * @param apiClient    the configured API client (provides httpClient, objectMapper, interceptor, timeout)
     * @param responseType the target class for Jackson deserialization
     * @param <P>          the page response type (e.g. {@code ApiResponseTowaryListRaw})
     * @return the deserialized page response
     * @throws ApiException on HTTP error responses (with body preserved as of 2.0.0)
     * @throws NoviCloudNetworkException on I/O or thread interruption
     */
    public static <P> P fetch(String link, ApiClient apiClient, Class<P> responseType) throws ApiException {
        ObjectMapper objectMapper = apiClient.getObjectMapper();
        Consumer<HttpRequest.Builder> interceptor = apiClient.getRequestInterceptor();
        Duration timeout = apiClient.getReadTimeout();

        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(link));
        builder.header(ACCEPT_HEADER, APPLICATION_JSON);
        if (timeout != null) {
            builder.timeout(timeout);
        }
        if (interceptor != null) {
            interceptor.accept(builder);
        }
        HttpRequest request = builder.GET().build();
        LOG.debug(LOG_FOLLOWING_LINK, link);
        try {
            HttpResponse<InputStream> response = apiClient.getHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofInputStream());
            try (InputStream body = response.body()) {
                if (response.statusCode() < HTTP_OK_MIN || response.statusCode() > HTTP_OK_MAX) {
                    String bodyText = new String(body.readAllBytes(), StandardCharsets.UTF_8);
                    throw new ApiException(response.statusCode(), ERR_LINK_CALL, response.headers(), bodyText);
                }
                return objectMapper.readValue(body, responseType);
            }
        } catch (IOException e) {
            throw new NoviCloudNetworkException(ERR_READ_PAGE, e, HTTP_STATUS_UNKNOWN, null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NoviCloudNetworkException(ERR_INTERRUPTED, e, HTTP_STATUS_UNKNOWN, null);
        }
    }
}
