/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.sprzedaz;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.SprzedazApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseSprzedazListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.SprzedazRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Sprzedaz;

import java.util.List;

/**
 * Client for the {@code sprzedaz} (sales) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#sprzedaz()}.
 *
 * <p>Supports: list, count, getById (read-only).
 * @since 1.0.0
 */
public final class SprzedazClient {

    private final ApiClient apiClient;
    private final SprzedazApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String ERR_LIST_PAGE = "Failed to list sprzedaz page";
    private static final String ERR_GET_BY_ID = "Failed to fetch sprzedaz by id";
    private static final String ERR_LINK_CALL = "Sprzedaz link call failed";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public SprzedazClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new SprzedazApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    // ADR-031: nrRapDob, cenaPrzedRab - broken server-side, always null
    private ApiResponseSprzedazListRaw listPage(SprzedazQueryBuilder query) {
        SprzedazQueryBuilder safe = query != null ? query : SprzedazQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listSprzedaz(accountName, safe.start(), null, null,
                    safe.id(), safe.data(), safe.nrDok(), safe.typDok(),
                    safe.nrSystemowy(), safe.nrFiskalny(), null,
                    safe.ilosc(), safe.cena(), null, safe.stawkaVat(),
                    safe.brutto(), safe.podatek(), safe.rabat(),
                    safe.towarId(), safe.sklepId(), safe.kasaId(), safe.kasjerId(), safe.kontrahentId()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all sprzedaz matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all records are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link Sprzedaz} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    public PagedResult<Sprzedaz> list(SprzedazQueryBuilder query) {
        SprzedazQueryBuilder safe = query != null ? query : SprzedazQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(Sprzedaz::from).toList();
                },
                SprzedazClient::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of sprzedaz matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public int count(SprzedazQueryBuilder query) {
        ApiResponseSprzedazListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<SprzedazRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single sprzedaz by its numeric ID.
     *
     * @param id the sprzedaz ID; must not be {@code null}
     * @return the sprzedaz record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no sprzedaz with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public Sprzedaz getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(FIELD_ID + ERR_NULL_SUFFIX);
        }
        SprzedazRaw raw = retryHandler.execute(() -> api.getSprzedazById(accountName, id), ERR_GET_BY_ID).getDane();
        return Sprzedaz.from(raw);
    }

    private ApiResponseSprzedazListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseSprzedazListRaw.class);
    }

    private ApiResponseSprzedazListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseSprzedazListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

}
