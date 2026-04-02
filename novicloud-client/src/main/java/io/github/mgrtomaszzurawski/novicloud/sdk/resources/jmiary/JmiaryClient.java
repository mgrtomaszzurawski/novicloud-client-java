/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.JmiaryApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseCreatedRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseJmiaryListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.JmiaryRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Jmiary;

import java.util.List;
import java.util.Objects;

/**
 * Client for the {@code jmiary} (units of measure) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#jmiary()}.
 *
 * <p>Supports full CRUD operations: list, count, getById, create, update, deleteById.
 * @since 1.0.0
 */
public final class JmiaryClient {

    private final ApiClient apiClient;
    private final JmiaryApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String ERR_LIST_PAGE = "Failed to list jmiary page";
    private static final String ERR_GET_BY_ID = "Failed to fetch jmiary by id";
    private static final String ERR_CREATE = "Failed to create jmiary";
    private static final String ERR_UPDATE = "Failed to update jmiary";
    private static final String ERR_DELETE = "Failed to delete jmiary by id";
    private static final String ERR_LINK_CALL = "Jmiary link call failed";
    private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public JmiaryClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new JmiaryApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    private ApiResponseJmiaryListRaw listPage(JmiaryQueryBuilder query) {
        JmiaryQueryBuilder safe = query != null ? query : JmiaryQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listJmiary(accountName, safe.start(), null, safe.fts(),
                    safe.id(), safe.nazwa(), safe.precyzja()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all jmiary matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all jmiary are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@code Jmiara} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    public PagedResult<Jmiary> list(JmiaryQueryBuilder query) {
        JmiaryQueryBuilder safe = query != null ? query : JmiaryQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(Jmiary::from).toList();
                },
                JmiaryClient::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of jmiary matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public int count(JmiaryQueryBuilder query) {
        ApiResponseJmiaryListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<JmiaryRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single jmiary record by its numeric ID.
     *
     * @param id the jmiary ID; must not be {@code null}
     * @return the jmiary record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no jmiary record with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public Jmiary getById(Long id) {
        requireNotNull(id, FIELD_ID);
        JmiaryRaw raw = retryHandler.execute(() -> api.getJmiaryById(accountName, id), ERR_GET_BY_ID).getDane();
        return Jmiary.from(raw);
    }

    /**
     * Creates a new jmiary record. Required fields are enforced by the builder factory method.
     *
     * @param builder the jmiary data; must not be {@code null}
     * @return the ID of the created jmiary record, or {@code null} if the server did not return one
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public String create(JmiaryCreateBuilder builder) {
        JmiaryRaw body = toJmiary(builder);
        ApiResponseCreatedRaw response = retryHandler.executePost(() -> api.createJmiary(accountName, body), ERR_CREATE);
        return response != null && response.getDane() != null ? response.getDane().getId() : null;
    }

    /**
     * Updates an existing jmiary record. The {@code id} field in the builder identifies the record to update.
     *
     * @param builder the updated jmiary data; must not be {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no jmiary record with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public void update(JmiaryUpdateBuilder builder) {
        JmiaryRaw body = toJmiary(builder);
        retryHandler.run(() -> api.updateJmiary(accountName, body), ERR_UPDATE);
    }

    /**
     * Deletes the jmiary record with the given ID.
     *
     * @param id the jmiary ID; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no jmiary record with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public void deleteById(Long id) {
        requireNotNull(id, FIELD_ID);
        retryHandler.run(() -> api.deleteJmiary(accountName, id), ERR_DELETE);
    }

    private ApiResponseJmiaryListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseJmiaryListRaw.class);
    }

    private ApiResponseJmiaryListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseJmiaryListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

    private static JmiaryRaw toJmiary(JmiaryCreateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        JmiaryRaw jmiary = new JmiaryRaw();
        jmiary.setId(builder.id());
        jmiary.setNazwa(builder.nazwa());
        if (builder.precyzja() != null) {
            jmiary.setPrecyzja(JmiaryRaw.PrecyzjaEnum.fromValue(builder.precyzja()));
        }
        return jmiary;
    }

    private static JmiaryRaw toJmiary(JmiaryUpdateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        JmiaryRaw jmiary = new JmiaryRaw();
        jmiary.setId(builder.id());
        jmiary.setNazwa(builder.nazwa());
        if (builder.precyzja() != null) {
            jmiary.setPrecyzja(JmiaryRaw.PrecyzjaEnum.fromValue(builder.precyzja()));
        }
        return jmiary;
    }

    private static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + ERR_NULL_SUFFIX);
        }
    }

}
