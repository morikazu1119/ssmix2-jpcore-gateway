# JP Core Conformance Matrix

| Canonical resource | FHIR resource | JP Core profile | MVP status | Notes |
| --- | --- | --- | --- | --- |
| Patient | Patient | `JP_Patient` | Placeholder | Basic demographics only |
| Encounter | Encounter | `JP_Encounter` | Placeholder | Minimal encounter state and class |
| Observation | Observation | `JP_Observation_Common` | Placeholder | String-valued observations only |
| MedicationRequest | MedicationRequest | `JP_MedicationRequest` | Placeholder | Basic medication coding only |
| DocumentReference | DocumentReference | `JP_DocumentReference` | Placeholder | Metadata only, no binary payload |

## Known Gaps

- Package-based JP Core profile validation is not yet wired in.
- Terminology binding coverage is incomplete.
- Real SS-MIX2 segment parsing is not yet implemented.

