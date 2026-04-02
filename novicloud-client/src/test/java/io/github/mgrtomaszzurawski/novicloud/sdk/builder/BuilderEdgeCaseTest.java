/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty.AsortyCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj.KartaLojCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci.KontrahentCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy.RapPracyQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rapsprzed.RapSprzedQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat.StawkaVatCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarUpdateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Edge case tests for representative builders (F-09).
 *
 * <p>Covers boundary values, empty strings, date validation,
 * and special numeric values across five builder types.
 */
class BuilderEdgeCaseTest {

    private static final String EMPTY_STRING = "";
    private static final String WHITESPACE_ONLY = "   ";
    private static final String TEST_KOD = "X";
    private static final String TEST_NAZWA = "Y";
    private static final String TEST_NAZWA_FULL = "Name";
    private static final String VALID_DATE_START = "2026-01-15";
    private static final String VALID_DATE_END = "2026-01-31";
    private static final String INVALID_DATE_FORMAT = "15-01-2026";
    private static final String INVALID_DATE_SLASH_FORMAT = "2026/01/31";
    private static final String NONSENSE_DATE = "not-a-date";
    private static final int ZERO_VAT_ID = 0;
    private static final int EXEMPT_VAT_ID = -1;
    private static final String TEST_LOJ_KOD = "LOJ-EDGE";
    private static final String TEST_TOWAR_KOD = "CODE";
    private static final String TEST_TOWAR_NAZWA = "Name";
    private static final int STANDARD_VAT_RATE = 2300;
    private static final long BUILDER_ID_1 = 1L;
    private static final long BUILDER_ID_2 = 2L;
    private static final String BUILDER_NAZWA_1 = "A";
    private static final String BUILDER_NAZWA_2 = "B";
    private static final String TEST_KONTRAHENT_NAZWA = "Firma X";
    private static final String TEST_KONTRAHENT_NIP = "1234567890";

    // ---- TowarCreateBuilder edge cases ----

    @Test
    void towarBuilder_whenEmptyKodAndNazwa_buildsSuccessfully() {
        // given
        // No client-side validation on empty strings - server validates

        // when
        TowarCreateBuilder d = TowarCreateBuilder.builder(EMPTY_STRING, EMPTY_STRING).build();

        // then
        assertEquals(EMPTY_STRING, d.kod());
        assertEquals(EMPTY_STRING, d.nazwa());
    }

    @Test
    void towarBuilder_whenNullOptionalFields_allFieldsAreNull() {
        // given
        var builder = TowarCreateBuilder.builder(TEST_KOD, TEST_NAZWA)
                .id(null)
                .stawkaVat(null)
                .akcyzowy(null)
                .typ(null)
                .cenaDet(null)
                .jmId(null)
                .asortId(null);

        // when
        TowarCreateBuilder d = builder.build();

        // then
        assertNull(d.id());
        assertNull(d.stawkaVat());
        assertNull(d.akcyzowy());
        assertNull(d.typ());
        assertNull(d.cenaDet());
        assertNull(d.jmId());
        assertNull(d.asortId());
    }

    @Test
    void towarBuilder_whenWhitespaceOnlyKod_accepted() {
        // given
        // Whitespace-only kod is not rejected client-side

        // when
        TowarCreateBuilder d = TowarCreateBuilder.builder(WHITESPACE_ONLY, TEST_NAZWA_FULL).build();

        // then
        assertEquals(WHITESPACE_ONLY, d.kod());
    }

    // ---- RapPracyQueryBuilder date validation edge cases ----

    @Test
    void rapPracyQuery_whenValidDates_buildsSuccessfully() {
        // given
        var builder = RapPracyQueryBuilder.builder()
                .dataPocz(VALID_DATE_START)
                .dataKonc(VALID_DATE_END);

        // when
        RapPracyQueryBuilder q = assertDoesNotThrow(builder::build);

        // then
        assertEquals(VALID_DATE_START, q.dataPocz());
        assertEquals(VALID_DATE_END, q.dataKonc());
    }

