/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.rapsprzed;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.RapSprzedApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseRapSprzedListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.RaportSprzedazyRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.RaportSprzedazy;

import java.util.List;

/**
 * Client for the {@code rapsprzed} (sales reports) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#rapSprzed()}.
 *
 * <p>Supports: list, count (read-only, no getById).
 * @since 1.0.0
 */
public final class RapSprzedClient {

    private final ApiClient apiClient;
    private final RapSprzedApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_LINK_CALL = "RapSprzed link call failed";
    private static final String ERR_LIST_PAGE = "Failed to list rapsprzed page";

    private final RetryHandler retryHandler;

    public RapSprzedClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new RapSprzedApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    private ApiResponseRapSprzedListRaw listPage(RapSprzedQueryBuilder query) {
        RapSprzedQueryBuilder safe = query != null ? query : RapSprzedQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listRapSprzed(accountName, safe.start(), null,
                    safe.dataPocz(), safe.dataKonc(), safe.grupowanie(), safe.skladniki(),
                    safe.towarId(), safe.asortId(), safe.sklepId(), safe.kasaId(),
                    safe.kasjerId(), safe.kontrahentId(), safe.formaPlatnId()), ERR_LIST_PAGE);
    }

    /**
     * Returns the total number of raport sprzedazy matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public int count(RapSprzedQueryBuilder query) {
        ApiResponseRapSprzedListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<RaportSprzedazyRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Returns a lazy iterable over all raport sprzedazy matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all records are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link RaportSprzedazy} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    public PagedResult<RaportSprzedazy> list(RapSprzedQueryBuilder query) {
        RapSprzedQueryBuilder safe = query != null ? query : RapSprzedQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(RaportSprzedazy::from).toList();
                },
                RapSprzedClient::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    private ApiResponseRapSprzedListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseRapSprzedListRaw.class);
    }

    private ApiResponseRapSprzedListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseRapSprzedListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

}
