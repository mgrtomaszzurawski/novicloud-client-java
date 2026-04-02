/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.stanymag;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.StanyMagApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseStanyMagListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.LinkRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.StanMagRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.StanMag;

import java.util.List;
import java.util.Objects;

/**
 * Client for the {@code stanymag} (stock levels) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#stanyMag()}.
 *
 * <p>Supports: list, count, listByTowar, getByTowarAndSklep, update.
 * @since 1.0.0
 */
public final class StanyMagClient {

    private final ApiClient apiClient;
    private final StanyMagApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String ERR_LINK_CALL = "StanyMag link call failed";
    private static final String ERR_LIST_PAGE = "Failed to list stanymag page";
    private static final String ERR_UPDATE = "Failed to update stan mag";
    private static final String ERR_LIST_BY_TOWAR = "Failed to list stany mag by towar";
    private static final String ERR_GET_BY_TOWAR_AND_SKLEP = "Failed to get stan mag by towar and sklep";
    private static final String ERR_TOWAR_ID_NULL = "idTowaru must not be null";
    private static final String ERR_SKLEP_ID_NULL = "idSklepu must not be null";

    private final RetryHandler retryHandler;

    public StanyMagClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new StanyMagApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    private ApiResponseStanyMagListRaw listPage(StanMagQueryBuilder query) {
        StanMagQueryBuilder safe = query != null ? query : StanMagQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listStanyMag(accountName, safe.start(), null,
                    safe.towarId(), safe.sklepId(), safe.naDzien()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all stany mag matching the given filters.
     * Pages are fetched on demand. If {@code query} is {@code null}, all records are returned.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link StanMag} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    public PagedResult<StanMag> list(StanMagQueryBuilder query) {
        StanMagQueryBuilder safe = query != null ? query : StanMagQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(StanMag::from).toList();
                },
                StanyMagClient::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of stany mag records matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public int count(StanMagQueryBuilder query) {
        ApiResponseStanyMagListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<StanMagRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Returns all stock records for a given towar, optionally filtered to a specific date.
     *
     * @param idTowaru the towar ID to filter by; must not be {@code null}
     * @param naDzien  the date in {@code YYYY-MM-DD} format to check stock as-of, or {@code null} for current stock
     * @return list of {@link StanMag} records for the given towar
     * @throws IllegalArgumentException if {@code idTowaru} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public java.util.List<StanMag> listByTowar(Long idTowaru, String naDzien) {
        if (idTowaru == null) {
            throw new IllegalArgumentException(ERR_TOWAR_ID_NULL);
        }
        ApiResponseStanyMagListRaw response = retryHandler.execute(() -> api.listStanyMagByTowar(accountName, idTowaru, naDzien), ERR_LIST_BY_TOWAR);
        java.util.List<StanMagRaw> dane = response.getDane();
        if (dane == null) {
            return java.util.List.of();
        }
        return dane.stream().map(StanMag::from).toList();
    }

    /**
     * Fetches the stock record for a specific towar at a specific sklep, optionally at a given date.
     *
     * @param idTowaru the towar ID; must not be {@code null}
     * @param idSklepu the sklep ID; must not be {@code null}
     * @param naDzien  the date in {@code YYYY-MM-DD} format to check stock as-of, or {@code null} for current stock
     * @return the {@link StanMag} record; never {@code null}
     * @throws IllegalArgumentException if {@code idTowaru} or {@code idSklepu} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no stock record exists for the given towar/sklep combination
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public StanMag getByTowarAndSklep(Long idTowaru, Long idSklepu, String naDzien) {
        if (idTowaru == null) {
            throw new IllegalArgumentException(ERR_TOWAR_ID_NULL);
        }
        if (idSklepu == null) {
            throw new IllegalArgumentException(ERR_SKLEP_ID_NULL);
        }
        StanMagRaw raw = retryHandler.execute(() -> api.getStanMagByTowarAndSklep(accountName, idTowaru, idSklepu, naDzien), ERR_GET_BY_TOWAR_AND_SKLEP).getDane();
        return StanMag.from(raw);
    }

    /**
     * Updates the stock level for the towar/sklep combination identified in the builder.
     * Both {@code towarId} and {@code sklepId} must be set in the builder.
     *
     * @param builder the updated stock data; must not be {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if the towar/sklep combination does not exist
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public void update(StanMagUpdateBuilder builder) {
        StanMagRaw body = toStanMag(builder);
        retryHandler.run(() -> api.updateStanMag(accountName, body), ERR_UPDATE);
    }

    private ApiResponseStanyMagListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseStanyMagListRaw.class);
    }

    private ApiResponseStanyMagListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseStanyMagListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

    private static StanMagRaw toStanMag(StanMagUpdateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        StanMagRaw stanMag = new StanMagRaw();
        stanMag.setIlosc(builder.ilosc());
        if (builder.towarId() != null) {
            LinkRaw towar = new LinkRaw();
            towar.setId(builder.towarId());
            stanMag.setTowar(towar);
        }
        if (builder.sklepId() != null) {
            LinkRaw sklep = new LinkRaw();
            sklep.setId(builder.sklepId());
            stanMag.setSklep(sklep);
        }
        return stanMag;
    }

}
