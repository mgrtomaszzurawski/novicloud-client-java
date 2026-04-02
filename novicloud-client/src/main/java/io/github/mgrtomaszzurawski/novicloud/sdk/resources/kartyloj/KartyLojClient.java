/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.KartyLojApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseCreatedRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseKartyLojListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.KartaLojalnosciowaRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.KartaLojalnosciowa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

/**
 * Client for the {@code kartyloj} (loyalty cards) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#kartyLoj()}.
 *
 * <p>Supports: list, count, getByKod, create, update.
 * @since 1.0.0
 */
public final class KartyLojClient {

    private final ApiClient apiClient;
    private final KartyLojApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String ERR_LIST_PAGE = "Failed to list kartyloj page";
    private static final String ERR_GET_BY_KOD = "Failed to fetch karta loj by kod";
    private static final String ERR_CREATE = "Failed to create karta loj";
    private static final String ERR_UPDATE = "Failed to update karta loj";
    private static final String ERR_LINK_CALL = "KartyLoj link call failed";
    private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String FIELD_KOD = "kod";
    private static final String FIELD_WAZNA_OD = "waznaOd";
    private static final String FIELD_WAZNA_DO = "waznaDo";
    private static final String FIELD_UNIEWAZNIONO = "uniewazniono";
    private static final String FIELD_DATA_URODZENIA = "dataUrodzenia";
    private static final String ERR_INVALID_DATETIME_FMT = "%s: invalid datetime format '%s'";
    private static final String ERR_INVALID_DATE_FMT = "%s: invalid date format '%s'";

    private final RetryHandler retryHandler;

