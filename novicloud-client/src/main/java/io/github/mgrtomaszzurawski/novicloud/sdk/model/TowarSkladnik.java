/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import java.util.List;

/**
 * Immutable SDK model for a bundle component group on a product of type 5.
 *
 * <p>Returned by {@link Towar#skladniki()}. Each component has a base price and
 * a list of acceptable products inside it.
 *
 * @param nazwa          human-readable name of the component group
 * @param cena           base price of the component
 * @param obowiazkowy    whether the component is mandatory
 * @param wyborWieluTow  whether multiple products may be selected
 * @param rozneCeny      whether component products may have different prices
 * @param towary         products that satisfy this component; never {@code null}, may be empty
 * @since 2.0.0
 */
public record TowarSkladnik(
        String nazwa,
        Double cena,
        Boolean obowiazkowy,
        Boolean wyborWieluTow,
        Boolean rozneCeny,
        List<TowarSkladnikTowar> towary
)
{
}
