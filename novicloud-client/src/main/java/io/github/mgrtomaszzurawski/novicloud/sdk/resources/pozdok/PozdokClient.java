/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.pozdok;

import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.PozycjaDokumentu;

/**
 * Client for the {@code pozdok} (document positions) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from
 * {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient} accessors.
 * Implementation lives in the non-exported {@code sdk.internal.resources.pozdok}
 * package; external code should depend on this interface only.
 *
 * @since 2.0.0
 */
public interface PozdokClient {

    /**
     * Returns a lazy paginated result over all document positions matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} of matching records (pages fetched on demand)
     * @throws NoviCloudException on API failure (thrown when the iterator fetches each page)
     */
    PagedResult<PozycjaDokumentu> list(PozdokQueryBuilder query);

    /**
     * Returns the total number of document positions matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws NoviCloudException on API failure
     */
    int count(PozdokQueryBuilder query);

    /**
     * Fetches a single document positions record by its numeric ID.
     *
     * @param id the record ID; must not be {@code null}
     * @return the record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws NoviCloudNotFoundException if no document positions record with the given ID exists
     * @throws NoviCloudException on other API failure
     */
    PozycjaDokumentu getById(Long id);
}
