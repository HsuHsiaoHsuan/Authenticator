# Project Review Log

Created: 2026-06-30

This file tracks the project-wide review suggestions and their improvement status.

| # | Suggestion | Status | Notes |
|---|------------|--------|-------|
| 1 | Fix QR scan runtime crash risk from AndroidX `by viewModels()` creating a Koin ViewModel with constructor dependencies. | Fixed | `MainActivity` now gets `MainViewModel` from Koin. |
| 2 | Restore an add-account entry after accounts already exist. | Fixed | `TotpListScreen` now shows Search and Add actions when accounts are present. Search also filters the displayed list. |
| 3 | Remove logs that expose QR payloads or TOTP secrets. | Fixed | QR/secret logs were replaced with non-sensitive messages or removed. |
| 4 | Unify deprecated QR reader saving behavior with the encrypted main QR flow. | Fixed | `QrCodeReaderViewModel` now uses `convertTotpDataToTOTPAccount`, which encrypts the secret before insert. |
| 5 | Align DAO query/delete behavior with the `issuer + accountName` primary key and avoid silent overwrite on duplicates. | Fixed | DAO insert uses `IGNORE`; get/delete use both issuer and account name. |
| 6 | Evaluate/remove Kotzilla analytics, tracked key/config files, and the lint warning from that dependency. | Fixed | Kotzilla plugin, SDK, Koin analytics hook, key/config files were removed; future Kotzilla files are ignored. |
| 7 | Fix failing JVM unit tests caused by direct Android `Uri` usage. | Fixed | OTP auth URI parsing now uses JVM-compatible APIs. |
| 8 | Broaden TOTP support beyond fixed SHA1/6 digits/30 seconds and handle invalid secrets gracefully. | Pending | Suggested for the next improvement batch. |
| 9 | Configure Room schema export and migrations. | Pending | Suggested before changing the database schema in future releases. |
| 10 | Harden release build settings and update target SDK, AGP, Gradle, and dependencies. | Pending | Lint reported multiple version update warnings. |
| 11 | Avoid splash navigation races caused by the first-time-open default value. | Pending | Suggested to model loading state explicitly. |
| 12 | Avoid LazyColumn keys that include the current passcode. | Pending | Suggested to keep item identity stable across OTP refreshes. |
| 13 | Remove deprecated Fragment/ViewBinding/navigation-fragment/legacy dependencies if Compose is now the only UI path. | Pending | Suggested cleanup. |
| 14 | Replace placeholder/dummy strings and finish tutorial copy. | Pending | Suggested product polish. |
| 15 | Expand README with build, test, architecture, security, and release notes. | Pending | Suggested documentation cleanup. |
