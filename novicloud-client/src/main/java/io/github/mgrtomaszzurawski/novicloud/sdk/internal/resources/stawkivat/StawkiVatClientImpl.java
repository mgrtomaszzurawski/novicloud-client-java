/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.stawkivat;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat.StawkiVatClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat.StawkaVatCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat.StawkaVatQueryBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.StawkiVatApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseCreatedRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseStawkiVatListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.StawkaVatRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.mapper.RawMappers;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.StawkaVat;

import java.util.List;
import java.util.Objects;

/**
 * Client for the {@code stawkivat} (VAT rates) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#stawkiVat()}.
 *
 * <p>Supports: list, count, getById, create, deleteById (no update - broken server-side, see ADR-022).
 * @since 2.0.0
 */
public final class StawkiVatClientImpl implements StawkiVatClient {

    private final ApiClient apiClient;
    private final StawkiVatApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String RESOURCE_NAME = "stawka vat";
    private static final String ERR_LIST_PAGE = "Failed to list stawkivat page";
    private static final String ERR_GET_BY_ID = "Failed to fetch stawka vat by id";
    private static final String ERR_CREATE = "Failed to create stawka vat";
    private static final String ERR_DELETE = "Failed to delete stawka vat by id";
    private static final String ERR_LINK_CALL = "StawkiVat link call failed";
    private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public StawkiVatClientImpl(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new StawkiVatApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    private ApiResponseStawkiVatListRaw listPage(StawkaVatQueryBuilder query) {
        StawkaVatQueryBuilder safe = query != null ? query : StawkaVatQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listStawkiVat(accountName, safe.start(), null, safe.id()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all stawki VAT matching the given filters.
     * Pages are fetched on demand. If {@code query} is {@code null}, all records are returned.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link StawkaVat} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    @Override
    public PagedResult<StawkaVat> list(StawkaVatQueryBuilder query) {
        StawkaVatQueryBuilder safe = query != null ? query : StawkaVatQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(RawMappers::toStawkaVat).toList();
                },
                StawkiVatClientImpl::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of stawki VAT matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    @Override
    public int count(StawkaVatQueryBuilder query) {
        ApiResponseStawkiVatListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<StawkaVatRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single stawka VAT by its numeric ID.
     *
     * @param id the stawka VAT ID; must not be {@code null}
     * @return the {@link StawkaVat} record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no stawka VAT with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public StawkaVat getById(Long id) {
        requireNotNull(id, FIELD_ID);
        StawkaVatRaw raw = retryHandler.execute(() -> api.getStawkaVatById(accountName, id), ERR_GET_BY_ID).getDane();
        return RawMappers.toStawkaVat(NoviCloudException.requireDane(raw, RESOURCE_NAME, id));
    }

    /**
     * Creates a new stawka VAT. Required fields are enforced by the builder factory method.
     *
     * @param builder the stawka VAT data; must not be {@code null}
     * @return the ID of the created record, or {@code null} if the server did not return one
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    @Override
    public String create(StawkaVatCreateBuilder builder) {
        StawkaVatRaw body = toStawkaVat(builder);
        ApiResponseCreatedRaw response = retryHandler.executePost(() -> api.createStawkaVat(accountName, body), ERR_CREATE);
        return response != null && response.getDane() != null ? response.getDane().getId() : null;
    }

    // update() intentionally absent - PUT /stawkivat is broken server-side (Short/Long ClassCastException).
    // See ADR-022. Use the web panel to change VAT rate metadata until the vendor fixes the server.

    /**
     * Deletes the stawka VAT with the given ID.
     *
     * @param id the stawka VAT ID; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no stawka VAT with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public void deleteById(Long id) {
        requireNotNull(id, FIELD_ID);
        retryHandler.run(() -> api.deleteStawkaVat(accountName, id), ERR_DELETE);
    }

    private ApiResponseStawkiVatListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseStawkiVatListRaw.class);
    }

    private ApiResponseStawkiVatListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseStawkiVatListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

    private static StawkaVatRaw toStawkaVat(StawkaVatCreateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        StawkaVatRaw stawkaVat = new StawkaVatRaw();
        stawkaVat.setId(builder.id());
        stawkaVat.setOpis(builder.opis());
        if (builder.etykieta() != null) {
            stawkaVat.setEtykieta(StawkaVatRaw.EtykietaEnum.fromValue(builder.etykieta()));
        }
        return stawkaVat;
    }

    private static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + ERR_NULL_SUFFIX);
        }
    }

}
