/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasy;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.KasyApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseKasyListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.KasaRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kasa;

import java.util.List;

/**
 * Client for the {@code kasy} (cash registers) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#kasy()}.
 *
 * <p>Supports: list, count, getById (read-only).
 * @since 1.0.0
 */
public final class KasyClient {

    private final ApiClient apiClient;
    private final KasyApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String ERR_LINK_CALL = "Kasy link call failed";
    private static final String ERR_LIST_PAGE = "Failed to list kasy page";
    private static final String ERR_GET_BY_ID = "Failed to fetch kasa by id";
    private static final String FIELD_ID = "id";

    private final RetryHandler retryHandler;

    public KasyClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new KasyApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    // ADR-031: ecr - broken server-side, always null
    private ApiResponseKasyListRaw listPage(KasaQueryBuilder query) {
        KasaQueryBuilder safe = query != null ? query : KasaQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listKasy(accountName, safe.start(), null, null,
                    safe.id(), safe.nazwa(), safe.numer(), null, safe.ostatniaSync(), safe.aktywny()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all kasa matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all records are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link Kasa} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    public PagedResult<Kasa> list(KasaQueryBuilder query) {
        KasaQueryBuilder safe = query != null ? query : KasaQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(Kasa::from).toList();
                },
                KasyClient::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of kasa matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public int count(KasaQueryBuilder query) {
        ApiResponseKasyListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<KasaRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single kasa by its numeric ID.
     *
     * @param id the kasa ID; must not be {@code null}
     * @return the kasa record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no kasa with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public Kasa getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(FIELD_ID + ERR_NULL_SUFFIX);
        }
        KasaRaw raw = retryHandler.execute(() -> api.getKasaById(accountName, id), ERR_GET_BY_ID).getDane();
        return Kasa.from(raw);
    }

    private ApiResponseKasyListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseKasyListRaw.class);
    }

    private ApiResponseKasyListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseKasyListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

}
