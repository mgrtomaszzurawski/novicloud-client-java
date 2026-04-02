/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package examples;

import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.RaportSprzedazy;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.RaportPracy;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rapsprzed.RapSprzedQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rapsprzed.RapSprzedGroup;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy.RapPracyQueryBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy.RapPracyGroup;

/**
 * Sales and work reports require date range. Different from CRUD endpoints:
 * read-only, grouped by dimension (product, shop, cashier, etc.).
 */
public class WorkWithReports {

    public static void main(String[] args) {
        try (NoviCloudClient client = NoviCloudClient.create(
                System.getenv("NOVICLOUD_ACCOUNT_NAME"),
                System.getenv("NOVICLOUD_PASSWORD"))) {

            // Sales report grouped by product for Q1 2026
            for (RaportSprzedazy r : client.rapSprzed().list(
                    RapSprzedQueryBuilder.builder()
                            .dataPocz("2026-01-01")
                            .dataKonc("2026-03-31")
                            .grupowanie(RapSprzedGroup.TOWAR)
                            .build())) {
                System.out.printf("  towar=%s: brutto=%.2f, ilosc=%.0f%n",
                        r.towarId(), r.sprzBrutto(), r.ilosc());
            }

            // Work hours report grouped by cashier
            int workRecords = client.rapPracy().count(
                    RapPracyQueryBuilder.builder()
                            .dataPocz("2026-01-01")
                            .dataKonc("2026-03-31")
                            .grupowanie(RapPracyGroup.KASJER)
                            .build());
            System.out.println("Work report records: " + workRecords);
        }
    }
}
