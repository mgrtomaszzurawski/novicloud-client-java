/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk;

import io.github.mgrtomaszzurawski.novicloud.client.ApiClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty.AsortyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.asorty.AsortyClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.dokumenty.DokumentyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.dokumenty.DokumentyClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn.FormyPlatnClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.formyplatn.FormyPlatnClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary.JmiaryClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.jmiary.JmiaryClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj.KartyLojClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.kartyloj.KartyLojClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasjerzy.KasjerzyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.kasjerzy.KasjerzyClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasy.KasyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.kasy.KasyClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci.KontrahenciClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.kontrahenci.KontrahenciClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje.KrajeClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.kraje.KrajeClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.pozdok.PozdokClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.pozdok.PozdokClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy.RapPracyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.rappracy.RapPracyClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rapsprzed.RapSprzedClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.rapsprzed.RapSprzedClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy.SklepyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.sklepy.SklepyClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.sprzedaz.SprzedazClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.sprzedaz.SprzedazClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stanymag.StanyMagClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.stanymag.StanyMagClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat.StawkiVatClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.stawkivat.StawkiVatClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowaryClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.towary.TowaryClientImpl;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty.WalutyClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.waluty.WalutyClientImpl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;

/**
 * Entry point for the NoviCloud SDK.
 *
 * <p>Provides access to all 18 resource clients through dedicated accessor methods.
 * Create an instance using the static factory methods or the builder:
 *
 * <pre>{@code
 * // quick start with default settings
 * NoviCloudClient client = NoviCloudClient.create(accountName, password);
 *
 * // custom base URL (e.g. for testing against a sandbox)
 * NoviCloudClient client = NoviCloudClient.create(baseUrl, accountName, password);
 *
 * // full control via builder
 * NoviCloudClient client = NoviCloudClient.builder()
 *     .baseUrl("https://api.example.com")
 *     .connectTimeout(Duration.ofSeconds(10))
 *     .readTimeout(Duration.ofMinutes(1))
 *     .retryPolicy(RetryPolicy.builder().maxAttempts(5).build())
 *     .build(accountName, password);
 * }</pre>
 *
 * <p>The client and all resource clients are thread-safe and intended to be shared.
 * Create one instance per application and reuse it.
 *
 * <p>Every SDK operation may throw a subclass of
 * {@link io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException}.
 * See that class for the full exception hierarchy and error-handling examples.
 *
 * @see RetryPolicy
 * @see io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException
 * @since 1.0.0
 */
public final class NoviCloudClient implements AutoCloseable {

