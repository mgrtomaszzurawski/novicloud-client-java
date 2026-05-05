/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.kraje;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje.KrajeClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje.KrajCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje.KrajQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje.KrajUpdateBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.KrajeApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseCreatedRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseKrajeListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.KrajRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.LinkRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.mapper.RawMappers;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kraj;

import java.util.List;
import java.util.Objects;

/**
 * Client for the {@code kraje} (countries) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#kraje()}.
 *
 * <p>Supports full CRUD operations: list, count, getById, create, update, deleteById.
 * @since 2.0.0
 */
public final class KrajeClientImpl implements KrajeClient {

    private final ApiClient apiClient;
    private final KrajeApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String RESOURCE_NAME = "kraj";
    private static final String ERR_LIST_PAGE = "Failed to list kraje page";
    private static final String ERR_GET_BY_ID = "Failed to fetch kraj by id";
    private static final String ERR_CREATE = "Failed to create kraj";
    private static final String ERR_UPDATE = "Failed to update kraj";
    private static final String ERR_DELETE = "Failed to delete kraj by id";
    private static final String ERR_LINK_CALL = "Kraje link call failed";
    private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public KrajeClientImpl(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new KrajeApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    private ApiResponseKrajeListRaw listPage(KrajQueryBuilder query) {
        KrajQueryBuilder safe = query != null ? query : KrajQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listKraje(accountName, safe.start(), null, safe.fts(),
                    safe.id(), safe.nazwa(), safe.kod(), safe.walutaId()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all kraj matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all records are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link Kraj} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    @Override
    public PagedResult<Kraj> list(KrajQueryBuilder query) {
        KrajQueryBuilder safe = query != null ? query : KrajQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(RawMappers::toKraj).toList();
                },
                KrajeClientImpl::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of kraj matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    @Override
    public int count(KrajQueryBuilder query) {
        ApiResponseKrajeListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<KrajRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single kraj by its numeric ID.
     *
     * @param id the kraj ID; must not be {@code null}
     * @return the kraj record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no kraj with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public Kraj getById(Long id) {
        requireNotNull(id, FIELD_ID);
        KrajRaw raw = retryHandler.execute(() -> api.getKrajById(accountName, id), ERR_GET_BY_ID).getDane();
        return RawMappers.toKraj(NoviCloudException.requireDane(raw, RESOURCE_NAME, id));
    }

    /**
     * Creates a new kraj. Required fields are enforced by the builder factory method.
     *
     * @param builder the kraj data; must not be {@code null}
     * @return the ID of the created record, or {@code null} if the server did not return one
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    @Override
    public String create(KrajCreateBuilder builder) {
        KrajRaw body = toKraj(builder);
        ApiResponseCreatedRaw response = retryHandler.executePost(() -> api.createKraj(accountName, body), ERR_CREATE);
        return response != null && response.getDane() != null ? response.getDane().getId() : null;
    }

    /**
     * Updates an existing kraj. The {@code id} field in the builder identifies the record to update.
     *
     * @param builder the updated kraj data; must not be {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no kraj with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public void update(KrajUpdateBuilder builder) {
        KrajRaw body = toKraj(builder);
        retryHandler.run(() -> api.updateKraje(accountName, body), ERR_UPDATE);
    }

    /**
     * Deletes the kraj with the given ID.
     *
     * @param id the kraj ID; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no kraj with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public void deleteById(Long id) {
        requireNotNull(id, FIELD_ID);
        retryHandler.run(() -> api.deleteKraj(accountName, id), ERR_DELETE);
    }

    private ApiResponseKrajeListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseKrajeListRaw.class);
    }

    private ApiResponseKrajeListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseKrajeListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

    private static KrajRaw toKraj(KrajCreateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        KrajRaw kraj = new KrajRaw();
        kraj.setId(builder.id());
        kraj.setNazwa(builder.nazwa());
        kraj.setKod(builder.kod());
        if (builder.walutaId() != null) {
            LinkRaw waluta = new LinkRaw();
            waluta.setId(builder.walutaId());
            kraj.setWaluta(waluta);
        }
        return kraj;
    }

    private static KrajRaw toKraj(KrajUpdateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        KrajRaw kraj = new KrajRaw();
        kraj.setId(builder.id());
        kraj.setNazwa(builder.nazwa());
        kraj.setKod(builder.kod());
        if (builder.walutaId() != null) {
            LinkRaw waluta = new LinkRaw();
            waluta.setId(builder.walutaId());
            kraj.setWaluta(waluta);
        }
        return kraj;
    }

    private static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + ERR_NULL_SUFFIX);
        }
    }

}
