/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat;

import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.StawkaVat;

/**
 * Client for the {@code stawkivat} (VAT rates) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from
 * {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient} accessors.
 * Implementation lives in the non-exported {@code sdk.internal.resources.stawkivat}
 * package; external code should depend on this interface only.
 *
 * @since 2.0.0
 */
public interface StawkiVatClient {

    /**
     * Returns a lazy paginated result over all VAT rates matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} of matching records (pages fetched on demand)
     * @throws NoviCloudException on API failure (thrown when the iterator fetches each page)
     */
    PagedResult<StawkaVat> list(StawkaVatQueryBuilder query);

    /**
     * Returns the total number of VAT rates matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws NoviCloudException on API failure
     */
    int count(StawkaVatQueryBuilder query);

    /**
     * Fetches a single VAT rates record by its numeric ID.
     *
     * @param id the record ID; must not be {@code null}
     * @return the record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws NoviCloudNotFoundException if no VAT rates record with the given ID exists
     * @throws NoviCloudException on other API failure
     */
    StawkaVat getById(Long id);

    /**
     * Creates a new VAT rates record.
     *
     * @param builder the data; must not be {@code null}
     * @return the ID (or code) of the created record, or {@code null} if the server did not return one
     * @throws NoviCloudException on API failure
     */
    String create(StawkaVatCreateBuilder builder);

    /**
     * Deletes the record with the given ID.
     *
     * @param id the record ID; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws NoviCloudNotFoundException if no record with the given ID exists
     * @throws NoviCloudException on other API failure
     */
    void deleteById(Long id);
}
