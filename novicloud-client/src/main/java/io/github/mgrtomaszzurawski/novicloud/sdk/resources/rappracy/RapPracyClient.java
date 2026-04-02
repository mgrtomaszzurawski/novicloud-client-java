/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.RapPracyApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseRapPracyListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.RaportPracyRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.RaportPracy;

import java.util.List;

/**
 * Client for the {@code rappracy} (work reports) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#rapPracy()}.
 *
 * <p>Supports: list, count (read-only, no getById).
 * @since 1.0.0
 */
public final class RapPracyClient {

    private final ApiClient apiClient;
    private final RapPracyApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_LINK_CALL = "RapPracy link call failed";
    private static final String ERR_LIST_PAGE = "Failed to list rappracy page";

    private final RetryHandler retryHandler;

    public RapPracyClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new RapPracyApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    private ApiResponseRapPracyListRaw listPage(RapPracyQueryBuilder query) {
        RapPracyQueryBuilder safe = query != null ? query : RapPracyQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listRapPracy(accountName, safe.start(), null,
                    safe.dataPocz(), safe.dataKonc(), safe.grupowanie(),
                    safe.sklepId(), safe.kasaId(), safe.kasjerId()), ERR_LIST_PAGE);
    }

    /**
     * Returns the total number of raport pracy matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public int count(RapPracyQueryBuilder query) {
        ApiResponseRapPracyListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<RaportPracyRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Returns a lazy iterable over all raport pracy matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all records are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link RaportPracy} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    public PagedResult<RaportPracy> list(RapPracyQueryBuilder query) {
        RapPracyQueryBuilder safe = query != null ? query : RapPracyQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(RaportPracy::from).toList();
                },
                RapPracyClient::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    private ApiResponseRapPracyListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseRapPracyListRaw.class);
    }

    private ApiResponseRapPracyListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseRapPracyListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

}
