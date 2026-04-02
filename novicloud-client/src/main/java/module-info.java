/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
module io.github.mgrtomaszzurawski.novicloud {
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.openapitools.jackson.nullable;
    requires jakarta.annotation;
    requires org.slf4j;
    exports io.github.mgrtomaszzurawski.novicloud.sdk;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.dokumenty;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasjerzy;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasy;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.pozdok;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.rapsprzed;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.sprzedaz;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.stanymag;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.exception;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.paging;
    exports io.github.mgrtomaszzurawski.novicloud.sdk.model;

    opens io.github.mgrtomaszzurawski.novicloud.client.model to com.fasterxml.jackson.databind;
}