    public KartyLojClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new KartyLojApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    // ADR-031: nazwiskoImie, waznaOd, waznaDo - broken server-side, always null
    private ApiResponseKartyLojListRaw listPage(KartaLojQueryBuilder query) {
        KartaLojQueryBuilder safe = query != null ? query : KartaLojQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listKartyLoj(accountName, safe.start(), null, null,
                    safe.kod(), null, safe.posiadacz(), safe.telefon(),
                    safe.email(), null, null, safe.uniewazniono()), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy iterable over all karty lojalnosciowe matching the given filters.
     * Pages are fetched on demand. If {@code query} is {@code null}, all records are returned.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link KartaLojalnosciowa} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     *         (thrown when the iterator fetches each page)
     */
    public PagedResult<KartaLojalnosciowa> list(KartaLojQueryBuilder query) {
        KartaLojQueryBuilder safe = query != null ? query : KartaLojQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(KartaLojalnosciowa::from).toList();
                },
                KartyLojClient::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of karty lojalnosciowe matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public int count(KartaLojQueryBuilder query) {
        ApiResponseKartyLojListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<KartaLojalnosciowaRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single karta lojalnosciowa by its {@code kod} (card code).
     *
     * @param kod the card code; must not be {@code null}
     * @return the {@link KartaLojalnosciowa} record; never {@code null}
     * @throws IllegalArgumentException if {@code kod} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no karta with the given kod exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public KartaLojalnosciowa getByKod(String kod) {
        if (kod == null) {
            throw new IllegalArgumentException(FIELD_KOD + ERR_NULL_SUFFIX);
        }
        KartaLojalnosciowaRaw raw = retryHandler.execute(() -> api.getKartaLojByKod(accountName, kod), ERR_GET_BY_KOD).getDane();
        return KartaLojalnosciowa.from(raw);
    }

    /**
     * Creates a new karta lojalnosciowa. Required fields are enforced by the builder factory method.
     *
     * @param builder the karta data; must not be {@code null}
     * @return the ID of the created record, or {@code null} if the server did not return one
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    public String create(KartaLojCreateBuilder builder) {
        KartaLojalnosciowaRaw body = toKartaLoj(builder);
        ApiResponseCreatedRaw response = retryHandler.executePost(() -> api.createKartaLoj(accountName, body), ERR_CREATE);
        return response != null && response.getDane() != null ? response.getDane().getId() : null;
    }

    /**
     * Updates an existing karta lojalnosciowa. The {@code kod} field in the builder
     * identifies the card to update.
     *
     * @param builder the updated karta data; must not be {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no karta with the given kod exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    public void update(KartaLojUpdateBuilder builder) {
        KartaLojalnosciowaRaw body = toKartaLoj(builder);
        retryHandler.run(() -> api.updateKartyLoj(accountName, body), ERR_UPDATE);
    }

    private ApiResponseKartyLojListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseKartyLojListRaw.class);
    }

    private ApiResponseKartyLojListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseKartyLojListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

    private static KartaLojalnosciowaRaw toKartaLoj(KartaLojCreateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        KartaLojalnosciowaRaw k = new KartaLojalnosciowaRaw();
        k.setKod(builder.kod());
        k.setTyp(builder.typ());
        k.setWaznaOd(parseDateTime(builder.waznaOd(), FIELD_WAZNA_OD));
        k.setWaznaDo(parseDateTime(builder.waznaDo(), FIELD_WAZNA_DO));
        k.setPosiadacz(builder.posiadacz());
        k.setOpis1(builder.opis1());
        k.setOpis2(builder.opis2());
        k.setUniewazniono(parseDateTime(builder.uniewazniono(), FIELD_UNIEWAZNIONO));
        k.setNazwiskoImie(builder.nazwiskoImie());
        k.setSkrot(builder.skrot());
        k.setTelefon(builder.telefon());
        k.setEmail(builder.email());
        k.setMiejscowosc(builder.miejscowosc());
        k.setUlica(builder.ulica());
        k.setNrDomu(builder.nrDomu());
        k.setNrLokalu(builder.nrLokalu());
        k.setKodPoczt(builder.kodPoczt());
        k.setPoczta(builder.poczta());
        k.setNip(builder.nip());
        k.setDataUrodz(parseDate(builder.dataUrodzenia(), FIELD_DATA_URODZENIA));
        if (builder.plec() != null) {
            k.setPlec(KartaLojalnosciowaRaw.PlecEnum.fromValue(builder.plec()));
        }
        return k;
    }

    private static KartaLojalnosciowaRaw toKartaLoj(KartaLojUpdateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        KartaLojalnosciowaRaw k = new KartaLojalnosciowaRaw();
        k.setKod(builder.kod());
        k.setTyp(builder.typ());
        k.setWaznaOd(parseDateTime(builder.waznaOd(), FIELD_WAZNA_OD));
        k.setWaznaDo(parseDateTime(builder.waznaDo(), FIELD_WAZNA_DO));
        k.setPosiadacz(builder.posiadacz());
        k.setOpis1(builder.opis1());
        k.setOpis2(builder.opis2());
        k.setUniewazniono(parseDateTime(builder.uniewazniono(), FIELD_UNIEWAZNIONO));
        k.setNazwiskoImie(builder.nazwiskoImie());
        k.setSkrot(builder.skrot());
        k.setTelefon(builder.telefon());
        k.setEmail(builder.email());
        k.setMiejscowosc(builder.miejscowosc());
        k.setUlica(builder.ulica());
        k.setNrDomu(builder.nrDomu());
        k.setNrLokalu(builder.nrLokalu());
        k.setKodPoczt(builder.kodPoczt());
        k.setPoczta(builder.poczta());
        k.setNip(builder.nip());
        k.setDataUrodz(parseDate(builder.dataUrodzenia(), FIELD_DATA_URODZENIA));
        if (builder.plec() != null) {
            k.setPlec(KartaLojalnosciowaRaw.PlecEnum.fromValue(builder.plec()));
        }
        return k;
    }

    private static LocalDateTime parseDateTime(String value, String fieldName) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(String.format(ERR_INVALID_DATETIME_FMT, fieldName, value), e);
        }
    }

    private static LocalDate parseDate(String value, String fieldName) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(String.format(ERR_INVALID_DATE_FMT, fieldName, value), e);
        }
    }

}
