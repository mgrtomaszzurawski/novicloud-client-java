/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
/**
 * Lazy pagination support: {@link io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult}.
 *
 * <p>Note: the internal {@code LinkFetcher} helper that follows pagination
 * URLs lives in the non-exported {@code sdk.internal.paging} package as of
 * 2.0.0; only {@code PagedResult} is part of the public surface here.
 *
 * @since 1.0.0
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.paging;