    private static final String USER_AGENT = "novicloud-client-java/2.0.0";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String BASIC_AUTH_PREFIX = "Basic ";
    private static final char CREDENTIALS_SEPARATOR = ':';
    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30);
    private static final String ERR_CLOSED = "NoviCloudClient has been closed";
    private static final String ERR_NULL_ACCOUNT = "accountName must not be null";
    private static final String ERR_NULL_PASSWORD = "password must not be null";

    private volatile boolean closed;
    private final TowaryClient towaryClient;
    private final AsortyClient asortyClient;
    private final JmiaryClient jmiaryClient;
    private final StawkiVatClient stawkiVatClient;
    private final WalutyClient walutyClient;
    private final KrajeClient krajeClient;
    private final FormyPlatnClient formyPlatnClient;
    private final KontrahenciClient kontrahenciClient;
    private final SklepyClient sklepyClient;
    private final KasyClient kasyClient;
    private final KasjerzyClient kasjerzyClient;
    private final DokumentyClient dokumentyClient;
    private final PozdokClient pozdokClient;
    private final StanyMagClient stanyMagClient;
    private final SprzedazClient sprzedazClient;
    private final RapSprzedClient rapSprzedClient;
    private final RapPracyClient rapPracyClient;
    private final KartyLojClient kartyLojClient;

    private NoviCloudClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        RetryPolicy policy = retryPolicy != null ? retryPolicy : RetryPolicy.defaultPolicy();
        this.towaryClient = new TowaryClientImpl(apiClient, accountName, policy);
        this.asortyClient = new AsortyClientImpl(apiClient, accountName, policy);
        this.jmiaryClient = new JmiaryClientImpl(apiClient, accountName, policy);
        this.stawkiVatClient = new StawkiVatClientImpl(apiClient, accountName, policy);
        this.walutyClient = new WalutyClientImpl(apiClient, accountName, policy);
        this.krajeClient = new KrajeClientImpl(apiClient, accountName, policy);
        this.formyPlatnClient = new FormyPlatnClientImpl(apiClient, accountName, policy);
        this.kontrahenciClient = new KontrahenciClientImpl(apiClient, accountName, policy);
        this.sklepyClient = new SklepyClientImpl(apiClient, accountName, policy);
        this.kasyClient = new KasyClientImpl(apiClient, accountName, policy);
        this.kasjerzyClient = new KasjerzyClientImpl(apiClient, accountName, policy);
        this.dokumentyClient = new DokumentyClientImpl(apiClient, accountName, policy);
        this.pozdokClient = new PozdokClientImpl(apiClient, accountName, policy);
        this.stanyMagClient = new StanyMagClientImpl(apiClient, accountName, policy);
        this.sprzedazClient = new SprzedazClientImpl(apiClient, accountName, policy);
        this.rapSprzedClient = new RapSprzedClientImpl(apiClient, accountName, policy);
        this.rapPracyClient = new RapPracyClientImpl(apiClient, accountName, policy);
        this.kartyLojClient = new KartyLojClientImpl(apiClient, accountName, policy);
    }

    /**
     * Marks this client as closed. After calling {@code close()}, any attempt to obtain
     * a resource client throws {@link IllegalStateException}.
     *
     * <p>On JDK 17 the underlying {@code java.net.http.HttpClient} does not expose a
     * {@code close()} method, so no I/O resources are released. This method exists to
     * support the {@link AutoCloseable} contract and {@code try-with-resources} usage.
     * When the SDK targets JDK 21+, this method will delegate to {@code HttpClient.close()}.
     */
    @Override
    public void close() {
        closed = true;
    }

    /** Returns the client for the {@code towary} (goods) endpoint. */
    public TowaryClient towary() { ensureOpen(); return towaryClient; }

    /** Returns the client for the {@code asorty} (assortment groups) endpoint. */
    public AsortyClient asorty() { ensureOpen(); return asortyClient; }

    /** Returns the client for the {@code jmiary} (units of measure) endpoint. */
    public JmiaryClient jmiary() { ensureOpen(); return jmiaryClient; }

    /** Returns the client for the {@code stawkivat} (VAT rates) endpoint. */
    public StawkiVatClient stawkiVat() { ensureOpen(); return stawkiVatClient; }

    /** Returns the client for the {@code waluty} (currencies) endpoint. */
    public WalutyClient waluty() { ensureOpen(); return walutyClient; }

    /** Returns the client for the {@code kraje} (countries) endpoint. */
    public KrajeClient kraje() { ensureOpen(); return krajeClient; }

    /** Returns the client for the {@code formyplatn} (payment forms) endpoint. */
    public FormyPlatnClient formyPlatn() { ensureOpen(); return formyPlatnClient; }

    /** Returns the client for the {@code kontrahenci} (contractors/customers) endpoint. */
    public KontrahenciClient kontrahenci() { ensureOpen(); return kontrahenciClient; }

    /** Returns the client for the {@code sklepy} (stores) endpoint. */
    public SklepyClient sklepy() { ensureOpen(); return sklepyClient; }

    /** Returns the client for the {@code kasy} (cash registers) endpoint. */
    public KasyClient kasy() { ensureOpen(); return kasyClient; }

    /** Returns the client for the {@code kasjerzy} (cashiers) endpoint. */
    public KasjerzyClient kasjerzy() { ensureOpen(); return kasjerzyClient; }

    /** Returns the client for the {@code dokumenty} (documents) endpoint. */
    public DokumentyClient dokumenty() { ensureOpen(); return dokumentyClient; }

    /** Returns the client for the {@code pozdok} (document lines) endpoint. */
    public PozdokClient pozdok() { ensureOpen(); return pozdokClient; }

    /** Returns the client for the {@code stanymag} (warehouse stock levels) endpoint. */
    public StanyMagClient stanyMag() { ensureOpen(); return stanyMagClient; }

    /** Returns the client for the {@code sprzedaz} (sales transactions) endpoint. */
    public SprzedazClient sprzedaz() { ensureOpen(); return sprzedazClient; }

    /** Returns the client for the {@code rapsprzed} (sales reports) endpoint. */
    public RapSprzedClient rapSprzed() { ensureOpen(); return rapSprzedClient; }

    /** Returns the client for the {@code rappracy} (work reports) endpoint. */
    public RapPracyClient rapPracy() { ensureOpen(); return rapPracyClient; }

    /** Returns the client for the {@code kartyloj} (loyalty cards) endpoint. */
    public KartyLojClient kartyLoj() { ensureOpen(); return kartyLojClient; }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException(ERR_CLOSED);
        }
    }

    /**
     * Creates a client using the default NoviCloud API base URL.
     *
     * @param accountName the NoviCloud account name
     * @param password    the account password
     * @return a fully configured {@code NoviCloudClient}
     */
    public static NoviCloudClient create(String accountName, String password) {
        return builder().build(accountName, password);
    }

    /**
     * Creates a client pointing at a custom base URL.
     * Use this for sandbox environments or when the API host differs from the default.
     *
     * @param baseUrl     the full base URL of the NoviCloud API (e.g. {@code https://api.example.com})
     * @param accountName the NoviCloud account name
     * @param password    the account password
     * @return a fully configured {@code NoviCloudClient}
     */
    public static NoviCloudClient create(String baseUrl, String accountName, String password) {
        return builder().baseUrl(baseUrl).build(accountName, password);
    }

    /**
     * Returns a new builder for constructing a {@code NoviCloudClient} with custom settings.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link NoviCloudClient}.
     * Obtain an instance via {@link NoviCloudClient#builder()}.
     */
    public static final class Builder {

        private String baseUrl;
        private Duration connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        private Duration readTimeout = DEFAULT_READ_TIMEOUT;
        private RetryPolicy retryPolicy = RetryPolicy.defaultPolicy();

        private Builder() {
        }

        /**
         * Overrides the default NoviCloud API base URL.
         *
         * @param baseUrl full URL, e.g. {@code https://api.novicloud.pl}
         * @return this builder
         */
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }

        /**
         * Sets the TCP connection timeout. Default: 5 seconds.
         *
         * @param timeout connection timeout; must not be {@code null}
         * @return this builder
         */
        public Builder connectTimeout(Duration timeout) { this.connectTimeout = timeout; return this; }

        /**
         * Sets the read timeout for each request. Default: 30 seconds.
         *
         * @param timeout read timeout; must not be {@code null}
         * @return this builder
         */
        public Builder readTimeout(Duration timeout) { this.readTimeout = timeout; return this; }

        /**
         * Sets the retry policy for all client operations.
         * If not called, {@link RetryPolicy#defaultPolicy()} is used (3 attempts, exponential
         * backoff, retries on 429 and 5xx).
         *
         * @param policy the retry policy; {@code null} resets to the default policy
         * @return this builder
         */
        public Builder retryPolicy(RetryPolicy policy) {
            this.retryPolicy = policy != null ? policy : RetryPolicy.defaultPolicy();
            return this;
        }

        /**
         * Builds the {@link NoviCloudClient} and authenticates with the given credentials.
         *
         * @param accountName the NoviCloud account name
         * @param password    the account password
         * @return a fully configured and authenticated {@code NoviCloudClient}
         */
        public NoviCloudClient build(String accountName, String password) {
            Objects.requireNonNull(accountName, ERR_NULL_ACCOUNT);
            Objects.requireNonNull(password, ERR_NULL_PASSWORD);
            ApiClient apiClient = new ApiClient();
            if (baseUrl != null) {
                apiClient.updateBaseUri(baseUrl);
            }
            apiClient.setConnectTimeout(connectTimeout);
            apiClient.setReadTimeout(readTimeout);
            apiClient.setRequestInterceptor(requestBuilder -> requestBuilder
                    .header(HEADER_AUTHORIZATION, basicAuthHeader(accountName, password))
                    .header(HEADER_USER_AGENT, USER_AGENT));
            SimpleModule flexibleDates = new SimpleModule();
            flexibleDates.addDeserializer(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());
            apiClient.setObjectMapper(apiClient.getObjectMapper()
                    .registerModule(flexibleDates)
                    .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true));
            return new NoviCloudClient(apiClient, accountName, retryPolicy);
        }

        private static String basicAuthHeader(String username, String password) {
            String credentials = username + CREDENTIALS_SEPARATOR + password;
            String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            return BASIC_AUTH_PREFIX + encoded;
        }
    }
}
