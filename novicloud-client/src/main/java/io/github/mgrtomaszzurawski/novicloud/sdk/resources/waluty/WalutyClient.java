/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.WalutyApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseCreatedRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseWalutyListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.WalutaRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Waluta;

import java.util.List;
import java.util.Objects;

/**
 * Client for the {@code waluty} (currencies) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#waluty()}.
 *
 * <p>Supports CRUD operations with soft delete: list, count, getById, create, update, deleteById.
 * @since 1.0.0
 */
public final class WalutyClient {

    private final ApiClient apiClient;
    private final WalutyApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String ERR_LIST_PAGE = "Failed to list waluty page";
    private static final String ERR_GET_BY_ID = "Failed to fetch waluta by id";
    private static final String ERR_CREATE = "Failed to create waluta";
    private static final String ERR_UPDATE = "Failed to update waluta";
    private static final String ERR_DELETE = "Failed to delete waluta by id";
    private static final String ERR_LINK_CALL = "Waluty link call failed";
    private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public WalutyClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new WalutyApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    // ADR-031: domyslna - broken server-side, always null
    private ApiResponseWalutyListRaw listPage(WalutaQueryBuilder query) {
        WalutaQueryBuilder safe = query != null ? query : WalutaQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listWaluty(accountName, safe.start(), null, safe.fts(),
                    safe.id(), safe.nazwa(), safe.kod(), safe.kurs(), null, safe.aktywny()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all waluty matching the given filters.
     * Pages are fetched on demand. If {@code query} is {@code null}, all waluty are returned.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link Waluta} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    public PagedResult<Waluta> list(WalutaQueryBuilder query) {
        WalutaQueryBuilder safe = query != null ? query : WalutaQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(Waluta::from).toList();
                },
                WalutyClient::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of waluty matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public int count(WalutaQueryBuilder query) {
        ApiResponseWalutyListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<WalutaRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single waluta by its numeric ID.
     *
     * @param id the waluta ID; must not be {@code null}
     * @return the {@link Waluta} record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no waluta with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public Waluta getById(Long id) {
        requireNotNull(id, FIELD_ID);
        WalutaRaw raw = retryHandler.execute(() -> api.getWalutaById(accountName, id), ERR_GET_BY_ID).getDane();
        return Waluta.from(raw);
    }

    /**
     * Creates a new waluta. Required fields are enforced by the builder factory method.
     *
     * @param builder the waluta data; must not be {@code null}
     * @return the ID of the created waluta, or {@code null} if the server did not return one
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public String create(WalutaCreateBuilder builder) {
        WalutaRaw body = toWaluta(builder);
        ApiResponseCreatedRaw response = retryHandler.executePost(() -> api.createWaluta(accountName, body), ERR_CREATE);
        return response != null && response.getDane() != null ? response.getDane().getId() : null;
    }

    /**
     * Updates an existing waluta. The {@code id} field in the builder identifies the record to update.
     *
     * @param builder the updated waluta data; must not be {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no waluta with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public void update(WalutaUpdateBuilder builder) {
        WalutaRaw body = toWaluta(builder);
        retryHandler.run(() -> api.updateWaluty(accountName, body), ERR_UPDATE);
    }

    /**
     * Deletes the waluta with the given ID.
     *
     * <p><strong>Soft delete:</strong> this resource does not support physical deletion.
     * The record's {@code aktywny} flag is set to {@code false}; the row remains in the
     * database and still appears in unfiltered list results. Use {@code .aktywny(true)} in the query
     * builder to retrieve only active records.
     * @param id the waluta ID; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no waluta with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public void deleteById(Long id) {
        requireNotNull(id, FIELD_ID);
        retryHandler.run(() -> api.deleteWaluta(accountName, id), ERR_DELETE);
    }

    private ApiResponseWalutyListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseWalutyListRaw.class);
    }

    private ApiResponseWalutyListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseWalutyListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

    private static WalutaRaw toWaluta(WalutaCreateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        WalutaRaw waluta = new WalutaRaw();
        waluta.setId(builder.id());
        waluta.setNazwa(builder.nazwa());
        waluta.setKod(builder.kod());
        waluta.setKurs(builder.kurs());
        waluta.setDomyslna(builder.domyslna());
        waluta.setAktywny(builder.aktywny());
        return waluta;
    }

    private static WalutaRaw toWaluta(WalutaUpdateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        WalutaRaw waluta = new WalutaRaw();
        waluta.setId(builder.id());
        waluta.setNazwa(builder.nazwa());
        waluta.setKod(builder.kod());
        waluta.setKurs(builder.kurs());
        waluta.setDomyslna(builder.domyslna());
        waluta.setAktywny(builder.aktywny());
        return waluta;
    }

    private static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + ERR_NULL_SUFFIX);
        }
    }

}
