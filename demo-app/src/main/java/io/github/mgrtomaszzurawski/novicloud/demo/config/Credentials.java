/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.config;

public record Credentials(String accountName, String password) {

    public boolean isValid() {
        return isNotBlank(accountName) && isNotBlank(password);
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
