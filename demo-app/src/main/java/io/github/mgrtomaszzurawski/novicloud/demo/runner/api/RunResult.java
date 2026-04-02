/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.demo.runner.api;

public final class RunResult {

    public enum Status { OK, FAIL, SKIP }

    private static final String ICON_OK   = "[OK  ]";
    private static final String ICON_FAIL = "[FAIL]";
    private static final String ICON_SKIP = "[SKIP]";
    private static final String FORMAT_WITH_DETAIL = "%s %s - %s";
    private static final String FORMAT_NO_DETAIL = "%s %s";

    private final String label;
    private final Status status;
    private final String detail;

    private RunResult(String label, Status status, String detail) {
        this.label = label;
        this.status = status;
        this.detail = detail;
    }

    public static RunResult ok(String label) {
        return new RunResult(label, Status.OK, null);
    }

    public static RunResult fail(String label, String detail) {
        return new RunResult(label, Status.FAIL, detail);
    }

    public static RunResult skip(String label, String reason) {
        return new RunResult(label, Status.SKIP, reason);
    }

    public String label() { return label; }
    public Status status() { return status; }
    public String detail() { return detail; }

    @Override
    public String toString() {
        String icon = switch (status) {
            case OK   -> ICON_OK;
            case FAIL -> ICON_FAIL;
            case SKIP -> ICON_SKIP;
        };
        return detail != null
                ? String.format(FORMAT_WITH_DETAIL, icon, label, detail)
                : String.format(FORMAT_NO_DETAIL, icon, label);
    }
}
