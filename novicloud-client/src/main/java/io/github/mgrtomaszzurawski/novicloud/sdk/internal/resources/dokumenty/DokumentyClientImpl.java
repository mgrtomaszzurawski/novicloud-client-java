/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.dokumenty;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.dokumenty.DokumentyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.dokumenty.DokumentQueryBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.DokumentyApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseDokumentyListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.DokumentRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.mapper.RawMappers;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Dokument;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;

import java.util.List;

/**
 * Client for the {@code dokumenty} (documents) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#dokumenty()}.
 *
 * <p>Supports: list, count, getById (read-only).
 * @since 2.0.0
 */
public final class DokumentyClientImpl implements DokumentyClient {

    private final ApiClient apiClient;
    private final DokumentyApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String RESOURCE_NAME = "dokument";
    private static final String ERR_LIST_PAGE = "Failed to list dokumenty page";
    private static final String ERR_GET_BY_ID = "Failed to fetch dokument by id";
    private static final String ERR_LINK_CALL = "Dokumenty link call failed";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public DokumentyClientImpl(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new DokumentyApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    // ADR-031: sklepOdbId - broken server-side, always null
    private ApiResponseDokumentyListRaw listPage(DokumentQueryBuilder query) {
        DokumentQueryBuilder safe = query != null ? query : DokumentQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listDokumenty(accountName, safe.start(), null, null,
                    safe.id(), safe.typDok(), safe.dataWystawienia(), safe.dataWplywu(),
                    safe.dataWykonania(), safe.nrDok(), safe.sklepId(), null,
                    safe.kontrahentId(), safe.platnikId(), safe.kasaId(), safe.kasjerId(), safe.storno()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all dokument matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all records are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a lazy iterable over all matching {@link Dokument} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    @Override
    public PagedResult<Dokument> list(DokumentQueryBuilder query) {
        DokumentQueryBuilder safe = query != null ? query : DokumentQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(RawMappers::toDokument).toList();
                },
                DokumentyClientImpl::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of dokument matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    @Override
    public int count(DokumentQueryBuilder query) {
        ApiResponseDokumentyListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<DokumentRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single dokument by its numeric ID.
     *
     * @param id the dokument ID; must not be {@code null}
     * @return the dokument record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no dokument with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public Dokument getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(FIELD_ID + ERR_NULL_SUFFIX);
        }
        DokumentRaw raw = retryHandler.execute(() -> api.getDokumentById(accountName, id), ERR_GET_BY_ID).getDane();
        return RawMappers.toDokument(NoviCloudException.requireDane(raw, RESOURCE_NAME, id));
    }

    private ApiResponseDokumentyListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseDokumentyListRaw.class);
    }

    private ApiResponseDokumentyListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseDokumentyListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

}
