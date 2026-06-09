/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.towary;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowaryClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarUpdateBuilder;

import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.TowaryApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseCreatedRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseTowaryListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.LinkRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarCenaWSklepieRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarKodDodatkowyRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarSkladnikRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarSkladnikTowarRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Towar;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarCenaWSklepie;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarKodDodatkowy;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarSkladnik;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarSkladnikTowar;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.mapper.RawMappers;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;

import java.util.List;
import java.util.Objects;

/**
 * Client for the {@code towary} (products) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#towary()}.
 *
 * <p>Supports CRUD operations with soft delete: list, count, getById, create, update, deleteById.
 * @since 2.0.0
 */
public final class TowaryClientImpl implements TowaryClient {

    private final ApiClient apiClient;
    private final TowaryApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String RESOURCE_NAME = "towar";
    private static final String ERR_LIST_PAGE = "Failed to list towary page";
    private static final String ERR_FETCH_BY_ID = "Failed to fetch towar by id";
    private static final String ERR_CREATE = "Failed to create towar";
    private static final String ERR_UPDATE = "Failed to update towar";
    private static final String ERR_DELETE = "Failed to delete towar by id";
    private static final String ERR_LINK_CALL = "Towary link call failed";
    private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public TowaryClientImpl(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new TowaryApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    // ADR-031: typ, cenaDet - broken server-side, always null
    private ApiResponseTowaryListRaw listPage(TowarQueryBuilder query) {
        TowarQueryBuilder safe = query != null ? query : TowarQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listTowary(
                    accountName,
                    safe.start(),
                    null,
                    safe.fts(),
                    safe.id(),
                    safe.nazwa(),
                    safe.kod(),
                    safe.stawkaVat(),
                    safe.akcyzowy(),
                    null,
                    null,
                    safe.jmId(),
                    safe.asortId(),
                    safe.aktywny()
            ), ERR_LIST_PAGE);
    }

    /**
     * Returns a lazy paginated result over all towary matching the given filters.
     * The first page is fetched on the first access to metadata or the iterator.
     * If {@code query} is {@code null}, all towary are returned with default page size.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link Towar} records
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    @Override
    public PagedResult<Towar> list(TowarQueryBuilder query) {
        TowarQueryBuilder safe = query != null ? query : TowarQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(RawMappers::toTowar).toList();
                },
                TowaryClientImpl::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    /**
     * Returns the total number of towary matching the given filters.
     * Uses the {@code size} field from the first page response; falls back to the number of
     * items on that page if {@code size} is not present.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    @Override
    public int count(TowarQueryBuilder query) {
        ApiResponseTowaryListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<TowarRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    /**
     * Fetches a single towar by its numeric ID.
     *
     * @param id the towar ID; must not be {@code null}
     * @return the {@link Towar} record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no towar with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public Towar getById(Long id) {
        requireNotNull(id, FIELD_ID);
        TowarRaw raw = retryHandler.execute(() -> api.getTowarById(accountName, id), ERR_FETCH_BY_ID).getDane();
        return RawMappers.toTowar(NoviCloudException.requireDane(raw, RESOURCE_NAME, id));
    }

    /**
     * Creates a new towar. The {@code kod} and {@code nazwa} fields are required
     * (enforced by {@link TowarCreateBuilder#builder(String, String)}).
     *
     * @param builder the towar data; must not be {@code null}
     * @return the ID of the created towar, or {@code null} if the server did not return one
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on API failure
     */
    @Override
    public String create(TowarCreateBuilder builder) {
        TowarRaw body = toTowar(builder);
        ApiResponseCreatedRaw response = retryHandler.executePost(() -> api.createTowar(accountName, body), ERR_CREATE);
        return response != null && response.getDane() != null ? response.getDane().getId() : null;
    }

