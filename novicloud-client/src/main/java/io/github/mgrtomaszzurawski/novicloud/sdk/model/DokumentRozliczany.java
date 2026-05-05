/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

/**
 * Immutable SDK model for a settlement row on a document.
 *
 * <p>Each row references a related document and the amount that has been settled
 * against it. Used inside {@link Dokument#dokRozliczane()}.
 *
 * <p>Note: the API field is spelled {@code dok_roliczane} (Polish typo on the server).
 * The SDK accessor uses the corrected spelling {@code dokRozliczane()}.
 *
 * @param dokumentId the related document ID
 * @param zaplacono  the settled amount
 * @since 2.0.0
 */
public record DokumentRozliczany(
        String dokumentId,
        Double zaplacono
)
{
}
