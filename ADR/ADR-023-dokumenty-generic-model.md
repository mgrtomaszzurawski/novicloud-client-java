# ADR-023: DokumentyQuery and Dokument model stay generic - no per-type variants

## Status

Accepted

## Context

The `/dokumenty` endpoint is read-only and returns documents of many types, distinguished
by a `typ_dok` field (e.g., type 11 = MM shift document, receipts, supplier invoices, etc.).
Each document type populates a different subset of fields; some fields are meaningful for
cash documents only, others for supplier flows.

Live API verification (2026-03-22) revealed two specific problems:

**1. Date filter semantics are unreliable.**
`data_wystawienia` min/max filters do not reliably match the business "date of issue" shown
on the document. Observed behavior is consistent with filtering on an internal row-creation
timestamp, not the business-entered date. Example: a document created on 2026-03-22 with
a business date of 2026-03-20 still appears when filtering `data_wystawienia >= 2026-03-22`.
This is not consistent across `typ_dok` values; receipts and MM documents behave differently.

**2. The typ_dok field matrix is inconsistent and incompletely documented.**
The provider documentation does not fully specify which fields apply to which document types.
Fields like `nr_dok` appear in live responses but are absent from the published spec.
Modeling per-type response classes would require guessing the full field matrix from
black-box observation, which is fragile.

## Decision

**Keep `DokumentyQuery` and the generated `Dokument` model generic.**

- No per-type query builders (e.g., no `ParagonyQuery`, `MMQuery`).
- No per-type response model variants.
- Date filter limitations are documented in SDK javadoc on `DokumentyQuery`.
- SDK docs warn that field presence depends on `typ_dok` and that date filter semantics
  are provider-dependent.

## Rationale

1. **The server behavior is the variable, not the client model.** Modeling complexity in
   the SDK to compensate for inconsistent server behavior adds maintenance weight without
   guaranteeing correctness.

2. **Business logic belongs in the application layer.** An application using the SDK knows
   which `typ_dok` values it cares about and can implement its own field validation,
   default handling, and date interpretation on top of the generic model.

3. **YAGNI.** No current use case requires type-specific query builders. Adding them
   speculatively would lock in a design based on incomplete provider documentation.

4. **Pragmatic decision.** The "right" model would require exhaustive provider cooperation
   to specify. The SDK's role is transport; document the limitation and move on.

## Consequences

- `DokumentyQuery` exposes generic date and `typ_dok` filters as-is from the OpenAPI spec.
- Callers must handle `NoviCloudSdkException` (HTTP 500 from the server) for edge cases
  in filter behavior.
- Date filter limitations are noted in code comments on `DokumentyQuery` date fields.
- If a future use case demands type-specific modeling, a new ADR is required before any
  per-type classes are added.
