/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.config;

import java.util.Properties;

public final class AppProperties {

    private static final String PROP_ACCOUNT_NAME = "novicloud.account-name";
    private static final String PROP_PASSWORD = "novicloud.password";
    private static final String PROP_BASE_URL = "novicloud.base-url";
    private static final String PROP_DEMO_MODE = "demo.mode";
    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";
    private static final String EMPTY_DEFAULT = "";
    private static final char PLACEHOLDER_DEFAULT_SEP = ':';
    private static final int SEPARATOR_NOT_FOUND = -1;
    private static final int AFTER_SEPARATOR_OFFSET = 1;
    private static final int INNER_START = 0;

    private final String accountName;
    private final String password;
    private final String baseUrl;
    private final String demoMode;

    private AppProperties(String accountName, String password, String baseUrl, String demoMode) {
        this.accountName = accountName;
        this.password = password;
        this.baseUrl = baseUrl;
        this.demoMode = demoMode;
    }

    public static AppProperties from(Properties properties) {
        String accountName = resolveEnvPlaceholder(properties.getProperty(PROP_ACCOUNT_NAME, EMPTY_DEFAULT));
        String password = resolveEnvPlaceholder(properties.getProperty(PROP_PASSWORD, EMPTY_DEFAULT));
        String baseUrl = resolveEnvPlaceholder(properties.getProperty(PROP_BASE_URL, EMPTY_DEFAULT));
        String demoMode = resolveEnvPlaceholder(properties.getProperty(PROP_DEMO_MODE, EMPTY_DEFAULT));
        return new AppProperties(accountName, password, baseUrl, demoMode);
    }

    public Credentials credentials() {
        return new Credentials(accountName, password);
    }

    public String baseUrl() {
        return baseUrl;
    }

    /** Returns the demo.mode property value as a string. */
    public String demoMode() {
        return demoMode;
    }

    private static String resolveEnvPlaceholder(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (!trimmed.startsWith(PLACEHOLDER_PREFIX) || !trimmed.endsWith(PLACEHOLDER_SUFFIX)) {
            return trimmed;
        }
        String inner = trimmed.substring(PLACEHOLDER_PREFIX.length(), trimmed.length() - PLACEHOLDER_SUFFIX.length());
        String envName;
        String defaultValue = EMPTY_DEFAULT;
        int colonIndex = inner.indexOf(PLACEHOLDER_DEFAULT_SEP);
        if (colonIndex > SEPARATOR_NOT_FOUND) {
            envName = inner.substring(INNER_START, colonIndex);
            defaultValue = inner.substring(colonIndex + AFTER_SEPARATOR_OFFSET);
        } else {
            envName = inner;
        }
        String envValue = System.getenv(envName);
        return (envValue != null && !envValue.isBlank()) ? envValue : defaultValue;
    }
}