    @Test
    void rapPracyQuery_whenDateFormatInvalid_throwsIllegalArgument() {
        // given
        var builder = RapPracyQueryBuilder.builder().dataPocz(INVALID_DATE_FORMAT);

        // when / then
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void rapPracyQuery_whenNonsenseDate_throwsIllegalArgument() {
        // given
        var builder = RapPracyQueryBuilder.builder().dataPocz(NONSENSE_DATE);

        // when / then
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void rapPracyQuery_whenNullDates_buildsWithDefaults() {
        // given
        // Null dates are allowed - server uses defaults (7 days ago / today)

        // when
        RapPracyQueryBuilder q = RapPracyQueryBuilder.builder()
                .dataPocz(null)
                .dataKonc(null)
                .build();

        // then
        assertNull(q.dataPocz());
        assertNull(q.dataKonc());
    }

    // ---- RapSprzedQueryBuilder date validation edge cases ----

    @Test
    void rapSprzedQuery_whenValidDate_buildsSuccessfully() {
        // given
        var builder = RapSprzedQueryBuilder.builder().dataPocz(VALID_DATE_START);

        // when
        RapSprzedQueryBuilder q = assertDoesNotThrow(builder::build);

        // then
        assertEquals(VALID_DATE_START, q.dataPocz());
    }

    @Test
    void rapSprzedQuery_whenInvalidDateFormat_throwsIllegalArgument() {
        // given
        var builder = RapSprzedQueryBuilder.builder().dataKonc(INVALID_DATE_SLASH_FORMAT);

        // when / then
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void rapSprzedQuery_whenEmptyDateString_throwsIllegalArgument() {
        // given
        var builder = RapSprzedQueryBuilder.builder().dataPocz(EMPTY_STRING);

        // when / then
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    // ---- StawkaVatCreateBuilder numeric edge cases ----

    @Test
    void stawkaVatBuilder_whenZeroVatId_accepted() {
        // given
        // id=0 is a valid VAT rate code (0% rate)

        // when
        StawkaVatCreateBuilder d = StawkaVatCreateBuilder.builder(ZERO_VAT_ID).build();

        // then
        assertEquals(ZERO_VAT_ID, d.id());
    }

    @Test
    void stawkaVatBuilder_whenNegativeVatId_accepted() {
        // given
        // Negative IDs represent exempt rates (e.g., -1 for "zw")

        // when
        StawkaVatCreateBuilder d = StawkaVatCreateBuilder.builder(EXEMPT_VAT_ID).build();

        // then
        assertEquals(EXEMPT_VAT_ID, d.id());
    }

    @Test
    void stawkaVatBuilder_whenNullVatId_throwsIllegalArgument() {
        // given / when / then
        assertThrows(IllegalArgumentException.class,
                () -> StawkaVatCreateBuilder.builder(null));
    }

    // ---- KartaLojCreateBuilder minimal required fields ----

    @Test
    void kartaLojBuilder_whenMinimalRequiredOnly_optionalsAreNull() {
        // given
        // Only kod is required

        // when
        KartaLojCreateBuilder d = KartaLojCreateBuilder.builder(TEST_LOJ_KOD).build();

        // then
        assertEquals(TEST_LOJ_KOD, d.kod());
        assertNull(d.typ());
        assertNull(d.waznaOd());
        assertNull(d.waznaDo());
        assertNull(d.nazwiskoImie());
        assertNull(d.email());
    }

    @Test
    void kartaLojBuilder_whenEmptyKod_accepted() {
        // given
        // Empty kod not rejected client-side

        // when
        KartaLojCreateBuilder d = KartaLojCreateBuilder.builder(EMPTY_STRING).build();

        // then
        assertEquals(EMPTY_STRING, d.kod());
    }

    // ---- T-08: Immutability tests ----

    @Test
    void towarBuilder_whenCalledTwice_returnsSameValues() {
        // given
        var builder = TowarCreateBuilder.builder(TEST_TOWAR_KOD, TEST_TOWAR_NAZWA)
                .stawkaVat(STANDARD_VAT_RATE);

        // when
        TowarCreateBuilder first = builder.build();
        TowarCreateBuilder second = builder.build();

        // then
        assertEquals(first.kod(), second.kod());
        assertEquals(first.nazwa(), second.nazwa());
        assertEquals(first.stawkaVat(), second.stawkaVat());
    }

    @Test
    void towarUpdateBuilder_whenTwoBuildersCreated_theyAreIndependent() {
        // given
        var builder1 = TowarUpdateBuilder.builder(BUILDER_ID_1).nazwa(BUILDER_NAZWA_1);
        var builder2 = TowarUpdateBuilder.builder(BUILDER_ID_2).nazwa(BUILDER_NAZWA_2);

        // when
        TowarUpdateBuilder d1 = builder1.build();
        TowarUpdateBuilder d2 = builder2.build();

        // then
        assertEquals(BUILDER_ID_1, d1.id());
        assertEquals(BUILDER_NAZWA_1, d1.nazwa());
        assertEquals(BUILDER_ID_2, d2.id());
        assertEquals(BUILDER_NAZWA_2, d2.nazwa());
    }

    @Test
    void kontrahentBuilder_whenCalledTwice_returnsSameValues() {
        // given
        var builder = KontrahentCreateBuilder.builder(TEST_KONTRAHENT_NAZWA)
                .nip(TEST_KONTRAHENT_NIP);

        // when
        KontrahentCreateBuilder first = builder.build();
        KontrahentCreateBuilder second = builder.build();

        // then
        assertEquals(first.nazwa(), second.nazwa());
        assertEquals(first.nip(), second.nip());
    }

    // ---- T-09: Required field null handling ----
    // SDK does not validate required fields client-side (server validates).
    // Exception: StawkaVat.id (already tested above - builder_whenNullVatId_throwsIllegalArgument).

    @Test
    void towarBuilder_whenNullRequiredFields_acceptedClientSide() {
        // given / when
        TowarCreateBuilder d = TowarCreateBuilder.builder(null, null).build();

        // then
        assertNull(d.kod());
        assertNull(d.nazwa());
    }

    @Test
    void asortyBuilder_whenNullNazwa_acceptedClientSide() {
        // given / when
        AsortyCreateBuilder d = AsortyCreateBuilder.builder(null).build();

        // then
        assertNull(d.nazwa());
    }

    @Test
    void kontrahentBuilder_whenNullNazwa_acceptedClientSide() {
        // given / when
        KontrahentCreateBuilder d = KontrahentCreateBuilder.builder(null).build();

        // then
        assertNull(d.nazwa());
    }

    @Test
    void kartaLojBuilder_whenNullKod_acceptedClientSide() {
        // given / when
        KartaLojCreateBuilder d = KartaLojCreateBuilder.builder(null).build();

        // then
        assertNull(d.kod());
    }
}
