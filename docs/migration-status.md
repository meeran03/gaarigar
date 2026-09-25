# Migration status

## Scope

The original backend and web admin are being restored on Railway with a new PostGIS database, Redis, and synthetic sample data. The Android clients have been repointed to the new service. The third client repository is an older mechanic UI prototype with no API layer; it is preserved and labelled as such.

## Verified locally

- Backend compiles on Java 17 / Spring Boot 3.5.16.
- Authentication regression tests reject legacy signing keys and refresh tokens used as access tokens, and read authority from the active database user.
- Application starts with PostGIS, initializes the schema, and seeds fictional data.
- Health, sample login, nearby service search, order listing, and category listing return successful responses.
- Read-only sample viewer signs into the original admin reports; counts come from the database.
- Public-source checks find no matches to the old backend credentials. A mistakenly saved HTML file masquerading as an Android PNG was replaced with a location vector icon.

## Integration configuration still required

The original third-party credentials are not used or published. Fresh Google Maps/Firebase configuration is needed for Android maps and push delivery. Payments, outbound SMS/email/push, and S3 uploads are disabled by default and need separate configuration and acceptance testing before use with real customers. Password-reset SMS routes are closed until that workflow is reviewed and configured.

This public deployment is populated with sample data. It does not dispatch real roadside assistance or charge customers. Database schema management currently uses Hibernate `update`, suitable for this fresh sample environment; use reviewed migrations before operating a real customer database.

Android CI builds and final Railway acceptance are recorded in the GitHub Actions history and follow-up updates to this document. A successful APK build alone does not establish on-device operation, map access, notification delivery, or Play Store publication.
