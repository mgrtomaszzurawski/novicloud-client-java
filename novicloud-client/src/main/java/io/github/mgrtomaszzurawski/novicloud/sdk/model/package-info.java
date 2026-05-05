/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
/**
 * Immutable SDK model records returned by all NoviCloud client methods.
 *
 * <p>Each record is an SDK-owned data class with field accessors named after
 * the API field (for example {@code towar.nazwa()}, {@code towar.kod()}). Link
 * references are unwrapped to plain IDs. Records are constructed by the SDK
 * internally; consumers receive them and read fields - they do not depend on
 * any types from the generated transport layer.
 *
 * @since 1.0.0
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;
