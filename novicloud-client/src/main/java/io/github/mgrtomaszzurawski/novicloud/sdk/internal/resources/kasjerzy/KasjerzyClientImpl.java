/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.kasjerzy;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasjerzy.KasjerzyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasjerzy.KasjerQueryBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.KasjerzyApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseKasjerzyListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.KasjerRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.mapper.RawMappers;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kasjer;

import java.util.List;

/**
 * Client for the {@code kasjerzy} (cashiers) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#kasjerzy()}.
 *
 * <p>Supports: list, count, getById (read-only).
 * @since 2.0.0
 */
public final class KasjerzyClientImpl implements KasjerzyClient {

    private final ApiClient apiClient;
    private final KasjerzyApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String ERR_LINK_CALL = "Kasjerzy link call failed";
    private static final String RESOURCE_NAME = "kasjer";
    private static final String ERR_LIST_PAGE = "Failed to list kasjerzy page";
    private static final String ERR_GET_BY_ID = "Failed to fetch kasjer by id";
    private static final String FIELD_ID = "id";

    private final RetryHandler retryHandler;

    public KasjerzyClientImpl(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new KasjerzyApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    private ApiResponseKasjerzyListRaw listPage(KasjerQueryBuilder query) {
        KasjerQueryBuilder safe = query != null ? query : KasjerQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listKasjerzy(accountName, safe.start(), null, null,
                    safe.id(), safe.nazwisko(), safe.kodKasjera(), safe.aktywny()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all kasjer matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all records are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link Kasjer} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    @Override
    public PagedResult<Kasjer> list(KasjerQueryBuilder query) {
        KasjerQueryBuilder safe = query != null ? query : KasjerQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(RawMappers::toKasjer).toList();
                },
                KasjerzyClientImpl::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of kasjer matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    @Override
    public int count(KasjerQueryBuilder query) {
        ApiResponseKasjerzyListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<KasjerRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single kasjer by its numeric ID.
     *
     * @param id the kasjer ID; must not be {@code null}
     * @return the kasjer record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no kasjer with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public Kasjer getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(FIELD_ID + ERR_NULL_SUFFIX);
        }
        KasjerRaw raw = retryHandler.execute(() -> api.getKasjerById(accountName, id), ERR_GET_BY_ID).getDane();
        return RawMappers.toKasjer(NoviCloudException.requireDane(raw, RESOURCE_NAME, id));
    }

    private ApiResponseKasjerzyListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseKasjerzyListRaw.class);
    }

    private ApiResponseKasjerzyListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseKasjerzyListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

}
