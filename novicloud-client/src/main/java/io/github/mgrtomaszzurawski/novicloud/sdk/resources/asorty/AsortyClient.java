/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.AsortyApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseAsortyListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseCreatedRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.AsortyRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.LinkRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Asorty;

import java.util.List;
import java.util.Objects;

/**
 * Client for the {@code asorty} (assortment groups) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#asorty()}.
 *
 * <p>Supports full CRUD operations: list, count, getById, create, update, deleteById.
 * @since 1.0.0
 */
public final class AsortyClient {

    private final ApiClient apiClient;
    private final AsortyApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String ERR_LIST_PAGE = "Failed to list asorty page";
    private static final String ERR_GET_BY_ID = "Failed to fetch asorty by id";
    private static final String ERR_CREATE = "Failed to create asorty";
    private static final String ERR_UPDATE = "Failed to update asorty";
    private static final String ERR_DELETE = "Failed to delete asorty by id";
    private static final String ERR_LINK_CALL = "Asorty link call failed";
    private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public AsortyClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new AsortyApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    /**
     * Returns one page of asorty, applying the given filters.
     * If {@code query} is {@code null}, returns the first page with default page size.
     *
     * @param query filter and pagination parameters, or {@code null} for defaults
     * @return a single page response including data, total count, and pagination links
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    private ApiResponseAsortyListRaw listPage(AsortyQueryBuilder query) {
        AsortyQueryBuilder safe = query != null ? query : AsortyQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listAsorty(accountName, safe.start(), null, safe.fts(),
                    safe.id(), safe.nazwa(), safe.parentId()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable that transparently walks all pages of asorty matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all asorty are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link Asorty} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    public PagedResult<Asorty> list(AsortyQueryBuilder query) {
        AsortyQueryBuilder safe = query != null ? query : AsortyQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(Asorty::from).toList();
                },
                AsortyClient::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of asorty matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public int count(AsortyQueryBuilder query) {
        ApiResponseAsortyListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<AsortyRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single asorty record by its numeric ID.
     *
     * @param id the asorty ID; must not be {@code null}
     * @return the {@link Asorty} record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no asorty record with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public Asorty getById(Long id) {
        requireNotNull(id, FIELD_ID);
        AsortyRaw raw = retryHandler.execute(() -> api.getAsortyById(accountName, id), ERR_GET_BY_ID).getDane();
        return Asorty.from(raw);
    }

    /**
     * Creates a new asorty record. The {@code nazwa} field is required
     * (enforced by {@link AsortyCreateBuilder#builder(String)}).
     *
     * @param builder the asorty data; must not be {@code null}
     * @return the ID of the created asorty record, or {@code null} if the server did not return one
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public String create(AsortyCreateBuilder builder) {
        AsortyRaw body = toAsorty(builder);
        ApiResponseCreatedRaw response = retryHandler.executePost(() -> api.createAsorty(accountName, body), ERR_CREATE);
        return response != null && response.getDane() != null ? response.getDane().getId() : null;
    }

    /**
     * Updates an existing asorty record. The {@code id} field in the builder identifies the record to update.
     *
     * @param builder the updated asorty data; must not be {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no asorty record with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public void update(AsortyUpdateBuilder builder) {
        AsortyRaw body = toAsorty(builder);
        retryHandler.run(() -> api.updateAsorty(accountName, body), ERR_UPDATE);
    }

    /**
     * Deletes the asorty record with the given ID.
     *
     * @param id the asorty ID; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no asorty record with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public void deleteById(Long id) {
        requireNotNull(id, FIELD_ID);
        retryHandler.run(() -> api.deleteAsorty(accountName, id), ERR_DELETE);
    }

    private ApiResponseAsortyListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseAsortyListRaw.class);
    }

    private ApiResponseAsortyListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseAsortyListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

    private static AsortyRaw toAsorty(AsortyCreateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        AsortyRaw asorty = new AsortyRaw();
        asorty.setId(builder.id());
        asorty.setNazwa(builder.nazwa());
        if (builder.parentId() != null) {
            LinkRaw parent = new LinkRaw();
            parent.setId(builder.parentId());
            asorty.setParent(parent);
        }
        return asorty;
    }

    private static AsortyRaw toAsorty(AsortyUpdateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        AsortyRaw asorty = new AsortyRaw();
        asorty.setId(builder.id());
        asorty.setNazwa(builder.nazwa());
        if (builder.parentId() != null) {
            LinkRaw parent = new LinkRaw();
            parent.setId(builder.parentId());
            asorty.setParent(parent);
        }
        return asorty;
    }

    private static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + ERR_NULL_SUFFIX);
        }
    }

}
