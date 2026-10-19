# Monate Chicken & Steak: Android App

![Android CI](https://github.com/Tlogelo/monate-android/actions/workflows/android.yml/badge.svg)

Android ordering app for **Monate Chicken & Steak**, built by **Speed Innovators** (Team 5) for module
**XISD6329MM** (Work Integrated Learning). It replaces the restaurant's paper-based process:
customers browse the menu, order ahead, track their order, earn loyalty points and get help.

## Features

| Area | What the app does |
|---|---|
| Accounts | Register and log in (passwords salted and hashed with PBKDF2-SHA256, never stored as plain text) |
| Single sign-on | **Continue with Google** (Android Credential Manager), see [docs/SSO_SETUP.md](docs/SSO_SETUP.md) |
| Home / dashboard | Greeting, quick actions, loyalty points, specials, about us, opening hours, call / email / map / social links |
| Menu | Categories, search, **item details** screen with quantity and customer reviews |
| Cart & checkout | Quantities, collection time, payment method, order summary |
| Orders | Order history and live status (Received, Preparing, Ready, Delivered), phone notifications, order again |
| Loyalty | Points, Bronze/Silver/Gold tiers, rewards with till codes |
| Reviews | Star ratings and comments, earn bonus points |
| Settings | Notifications switch, default collection time, default payment, change name/password, delete account |
| Help & support | Quick guides, FAQs, call/email shortcuts, message form (saved to the database) |
| Database | On-device SQLite, 11 tables, **10+ records in every table** ([docs/DATABASE.md](docs/DATABASE.md)) |

## Tech stack

- Kotlin, XML layouts, Material 3, ViewBinding
- Jetpack Navigation, LiveData (cart), Coroutines
- **SQLite** (`SQLiteOpenHelper`) behind repository + store classes
- Encrypted SharedPreferences for the session and settings
- Credential Manager + Google ID for single sign-on
- JUnit unit tests, **GitHub Actions** for automated testing and building

## Project structure

```
app/src/main/java/com/example/monatechickensteak/
  auth/    Google single sign-on helper
  data/    repositories (rules), store interfaces, seed data, session
  data/db/ SQLite helper (schema + seeding) and SQLite stores
  model/   data classes
  ui/      screens (fragments) and adapters
  util/    validators, password hashing, loyalty rules, notifications, helpers
app/src/test/          unit tests (run on a laptop and in CI)
app/src/androidTest/   database tests (run on a phone / emulator)
docs/                  user guide, database, SSO setup, Task 2 checklist
```

## Run the app

1. Clone the repository and open it in Android Studio. Let Gradle sync.
2. Connect a **physical Android phone** (8.0 / API 26 or newer) with USB debugging on.
3. Run the `app` configuration.

Demo account: `demo@monate.co.za` / `Monate@123`
(all sample customers use the same password; see the table in [docs/DATABASE.md](docs/DATABASE.md)).

Before showing the Google button working, add your Web client ID in
`app/src/main/res/values/strings.xml` (`google_web_client_id`). Also fill in the restaurant's
address and social links there; empty values are simply hidden in the app.

## Run the tests

```
./gradlew testDebugUnitTest            # unit tests (Windows: gradlew.bat testDebugUnitTest)
./gradlew connectedDebugAndroidTest    # database tests on a connected phone / emulator
```

## Continuous integration (GitHub Actions)

Every push and pull request runs `.github/workflows/android.yml`, which

1. runs the unit tests and prints a pass/fail table in the run summary,
2. builds the debug APK,
3. uploads the test report and the APK as downloadable artifacts.

If a build or test fails the change is sent back to development for correction.

## Documentation

- [User guide](docs/USER_GUIDE.md): how customers use the app
- [Database](docs/DATABASE.md): tables, relationships, sample data
- [Google sign-in setup](docs/SSO_SETUP.md)
- [Task 2 checklist](docs/TASK2_CHECKLIST.md): requirement-by-requirement status and what is still open

## Team (Speed Innovators, Team 5)

| Member | Role |
|---|---|
| Mentor Lukheli | Team leader / Project manager |
| Katlego Motsoaledi | Backend developer & tester |
| Tlogelo Molele | UI/UX designer & front-end developer |
| Tshimologo Mokiba | Business analyst |
| Mmakwena Masenya | Technical writer |
