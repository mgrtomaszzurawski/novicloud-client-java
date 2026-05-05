/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary;

import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Jmiary;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;

/**
 * Client for the {@code jmiary} (units of measure) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from
 * {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient} accessors.
 * Implementation lives in the non-exported {@code sdk.internal.resources.jmiary}
 * package; external code should depend on this interface only.
 *
 * @since 2.0.0
 */
public interface JmiaryClient {

    /**
     * Returns a lazy paginated result over all units of measure matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} of matching records (pages fetched on demand)
     * @throws NoviCloudException on API failure (thrown when the iterator fetches each page)
     */
    PagedResult<Jmiary> list(JmiaryQueryBuilder query);

    /**
     * Returns the total number of units of measure matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws NoviCloudException on API failure
     */
    int count(JmiaryQueryBuilder query);

    /**
     * Fetches a single units of measure record by its numeric ID.
     *
     * @param id the record ID; must not be {@code null}
     * @return the record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws NoviCloudNotFoundException if no units of measure record with the given ID exists
     * @throws NoviCloudException on other API failure
     */
    Jmiary getById(Long id);

    /**
     * Creates a new units of measure record.
     *
     * @param builder the data; must not be {@code null}
     * @return the ID (or code) of the created record, or {@code null} if the server did not return one
     * @throws NoviCloudException on API failure
     */
    String create(JmiaryCreateBuilder builder);

    /**
     * Updates an existing units of measure record. The identifier field in the builder identifies the record.
     *
     * @param builder the updated data; must not be {@code null}
     * @throws NoviCloudNotFoundException if no units of measure record with the given identifier exists
     * @throws NoviCloudException on other API failure
     */
    void update(JmiaryUpdateBuilder builder);

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
