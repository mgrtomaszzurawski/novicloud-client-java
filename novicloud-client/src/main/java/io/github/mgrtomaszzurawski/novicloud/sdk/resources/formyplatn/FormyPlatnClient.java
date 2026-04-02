/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.FormyPlatnApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseCreatedRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseFormyPlatnListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.FormaPlatnRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.FormaPlatn;

import java.util.List;
import java.util.Objects;

/**
 * Client for the {@code formyplatn} (payment forms) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#formyPlatn()}.
 *
 * <p>Supports: list, count, getById, create, update, deleteById (soft delete).
 * @since 1.0.0
 */
public final class FormyPlatnClient {

    private final ApiClient apiClient;
    private final FormyPlatnApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String ERR_LIST_PAGE = "Failed to list formyplatn page";
    private static final String ERR_GET_BY_ID = "Failed to fetch forma platn by id";
    private static final String ERR_CREATE = "Failed to create forma platn";
    private static final String ERR_UPDATE = "Failed to update forma platn";
    private static final String ERR_DELETE = "Failed to delete forma platn by id";
    private static final String ERR_LINK_CALL = "FormyPlatn link call failed";
    private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public FormyPlatnClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new FormyPlatnApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    // ADR-031: nazwa, typ - not filterable server-side, always null
    private ApiResponseFormyPlatnListRaw listPage(FormaPlatnQueryBuilder query) {
        FormaPlatnQueryBuilder safe = query != null ? query : FormaPlatnQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listFormyPlatn(accountName, safe.start(), null,
                    safe.id(), null, null), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all formaPlatn matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all records are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link FormaPlatn} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    public PagedResult<FormaPlatn> list(FormaPlatnQueryBuilder query) {
        FormaPlatnQueryBuilder safe = query != null ? query : FormaPlatnQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(FormaPlatn::from).toList();
                },
                FormyPlatnClient::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of formaPlatn matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public int count(FormaPlatnQueryBuilder query) {
        ApiResponseFormyPlatnListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<FormaPlatnRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single formaPlatn by its numeric ID.
     *
     * @param id the formaPlatn ID; must not be {@code null}
     * @return the forma platnosci record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no formaPlatn with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public FormaPlatn getById(Long id) {
        requireNotNull(id, FIELD_ID);
        FormaPlatnRaw raw = retryHandler.execute(() -> api.getFormaPlatnById(accountName, id), ERR_GET_BY_ID).getDane();
        return FormaPlatn.from(raw);
    }

    /**
     * Creates a new formaPlatn. Required fields are enforced by the builder factory method.
     *
     * @param builder the formaPlatn data; must not be {@code null}
     * @return the ID of the created record, or {@code null} if the server did not return one
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public String create(FormaPlatnCreateBuilder builder) {
        FormaPlatnRaw body = toFormaPlatn(builder);
        ApiResponseCreatedRaw response = retryHandler.executePost(() -> api.createFormaPlatn(accountName, body), ERR_CREATE);
        return response != null && response.getDane() != null ? response.getDane().getId() : null;
    }

    /**
     * Updates an existing formaPlatn. The {@code id} field in the builder identifies the record to update.
     *
     * @param builder the updated formaPlatn data; must not be {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no formaPlatn with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public void update(FormaPlatnUpdateBuilder builder) {
        FormaPlatnRaw body = toFormaPlatn(builder);
        retryHandler.run(() -> api.updateFormyPlatn(accountName, body), ERR_UPDATE);
    }

    /**
     * Deletes the formaPlatn with the given ID.
     *
     * <p><strong>Soft delete:</strong> this resource does not support physical deletion.
     * The record's {@code aktywny} flag is set to {@code false}; the row remains in the
     * database and still appears in unfiltered list results.
     * @param id the formaPlatn ID; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no formaPlatn with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public void deleteById(Long id) {
        requireNotNull(id, FIELD_ID);
        retryHandler.run(() -> api.deleteFormaPlatn(accountName, id), ERR_DELETE);
    }

    private ApiResponseFormyPlatnListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseFormyPlatnListRaw.class);
    }

    private ApiResponseFormyPlatnListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseFormyPlatnListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

    private static FormaPlatnRaw toFormaPlatn(FormaPlatnCreateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        FormaPlatnRaw formaPlatn = new FormaPlatnRaw();
        formaPlatn.setId(builder.id());
        formaPlatn.setNazwa(builder.nazwa());
        if (builder.typ() != null) {
            formaPlatn.setTyp(FormaPlatnRaw.TypEnum.fromValue(builder.typ()));
        }
        formaPlatn.setReszta(builder.reszta());
        return formaPlatn;
    }

    private static FormaPlatnRaw toFormaPlatn(FormaPlatnUpdateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        FormaPlatnRaw formaPlatn = new FormaPlatnRaw();
        formaPlatn.setId(builder.id());
        formaPlatn.setNazwa(builder.nazwa());
        if (builder.typ() != null) {
            formaPlatn.setTyp(FormaPlatnRaw.TypEnum.fromValue(builder.typ()));
        }
        formaPlatn.setReszta(builder.reszta());
        formaPlatn.setAktywny(builder.aktywny());
        return formaPlatn;
    }

    private static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + ERR_NULL_SUFFIX);
        }
    }

}
