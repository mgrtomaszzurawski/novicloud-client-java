/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

/**
 * Immutable SDK model for a single product item inside a bundle component.
 *
 * <p>Used inside {@link TowarSkladnik#towary()}.
 *
 * @param towarId        the product ID this item references
 * @param ilosc          quantity required from this item
 * @param cenaZKartyTow  whether the price comes from the product card
 * @param cena           the explicit price for this item; may be {@code null}
 * @param domyslny       whether this item is the default selection
 * @since 2.0.0
 */
public record TowarSkladnikTowar(
        String towarId,
        Double ilosc,
        Boolean cenaZKartyTow,
        Double cena,
        Boolean domyslny
)
{
}
