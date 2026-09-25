# Migration status

## Deployed scope

The original Spring Boot backend and web admin are running on Railway with a new PostGIS database, Redis, and synthetic sample data. Both databases have persistent volumes. The Android clients target the new HTTPS/WSS service. The third client repository is an older mechanic UI prototype with no API layer; it is preserved and labelled as such.

The original private repositories and their history were not published. This repository contains a cleaned source snapshot. No old customer database was copied.

## Verified behavior

- Java 17 / Spring Boot 3.5.16 build and authentication regression tests pass in GitHub Actions and the Docker build.
- The deployed public endpoint passes health, sample login, nearby search, category listing, and order listing checks.
- A fictional cash booking passed creation, provider acceptance, start, and completion on Railway. A customer could not start a provider's job.
- Cross-account customer/order reads, unauthenticated admin registration, and customer catalog writes are rejected.
- Live STOMP chat accepts an order participant, enforces the sender identity and channel on the server, and rejects an unrelated subscriber.
- Every admin listing/report page was opened in the browser. The sample viewer can read data; an attempted form submission did not create a category. Private administrator login was verified separately.
- Report fixes distinguish completed and cancelled provider orders and use the selected date interval. Report table headings now match the query output.
- All three Android projects build into debug APKs in GitHub Actions. Source for the connected apps uses the Railway URL and server-issued chat identity.
- The source gate passes, and a comparison against old backend credentials found no retained matches. A saved HTML page disguised as an Android image was replaced with a location vector.

The initial seed is 8 customers, 6 mechanics, 3 fuel stations, 4 categories, 6 services, and 36 orders. Live acceptance added one completed fictional booking. Seed startup is idempotent and does not reset existing records.

## Remaining work for real customers

Fresh Google Maps and Firebase configuration is needed for Android maps and push delivery. Payments, outbound SMS/email/push, and S3 uploads are disabled and require fresh provider configuration and acceptance testing. Password-reset SMS routes remain closed until reviewed and configured. Rotate credentials in the old private repositories through their providers before reusing those accounts.

This deployed original application is populated with sample data. It does not dispatch real roadside assistance or charge customers. Schema management currently uses Hibernate `update`; adopt reviewed database migrations and backups before operating a real customer database. A wider review of concurrency, payments, account lifecycle, rate limits, and operational monitoring is still needed.

The APKs are evaluation/debug builds. They have not been tested on a physical Android device and are not published to the Play Store. Sample nearby-provider results require a location near Islamabad (33.6844, 73.0479). The third APK is a standalone historical UI prototype, not a third connected production app.

## Re-deploying

The current Railway deployment was uploaded from `backend/` with the Railway CLI. GitHub Actions validates source and builds Android artifacts; pushing to GitHub alone does not deploy the backend.

```sh
railway up backend --path-as-root --service backend \
  --project 08ede4e8-5e63-4004-a595-e975ea99338c \
  --environment production --detach
```

Run the acceptance scripts in `verification/` against the resulting public endpoint. `GAARIGAR_TEST_BOOKING=1` explicitly enables creation of another synthetic booking.
