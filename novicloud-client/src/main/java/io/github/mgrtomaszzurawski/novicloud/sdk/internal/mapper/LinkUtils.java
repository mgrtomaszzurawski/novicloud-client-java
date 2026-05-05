/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.internal.mapper;

import io.github.mgrtomaszzurawski.novicloud.client.model.LinkRaw;

/**
 * Shared utility for extracting link identifiers from raw API responses.
 *
 * <p>Internal API since 2.0.0 (moved from {@code sdk.model} into the
 * non-exported {@code sdk.internal.mapper} package). Used by
 * {@link RawMappers} to flatten {@link LinkRaw} fields into plain
 * {@code String} IDs in the public records.
 */
public final class LinkUtils {

    private static final String FIELD_ID = "id";

    private LinkUtils() { }

    /**
     * Extracts the {@code id} value from a link object returned by the NoviCloud API.
     *
     * <p>The link may arrive as a {@link LinkRaw} instance or as a plain {@link java.util.Map}
     * depending on the Jackson deserialization path.
     *
     * @param link the link object; may be {@code null}
     * @return the extracted id, or {@code null} if the link is {@code null} or unrecognised
     */
    public static String extractId(Object link) {
        if (link == null) { return null; }
        if (link instanceof LinkRaw lr) { return lr.getId(); }
        if (link instanceof java.util.Map<?, ?> map) { return map.get(FIELD_ID) != null ? map.get(FIELD_ID).toString() : null; }
        return null;
    }
}
