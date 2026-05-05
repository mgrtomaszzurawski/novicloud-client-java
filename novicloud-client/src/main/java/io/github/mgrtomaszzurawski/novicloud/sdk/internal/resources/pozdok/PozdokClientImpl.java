/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.pozdok;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.pozdok.PozdokClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.pozdok.PozdokQueryBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.PozdokApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponsePozdokListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.PozycjaDokumentuRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.mapper.RawMappers;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.PozycjaDokumentu;

import java.util.List;

/**
 * Client for the {@code pozdok} (document positions) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#pozdok()}.
 *
 * <p>Supports: list, count, getById (read-only).
 * @since 2.0.0
 */
public final class PozdokClientImpl implements PozdokClient {

    private final ApiClient apiClient;
    private final PozdokApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String RESOURCE_NAME = "pozycja dokumentu";
    private static final String ERR_LIST_PAGE = "Failed to list pozdok page";
    private static final String ERR_GET_BY_ID = "Failed to fetch pozycja dokumentu by id";
    private static final String ERR_LINK_CALL = "Pozdok link call failed";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public PozdokClientImpl(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new PozdokApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    // ADR-031: id, dokumentTypDok, dokumentDataWystawienia, dokumentDataWplywu, dokumentDataWykonania - broken server-side, always null
    private ApiResponsePozdokListRaw listPage(PozdokQueryBuilder query) {
        PozdokQueryBuilder safe = query != null ? query : PozdokQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listPozdok(accountName, safe.start(), null, null,
                    null, safe.dokumentId(), null,
                    null, null, null,
                    safe.dokumentNrDok(), safe.dokumentKontrahentId(), safe.dokumentPlatnikId(),
                    safe.dokumentSklepId(), safe.dokumentKasaId(), safe.dokumentKasjerId(),
                    safe.towarId(), safe.nrPozycji()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all pozycja dokumentu matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all records are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link PozycjaDokumentu} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    @Override
    public PagedResult<PozycjaDokumentu> list(PozdokQueryBuilder query) {
        PozdokQueryBuilder safe = query != null ? query : PozdokQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(RawMappers::toPozycjaDokumentu).toList();
                },
                PozdokClientImpl::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of pozycja dokumentu matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    @Override
    public int count(PozdokQueryBuilder query) {
        ApiResponsePozdokListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<PozycjaDokumentuRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single pozycja dokumentu by its numeric ID.
     *
     * @param id the pozycja dokumentu ID; must not be {@code null}
     * @return the {@link PozycjaDokumentu} record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no pozycja dokumentu with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public PozycjaDokumentu getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(FIELD_ID + ERR_NULL_SUFFIX);
        }
        PozycjaDokumentuRaw raw = retryHandler.execute(() -> api.getPozdokById(accountName, id), ERR_GET_BY_ID).getDane();
        return RawMappers.toPozycjaDokumentu(NoviCloudException.requireDane(raw, RESOURCE_NAME, id));
    }

    private ApiResponsePozdokListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponsePozdokListRaw.class);
    }

    private ApiResponsePozdokListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponsePozdokListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

}
