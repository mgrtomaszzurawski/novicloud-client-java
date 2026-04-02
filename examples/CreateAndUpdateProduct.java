/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package examples;

import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Towar;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarCreateBuilder;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarUpdateBuilder;

/**
 * Create, read, update, and delete a product.
 * Towary uses soft-delete: deleteById() sets aktywny=false, record stays in database.
 */
public class CreateAndUpdateProduct {

    public static void main(String[] args) {
        try (NoviCloudClient client = NoviCloudClient.create(
                System.getenv("NOVICLOUD_ACCOUNT_NAME"),
                System.getenv("NOVICLOUD_PASSWORD"))) {

            // Create - kod and nazwa are required (enforced by builder factory)
            String id = client.towary().create(
                    TowarCreateBuilder.builder("EXAMPLE-001", "Example Product")
                            .stawkaVat(2300)    // 23% VAT (in hundredths)
                            .cenaDet(49.99)
                            .aktywny(true)
                            .build());
            System.out.println("Created product id=" + id);

            // Read back
            long numericId = Long.parseLong(id);
            Towar product = client.towary().getById(numericId);
            System.out.println("Name: " + product.nazwa());

            // Update - only changed fields, ID identifies the record
            client.towary().update(
                    TowarUpdateBuilder.builder(numericId)
                            .nazwa("Example Product (updated)")
                            .cenaDet(59.99)
                            .build());

            // Delete (soft) - sets aktywny=false
            client.towary().deleteById(numericId);

            // Record still exists but is inactive
            Towar deleted = client.towary().getById(numericId);
            System.out.println("After delete: aktywny=" + deleted.aktywny()); // false
        }
    }
}
