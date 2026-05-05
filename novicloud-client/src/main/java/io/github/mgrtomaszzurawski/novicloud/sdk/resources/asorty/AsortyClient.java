/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty;

import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Asorty;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;

/**
 * Client for the {@code asorty} (assortment groups) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from
 * {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient#asorty()}.
 * Supports full CRUD operations: list, count, getById, create, update, deleteById.
 *
 * <p>Implementation is provided in the non-exported package
 * {@code sdk.internal.resources.asorty}; external code should depend on this
 * interface only.
 *
 * @since 2.0.0
 */
public interface AsortyClient {

    /**
     * Returns a lazy paginated result over all asorty matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} over all matching {@link Asorty} records
     * @throws NoviCloudException on API failure (thrown when the iterator fetches each page)
     */
    PagedResult<Asorty> list(AsortyQueryBuilder query);

    /**
     * Returns the total number of asorty matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws NoviCloudException on API failure
     */
    int count(AsortyQueryBuilder query);

    /**
     * Fetches a single asorty record by its numeric ID.
     *
     * @param id the asorty ID; must not be {@code null}
     * @return the {@link Asorty} record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws NoviCloudNotFoundException if no asorty record with the given ID exists
     * @throws NoviCloudException on other API failure
     */
    Asorty getById(Long id);

    /**
     * Creates a new asorty record.
     *
     * @param builder the asorty data; must not be {@code null}
     * @return the ID of the created record, or {@code null} if the server did not return one
     * @throws NoviCloudException on API failure
     */
    String create(AsortyCreateBuilder builder);

    /**
     * Updates an existing asorty record. The {@code id} field in the builder identifies the record.
     *
     * @param builder the updated asorty data; must not be {@code null}
     * @throws NoviCloudNotFoundException if no asorty record with the given ID exists
     * @throws NoviCloudException on other API failure
     */
    void update(AsortyUpdateBuilder builder);

    /**
     * Deletes the asorty record with the given ID.
     *
     * @param id the asorty ID; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws NoviCloudNotFoundException if no asorty record with the given ID exists
     * @throws NoviCloudException on other API failure
     */
    void deleteById(Long id);
}
