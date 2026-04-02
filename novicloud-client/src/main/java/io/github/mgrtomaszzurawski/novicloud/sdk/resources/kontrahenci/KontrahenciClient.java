/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.KontrahenciApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseCreatedRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseKontrahenciListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.KontrahentRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.LinkRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kontrahent;

import java.util.List;
import java.util.Objects;

/**
 * Client for the {@code kontrahenci} (contractors) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#kontrahenci()}.
 *
 * <p>Supports CRUD operations with soft delete: list, count, getById, create, update, deleteById.
 * @since 1.0.0
 */
public final class KontrahenciClient {

    private final ApiClient apiClient;
    private final KontrahenciApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String ERR_LIST_PAGE = "Failed to list kontrahenci page";
    private static final String ERR_GET_BY_ID = "Failed to fetch kontrahent by id";
    private static final String ERR_CREATE = "Failed to create kontrahent";
    private static final String ERR_UPDATE = "Failed to update kontrahent";
    private static final String ERR_DELETE = "Failed to delete kontrahent by id";
    private static final String ERR_LINK_CALL = "Kontrahenci link call failed";
    private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public KontrahenciClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new KontrahenciApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    // ADR-031: osoba - broken server-side, always null
    private ApiResponseKontrahenciListRaw listPage(KontrahentQueryBuilder query) {
        KontrahentQueryBuilder safe = query != null ? query : KontrahentQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listKontrahenci(accountName, safe.start(), null, safe.fts(),
                    safe.id(), safe.nazwa(), safe.nip(), safe.skrot(),
                    safe.ulica(), safe.nrDomu(), safe.nrLokalu(), safe.kodPoczt(),
                    safe.poczta(), safe.miasto(), safe.telefon(), safe.email(),
                    safe.krajId(), safe.aktywny(), safe.dostawca(), safe.staly(),
                    safe.producent(), safe.odbiorca(), null), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all kontrahent matching the given filters.
     * Pages are fetched on demand as the iterator advances. If {@code query} is {@code null},
     * all records are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link Kontrahent} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    public PagedResult<Kontrahent> list(KontrahentQueryBuilder query) {
        KontrahentQueryBuilder safe = query != null ? query : KontrahentQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(Kontrahent::from).toList();
                },
                KontrahenciClient::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of kontrahent matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public int count(KontrahentQueryBuilder query) {
        ApiResponseKontrahenciListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<KontrahentRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single kontrahent by its numeric ID.
     *
     * @param id the kontrahent ID; must not be {@code null}
     * @return the kontrahent record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no kontrahent with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public Kontrahent getById(Long id) {
        requireNotNull(id, FIELD_ID);
        KontrahentRaw raw = retryHandler.execute(() -> api.getKontrahentById(accountName, id), ERR_GET_BY_ID).getDane();
        return Kontrahent.from(raw);
    }

    /**
     * Creates a new kontrahent. Required fields are enforced by the builder factory method.
     *
     * @param builder the kontrahent data; must not be {@code null}
     * @return the ID of the created record, or {@code null} if the server did not return one
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public String create(KontrahentCreateBuilder builder) {
        KontrahentRaw body = toKontrahent(builder);
        ApiResponseCreatedRaw response = retryHandler.executePost(() -> api.createKontrahent(accountName, body), ERR_CREATE);
        return response != null && response.getDane() != null ? response.getDane().getId() : null;
    }

    /**
     * Updates an existing kontrahent. The {@code id} field in the builder identifies the record to update.
     *
     * @param builder the updated kontrahent data; must not be {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no kontrahent with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public void update(KontrahentUpdateBuilder builder) {
        KontrahentRaw body = toKontrahent(builder);
        retryHandler.run(() -> api.updateKontrahenci(accountName, body), ERR_UPDATE);
    }

    /**
     * Deletes the kontrahent with the given ID.
     *
     * <p><strong>Soft delete:</strong> this resource does not support physical deletion.
     * The record's {@code aktywny} flag is set to {@code false}; the row remains in the
     * database and still appears in unfiltered list results. Use {@code .aktywny(true)} in the query
     * builder to retrieve only active records.
     * @param id the kontrahent ID; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no kontrahent with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public void deleteById(Long id) {
        requireNotNull(id, FIELD_ID);
        retryHandler.run(() -> api.deleteKontrahent(accountName, id), ERR_DELETE);
    }

    private ApiResponseKontrahenciListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseKontrahenciListRaw.class);
    }

    private ApiResponseKontrahenciListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseKontrahenciListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

    private static KontrahentRaw toKontrahent(KontrahentCreateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        KontrahentRaw k = new KontrahentRaw();
        k.setId(builder.id());
        k.setNazwa(builder.nazwa());
        k.setNip(builder.nip());
        k.setSkrot(builder.skrot());
        k.setUlica(builder.ulica());
        k.setNrDomu(builder.nrDomu());
        k.setNrLokalu(builder.nrLokalu());
        k.setKodPoczt(builder.kodPoczt());
        k.setPoczta(builder.poczta());
        k.setMiasto(builder.miasto());
        k.setTelefon(builder.telefon());
        k.setEmail(builder.email());
        k.setAktywny(builder.aktywny());
        k.setDostawca(builder.dostawca());
        k.setStaly(builder.staly());
        k.setProducent(builder.producent());
        k.setOdbiorca(builder.odbiorca());
        k.setOsoba(builder.osoba());
        if (builder.krajId() != null) {
            LinkRaw kraj = new LinkRaw();
            kraj.setId(builder.krajId());
            k.setKraj(kraj);
        }
        return k;
    }

    private static KontrahentRaw toKontrahent(KontrahentUpdateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        KontrahentRaw k = new KontrahentRaw();
        k.setId(builder.id());
        k.setNazwa(builder.nazwa());
        k.setNip(builder.nip());
        k.setSkrot(builder.skrot());
        k.setUlica(builder.ulica());
        k.setNrDomu(builder.nrDomu());
        k.setNrLokalu(builder.nrLokalu());
        k.setKodPoczt(builder.kodPoczt());
        k.setPoczta(builder.poczta());
        k.setMiasto(builder.miasto());
        k.setTelefon(builder.telefon());
        k.setEmail(builder.email());
        k.setAktywny(builder.aktywny());
        k.setDostawca(builder.dostawca());
        k.setStaly(builder.staly());
        k.setProducent(builder.producent());
        k.setOdbiorca(builder.odbiorca());
        k.setOsoba(builder.osoba());
        if (builder.krajId() != null) {
            LinkRaw kraj = new LinkRaw();
            kraj.setId(builder.krajId());
            k.setKraj(kraj);
        }
        return k;
    }

    private static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + ERR_NULL_SUFFIX);
        }
    }

}
