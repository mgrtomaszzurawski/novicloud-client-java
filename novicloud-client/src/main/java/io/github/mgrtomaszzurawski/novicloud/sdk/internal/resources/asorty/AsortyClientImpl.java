/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.asorty;

import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.client.api.AsortyApi;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseAsortyListRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.ApiResponseCreatedRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.AsortyRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.LinkRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.RetryHandler;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.mapper.RawMappers;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.paging.LinkFetcher;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Asorty;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty.AsortyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty.AsortyCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty.AsortyQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty.AsortyUpdateBuilder;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of {@link AsortyClient}. Lives in the non-exported
 * {@code sdk.internal.resources.asorty} package since 2.0.0; constructed
 * by {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient}.
 */
public final class AsortyClientImpl implements AsortyClient {

    private final ApiClient apiClient;
    private final AsortyApi api;
    private final String accountName;

    private static final int EMPTY_COUNT = 0;
    private static final String ERR_NULL_SUFFIX = " must not be null";
    private static final String RESOURCE_NAME = "asort";
    private static final String ERR_LIST_PAGE = "Failed to list asorty page";
    private static final String ERR_GET_BY_ID = "Failed to fetch asorty by id";
    private static final String ERR_CREATE = "Failed to create asorty";
    private static final String ERR_UPDATE = "Failed to update asorty";
    private static final String ERR_DELETE = "Failed to delete asorty by id";
    private static final String ERR_LINK_CALL = "Asorty link call failed";
    private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String FIELD_ID = "id";
    private final RetryHandler retryHandler;

    public AsortyClientImpl(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new AsortyApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }

    private ApiResponseAsortyListRaw listPage(AsortyQueryBuilder query) {
        AsortyQueryBuilder safe = query != null ? query : AsortyQueryBuilder.builder().build();
        return retryHandler.execute(() -> api.listAsorty(accountName, safe.start(), null, safe.fts(),
                    safe.id(), safe.nazwa(), safe.parentId()), ERR_LIST_PAGE);
    }

    @Override
    public PagedResult<Asorty> list(AsortyQueryBuilder query) {
        AsortyQueryBuilder safe = query != null ? query : AsortyQueryBuilder.builder().build();
        return PagedResult.of(
                () -> listPage(safe),
                this::fetchByLink,
                p -> {
                    var items = p.getDane();
                    return items == null ? List.of() : items.stream().map(RawMappers::toAsorty).toList();
                },
                AsortyClientImpl::extractSelfLink,
                p -> p.getSize() != null ? p.getSize() : PagedResult.UNKNOWN,
                p -> p.getOnPage() != null ? p.getOnPage() : PagedResult.UNKNOWN
        );
    }

    @Override
    public int count(AsortyQueryBuilder query) {
        ApiResponseAsortyListRaw response = listPage(query);
        Integer total = response.getSize();
        if (total != null) {
            return total;
        }
        List<AsortyRaw> data = response.getDane();
        return data == null ? EMPTY_COUNT : data.size();
    }

    @Override
    public Asorty getById(Long id) {
        requireNotNull(id, FIELD_ID);
        AsortyRaw raw = retryHandler.execute(() -> api.getAsortyById(accountName, id), ERR_GET_BY_ID).getDane();
        return RawMappers.toAsorty(NoviCloudException.requireDane(raw, RESOURCE_NAME, id));
    }

    @Override
    public String create(AsortyCreateBuilder builder) {
        AsortyRaw body = toAsorty(builder);
        ApiResponseCreatedRaw response = retryHandler.executePost(() -> api.createAsorty(accountName, body), ERR_CREATE);
        return response != null && response.getDane() != null ? response.getDane().getId() : null;
    }

    @Override
    public void update(AsortyUpdateBuilder builder) {
        AsortyRaw body = toAsorty(builder);
        retryHandler.run(() -> api.updateAsorty(accountName, body), ERR_UPDATE);
    }

    @Override
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