    /**
     * Updates an existing towar. The {@code id} field in the builder identifies the record
     * to update.
     *
     * @param builder the updated towar data; must not be {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no towar with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public void update(TowarUpdateBuilder builder) {
        TowarRaw body = toTowar(builder);
        retryHandler.run(() -> api.updateTowary(accountName, body), ERR_UPDATE);
    }

    /**
     * Deletes the towar with the given ID.
     *
     * <p><strong>Soft delete:</strong> this resource does not support physical deletion.
     * The record's {@code aktywny} flag is set to {@code false}; the row remains in the
     * database and still appears in unfiltered list results. Use {@code .aktywny(true)} in the query
     * builder to retrieve only active records.
     * @param id the towar ID; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException
     *         if no towar with the given ID exists
     * @throws io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException on other API failure
     */
    @Override
    public void deleteById(Long id) {
        requireNotNull(id, FIELD_ID);
        retryHandler.run(() -> api.deleteTowar(accountName, id), ERR_DELETE);
    }

    private ApiResponseTowaryListRaw doFetchByLink(String link) throws ApiException {
        return LinkFetcher.fetch(link, apiClient, ApiResponseTowaryListRaw.class);
    }

    private ApiResponseTowaryListRaw fetchByLink(String link) {
        return retryHandler.execute(() -> doFetchByLink(link), ERR_LINK_CALL);
    }

    private static String extractSelfLink(ApiResponseTowaryListRaw response) {
        if (response == null || response.getLinks() == null || response.getLinks().getSelf() == null) {
            return null;
        }
        return response.getLinks().getSelf().toString();
    }

    private static TowarRaw toTowar(TowarCreateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        TowarRaw towar = new TowarRaw();
        towar.setId(builder.id());
        towar.setNazwa(builder.nazwa());
        towar.setKod(builder.kod());
        towar.setCku(builder.cku());
        towar.setStawkaVat(builder.stawkaVat());
        towar.setAkcyzowy(builder.akcyzowy());
        if (builder.typ() != null) {
            towar.setTyp(TowarRaw.TypEnum.fromValue(builder.typ()));
        }
        towar.setCenaEw(builder.cenaEw());
        towar.setCenaDet(builder.cenaDet());
        towar.setCenaHurt(builder.cenaHurt());
        towar.setCenaNoc(builder.cenaNoc());
        towar.setCenaDod(builder.cenaDod());
        if (builder.przySprzedazy() != null) {
            towar.setPrzySprzedazy(TowarRaw.PrzySprzedazyEnum.fromValue(builder.przySprzedazy()));
        }
        towar.setGtu(builder.gtu());
        towar.setPkwiu(builder.pkwiu());
        towar.setMasaWl(builder.masaWl());
        towar.setAktywny(builder.aktywny());
        towar.setOpis1(builder.opis1());
        towar.setOpis2(builder.opis2());
        towar.setOpis3(builder.opis3());
        towar.setOpis4(builder.opis4());
        towar.setOpis5(builder.opis5());

        if (builder.jmId() != null) {
            LinkRaw jm = new LinkRaw();
            jm.setId(builder.jmId());
            towar.setJm(jm);
        }

        if (builder.asortId() != null) {
            LinkRaw asort = new LinkRaw();
            asort.setId(builder.asortId());
            towar.setAsort(asort);
        }

        applyNestedLists(towar, builder.kodyDod(), builder.cenyWSklepach(), builder.skladniki());

        return towar;
    }

    private static TowarRaw toTowar(TowarUpdateBuilder builder) {
        Objects.requireNonNull(builder, ERR_BUILDER_NULL);
        TowarRaw towar = new TowarRaw();
        towar.setId(builder.id());
        towar.setNazwa(builder.nazwa());
        towar.setKod(builder.kod());
        towar.setCku(builder.cku());
        towar.setStawkaVat(builder.stawkaVat());
        towar.setAkcyzowy(builder.akcyzowy());
        if (builder.typ() != null) {
            towar.setTyp(TowarRaw.TypEnum.fromValue(builder.typ()));
        }
        towar.setCenaEw(builder.cenaEw());
        towar.setCenaDet(builder.cenaDet());
        towar.setCenaHurt(builder.cenaHurt());
        towar.setCenaNoc(builder.cenaNoc());
        towar.setCenaDod(builder.cenaDod());
        if (builder.przySprzedazy() != null) {
            towar.setPrzySprzedazy(TowarRaw.PrzySprzedazyEnum.fromValue(builder.przySprzedazy()));
        }
        towar.setGtu(builder.gtu());
        towar.setPkwiu(builder.pkwiu());
        towar.setMasaWl(builder.masaWl());
        towar.setAktywny(builder.aktywny());
        towar.setOpis1(builder.opis1());
        towar.setOpis2(builder.opis2());
        towar.setOpis3(builder.opis3());
        towar.setOpis4(builder.opis4());
        towar.setOpis5(builder.opis5());

        if (builder.jmId() != null) {
            LinkRaw jm = new LinkRaw();
            jm.setId(builder.jmId());
            towar.setJm(jm);
        }

        if (builder.asortId() != null) {
            LinkRaw asort = new LinkRaw();
            asort.setId(builder.asortId());
            towar.setAsort(asort);
        }

        applyNestedLists(towar, builder.kodyDod(), builder.cenyWSklepach(), builder.skladniki());

        return towar;
    }

