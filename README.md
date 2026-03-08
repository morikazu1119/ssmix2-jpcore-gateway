# ssmix2-jpcore-gateway

`ssmix2-jpcore-gateway` is a read-only, intentionally narrow OSS gateway that converts a limited subset of SS-MIX2 standardized storage inputs into Japanese FHIR R4 artifacts aligned with JP Core where practical.

## Purpose

- Provide a maintainable reference implementation for SS-MIX2 to FHIR conversion.
- Keep parsing, canonical modeling, mapping, and validation clearly separated.
- Support file-based ingest for an MVP that is easy to understand and test.

## MVP Scope

- Input: file-based ingest from a very limited subset of SS-MIX2-like fixture data.
- Output: FHIR R4 `Bundle` output containing `Patient`, `Encounter`, `Observation`, `MedicationRequest`, and `DocumentReference`.
- Read-only conversion only.
- Built-in validation using HAPI FHIR validator wrappers.

## Non-Goals

- Full EMR behavior.
- Bidirectional sync or write-back.
- Realtime conversion pipelines.
- SMART on FHIR or production-grade auth.
- Full SS-MIX2 coverage or support for every Japanese profile.
- A production-ready universal converter.

## Architecture

The repository is a Gradle multi-module monorepo:

```text
.
├── app
│   └── Spring Boot REST API, ingest endpoints, audit logging, config, health
├── core
│   └── parser contracts, canonical model, mapping contracts, validation wrapper
├── profiles-jp
│   └── JP Core mapping definitions, fixtures, conformance matrix, examples
└── deploy
    └── Docker Compose, env templates, local dev scripts, container image
```

Conversion flow:

1. `Ssmix2Parser` reads a constrained file layout from disk.
2. `CanonicalModelAssembler` builds a canonical intermediate model.
3. `FhirBundleMapper` produces a FHIR R4 `Bundle`.
4. `FhirValidationService` validates the output before it is returned or stored.

## Why Canonical Modeling Exists

Canonical modeling isolates source-specific SS-MIX2 parsing concerns from downstream output concerns. This keeps the core domain model independent from FHIR classes, makes assumptions visible, and gives the project a stable place to track missing fields, local codes, unresolved mappings, and raw source text before any target-specific serialization is attempted.

## Narrow Fixture Format Used In This Scaffold

This first pass does **not** implement real SS-MIX2 parsing. Instead, it accepts a documented placeholder layout under resource-type directories:

```text
sample-001/
├── patient/PAT-001.txt
├── encounter/ENC-001.txt
├── observation/OBS-001.txt
├── medication-request/MED-001.txt
└── document-reference/DOC-001.txt
```

Each file contains `key=value` pairs. This is deliberately limited so the pipeline remains explicit and testable. Real SS-MIX2 standardized storage parsing is left as a tracked TODO.

## Local Setup

### Option 1: Docker Compose

1. Copy `deploy/env/.env.example` to `deploy/env/.env`.
2. Start the stack:

   ```bash
   ./deploy/scripts/dev-up.sh
   ```

3. Check health:

   ```bash
   curl http://localhost:8080/health
   ```

4. Trigger a sample ingest:

   ```bash
   curl -X POST http://localhost:8080/ingest/ssmix2 \
     -H 'Content-Type: application/json' \
     -d '{
       "bundleId": "sample-001",
       "facilityId": "demo-hospital",
       "sourcePath": "/fixtures/ssmix2/sample-001"
     }'
   ```

5. Retrieve the generated bundle:

   ```bash
   curl http://localhost:8080/fhir/Bundle/sample-001
   ```

### Option 2: Gradle

The build targets Java 21 and uses the Gradle wrapper.

```bash
./gradlew test
./gradlew :app:bootRun
```

## Assumptions And Explicit Gaps

- The parser currently supports only a tiny, file-system based subset of input.
- Mapping logic is intentionally skeletal and documents unsupported assumptions with explicit TODOs.
- Validation is wired through HAPI FHIR base R4 validation support. JP Core package-based validation is a follow-up task.
- Bundle persistence is in-memory for the scaffold. PostgreSQL is provisioned in `deploy/` for the next increment.

## API Placeholders

- `POST /ingest/ssmix2`
- `GET /fhir/Bundle/{id}`
- `GET /health`

## Testing

The first pass includes JUnit 5 tests for the core parser, canonical assembler, and conversion pipeline abstractions.
