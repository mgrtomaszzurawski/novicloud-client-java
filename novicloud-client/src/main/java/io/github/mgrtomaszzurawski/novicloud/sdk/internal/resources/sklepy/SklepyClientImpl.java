/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.sklepy;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy.SklepyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy.SklepCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy.SklepQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy.SklepUpdateBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.SklepyApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseCreatedRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseSklepyListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.LinkRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.SklepRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.mapper.RawMappers;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Sklep;

import java.util.List;
import java.util.Objects;

/**
 * Client for the {@code sklepy} (shops) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#sklepy()}.
 *
 * <p>Supports CRUD operations with soft delete: list, count, getById, create, update, deleteById.
 * @since 2.0.0
 */
public final class SklepyClientImpl implements SklepyClient {

    private final ApiClient apiClient;
    private final SklepyApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String RESOURCE_NAME = "sklep";
    private static final String ERR_LIST_PAGE = "Failed to list sklepy page";
    private static final String ERR_GET_BY_ID = "Failed to fetch sklep by id";
    private static final String ERR_CREATE = "Failed to create sklep";
    private static final String ERR_UPDATE = "Failed to update sklep";
    private static final String ERR_DELETE = "Failed to delete sklep by id";
    private static final String ERR_LINK_CALL = "Sklepy link call failed";
    private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public SklepyClientImpl(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new SklepyApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    // ADR-031: nrDomu, nrLokalu, poczta, krajId - broken server-side, always null
    private ApiResponseSklepyListRaw listPage(SklepQueryBuilder query) {
        SklepQueryBuilder safe = query != null ? query : SklepQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listSklepy(accountName, safe.start(), null, safe.fts(),
                    safe.id(), safe.nazwa(), safe.nip(), safe.skrot(), safe.numer(),
                    safe.ulica(), null, null, safe.kodPoczt(),
                    null, safe.miasto(), safe.telefon(), safe.email(),
                    null, safe.aktywny()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all sklep matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all records are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link Sklep} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    @Override
    public PagedResult<Sklep> list(SklepQueryBuilder query) {
        SklepQueryBuilder safe = query != null ? query : SklepQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(RawMappers::toSklep).toList();
                },
                SklepyClientImpl::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of sklep matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    @Override
    public int count(SklepQueryBuilder query) {
        ApiResponseSklepyListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<SklepRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single sklep by its numeric ID.
     *
     * @param id the sklep ID; must not be {@code null}
     * @return the sklep record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no sklep with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public Sklep getById(Long id) {
        requireNotNull(id, FIELD_ID);
        SklepRaw raw = retryHandler.execute(() -> api.getSklepById(accountName, id), ERR_GET_BY_ID).getDane();
        return RawMappers.toSklep(NoviCloudException.requireDane(raw, RESOURCE_NAME, id));
    }

    /**
     * Creates a new sklep. Required fields are enforced by the builder factory method.
     *
     * @param builder the sklep data; must not be {@code null}
     * @return the ID of the created record, or {@code null} if the server did not return one
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    @Override
    public String create(SklepCreateBuilder builder) {
        SklepRaw body = toSklep(builder);
        ApiResponseCreatedRaw response = retryHandler.executePost(() -> api.createSklep(accountName, body), ERR_CREATE);
        return response != null && response.getDane() != null ? response.getDane().getId() : null;
    }

    /**
     * Updates an existing sklep. The {@code id} field in the builder identifies the record to update.
     *
     * @param builder the updated sklep data; must not be {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no sklep with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public void update(SklepUpdateBuilder builder) {
        SklepRaw body = toSklep(builder);
        retryHandler.run(() -> api.updateSklepy(accountName, body), ERR_UPDATE);
    }

    /**
     * Deletes the sklep with the given ID.
     *
     * <p><strong>Soft delete:</strong> this resource does not support physical deletion.
     * The record's {@code aktywny} flag is set to {@code false}; the row remains in the
     * database and still appears in unfiltered list results. Use {@code .aktywny(true)} in the query
     * builder to retrieve only active records.
     * @param id the sklep ID; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no sklep with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public void deleteById(Long id) {
        requireNotNull(id, FIELD_ID);
        retryHandler.run(() -> api.deleteSklep(accountName, id), ERR_DELETE);
    }

    private ApiResponseSklepyListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseSklepyListRaw.class);
    }

    private ApiResponseSklepyListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseSklepyListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

    private static SklepRaw toSklep(SklepCreateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        SklepRaw sklep = new SklepRaw();
        sklep.setId(builder.id());
        sklep.setNazwa(builder.nazwa());
        sklep.setNip(builder.nip());
        sklep.setSkrot(builder.skrot());
        sklep.setNumer(builder.numer());
        sklep.setUlica(builder.ulica());
        sklep.setNrDomu(builder.nrDomu());
        sklep.setNrLokalu(builder.nrLokalu());
        sklep.setKodPoczt(builder.kodPoczt());
        sklep.setPoczta(builder.poczta());
        sklep.setMiasto(builder.miasto());
        sklep.setTelefon(builder.telefon());
        sklep.setEmail(builder.email());
        sklep.setBank(builder.bank());
        sklep.setKonto(builder.konto());
        sklep.setAktywny(builder.aktywny());
        if (builder.krajId() != null) {
            LinkRaw kraj = new LinkRaw();
            kraj.setId(builder.krajId());
            sklep.setKraj(kraj);
        }
        return sklep;
    }

    private static SklepRaw toSklep(SklepUpdateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        SklepRaw sklep = new SklepRaw();
        sklep.setId(builder.id());
        sklep.setNazwa(builder.nazwa());
        sklep.setNip(builder.nip());
        sklep.setSkrot(builder.skrot());
        sklep.setNumer(builder.numer());
        sklep.setUlica(builder.ulica());
        sklep.setNrDomu(builder.nrDomu());
        sklep.setNrLokalu(builder.nrLokalu());
        sklep.setKodPoczt(builder.kodPoczt());
        sklep.setPoczta(builder.poczta());
        sklep.setMiasto(builder.miasto());
        sklep.setTelefon(builder.telefon());
        sklep.setEmail(builder.email());
        sklep.setBank(builder.bank());
        sklep.setKonto(builder.konto());
        sklep.setAktywny(builder.aktywny());
        if (builder.krajId() != null) {
            LinkRaw kraj = new LinkRaw();
            kraj.setId(builder.krajId());
            sklep.setKraj(kraj);
        }
        return sklep;
    }

    private static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + ERR_NULL_SUFFIX);
        }
    }

}