    private static void applyNestedLists(TowarRaw towar, List<TowarKodDodatkowy> kodyDod,
            List<TowarCenaWSklepie> cenyWSklepach, List<TowarSkladnik> skladniki)
    {
        if (kodyDod != null) {
            towar.setKodyDod(kodyDod.stream().map(TowaryClientImpl::toKodDodatkowyRaw).toList());
        }
        if (cenyWSklepach != null) {
            towar.setCenyWSklepach(cenyWSklepach.stream().map(TowaryClientImpl::toCenaWSklepieRaw).toList());
        }
        if (skladniki != null) {
            towar.setSkladniki(skladniki.stream().map(TowaryClientImpl::toSkladnikRaw).toList());
        }
    }

    private static TowarKodDodatkowyRaw toKodDodatkowyRaw(TowarKodDodatkowy src) {
        TowarKodDodatkowyRaw raw = new TowarKodDodatkowyRaw();
        raw.setKod(src.kod());
        raw.setIleWOpak(src.ileWOpak());
        if (src.poziomCen() != null) {
            raw.setPoziomCen(TowarKodDodatkowyRaw.PoziomCenEnum.fromValue(src.poziomCen()));
        }
        return raw;
    }

    private static TowarCenaWSklepieRaw toCenaWSklepieRaw(TowarCenaWSklepie src) {
        TowarCenaWSklepieRaw raw = new TowarCenaWSklepieRaw();
        if (src.sklepId() != null) {
            LinkRaw sklep = new LinkRaw();
            sklep.setId(src.sklepId());
            raw.setSklep(sklep);
        }
        raw.setCenaEw(src.cenaEw());
        raw.setCenaDet(src.cenaDet());
        raw.setCenaHurt(src.cenaHurt());
        raw.setCenaNoc(src.cenaNoc());
        raw.setCenaDod(src.cenaDod());
        raw.setPrzySprzedazy(src.przySprzedazy());
        return raw;
    }

    private static TowarSkladnikRaw toSkladnikRaw(TowarSkladnik src) {
        TowarSkladnikRaw raw = new TowarSkladnikRaw();
        raw.setNazwa(src.nazwa());
        raw.setCena(src.cena());
        raw.setObowiazkowy(src.obowiazkowy());
        raw.setWyborWieluTow(src.wyborWieluTow());
        raw.setRozneCeny(src.rozneCeny());
        if (src.towary() != null) {
            raw.setTowary(src.towary().stream().map(TowaryClientImpl::toSkladnikTowarRaw).toList());
        }
        return raw;
    }

    private static TowarSkladnikTowarRaw toSkladnikTowarRaw(TowarSkladnikTowar src) {
        TowarSkladnikTowarRaw raw = new TowarSkladnikTowarRaw();
        if (src.towarId() != null) {
            LinkRaw towar = new LinkRaw();
            towar.setId(src.towarId());
            raw.setTowar(towar);
        }
        raw.setIlosc(src.ilosc());
        raw.setCenaZKartyTow(src.cenaZKartyTow());
        raw.setCena(src.cena());
        raw.setDomyslny(src.domyslny());
        return raw;
    }

    private static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + ERR_NULL_SUFFIX);
        }
    }

}
