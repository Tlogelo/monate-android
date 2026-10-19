# Task 2 checklist (XISD6329MM) and project-plan alignment

Status key: **Done** = implemented and checked, **Check** = implemented but needs a real-phone / account step by the team,
**Team** = not code, the team has to do it.

> Source of the requirements: the Task 2 brief as recorded in the team's notes. The module document
> (`XISD6329MM.docx`) was rights-protected when this update was made and could not be opened, so please compare this table
> with the original once and tell us anything missing.

## Task 2 requirements

| Requirement | Status | Where / how |
|---|---|---|
| Working Android app on GitHub | Done (code) | this repository |
| Runs on a **real phone**, not only an emulator | **Check** | Build and run on a phone (README > Run the app); record this for the demo |
| Update the website | **Team** | the website is not part of this repository |
| Use DevOps | Done / **Team** | GitHub Actions workflow; keep the Azure DevOps board updated (below) |
| User support: help system | Done | Profile > Help & Support: quick guides, FAQs (database), call/email, message form |
| User support: user documentation | Done | [USER_GUIDE.md](USER_GUIDE.md) |
| Register / log in with **encrypted password** | Done | PBKDF2-HMAC-SHA256, 120 000 rounds, random salt per user (`PasswordHasher`); tests in `AuthTests` |
| **SSO** | **Check** | Google sign-in implemented; needs the Web client ID, see [SSO_SETUP.md](SSO_SETUP.md) |
| Settings | Done | Profile > Settings: notifications, default collection time, default payment, delete account |
| Database with **10+ records per table** | Done | 11 tables, see [DATABASE.md](DATABASE.md); proven by `SeedDataTest` and `DatabaseTest` |
| **GitHub Actions** automated testing | Done | `.github/workflows/android.yml`, 91 unit tests, results table in each run summary |
| Azure DevOps updates | **Team** | move the Task 2 work items on the board (suggested items below) |
| Demo video with voice-over | **Team** | suggested script below |
| Regular team meetings | **Team** | keep short minutes (date, who attended, decisions) as evidence |

## What was wrong and is now fixed

| Problem found | Fix |
|---|---|
| Home screen was a "coming soon" placeholder (plan promises a dashboard) | Real Home screen with specials, points, about, hours, contact |
| No item details screen (plan 8.2) | New Item details screen with quantity, reviews |
| All data was in memory: lost on restart, and **every customer saw the same orders and points** | SQLite database, orders/points/reviews saved per customer |
| Password hashing used PBKDF2-SHA1 with only 10 000 rounds | PBKDF2-SHA256, 120 000 rounds, rounds stored with the hash, constant-time compare (minSdk raised 24 to 26) |
| No SSO although a Google library was listed | Google sign-in with Credential Manager |
| Settings had one switch that did nothing | Notifications really sent, default time/payment used at checkout, delete account |
| Help was a static list inside the code | FAQs from the database, quick guides, messages saved to the database, user guide document |
| README claimed Retrofit/Flask/MySQL that the app never used, and listed "Mentor" as a role (it is a team member's first name) | README rewritten to match the code and the team table |
| Unused libraries (Retrofit, OkHttp, Gson, Coil, CardView, old Google auth) and a version catalog that disagreed with the build file | Removed; one version catalog now drives the build |
| `allowBackup="true"` with encrypted preferences (restores can corrupt the key) | Backup switched off |
| Leftover `ExampleUnitTest`, tests sharing global state, `assert()` that can be silently disabled | Replaced with isolated tests using fake stores |
| Mixed Windows/Unix line endings in several files | Normalised to LF and `.gitattributes` added (this makes many files show as changed in git) |

## Alignment with the project plan (section 8.2 "Android Application")

| Plan item | Status |
|---|---|
| Login and registration | Done |
| Dashboard | Done (Home screen, was a "coming soon" placeholder) |
| Menu | Done (now loaded from the database, 25 items in 10 categories) |
| Item details | Done (new screen, was missing) |
| Cart, Checkout | Done |
| Order tracking, Order history | Done (orders are now saved per customer; before they were shared and lost on restart) |
| Profile | Done |
| Reviews, Loyalty points | Done |
| Help / support | Done (extended) |
| Trust elements (contact details, location, about, social links) | Done on Home; address and social links need real values in `strings.xml` |
| Engagement (promotions, loyalty, reviews) | Done |
| Kitchen management (8.3) and administration (8.4) | Not in the Android app (staff features of the web system) |

## Things in the plan that do not match what was built

Please decide how to word these in the report:

1. **Back end / database technology.** The plan says Python/Flask + MySQL (and the WBS table says Node.js/MySQL).
   The app uses an on-device SQLite database behind repository classes, so it runs on a phone with no server.
   Moving to a Flask/MySQL API later only means replacing the classes in `data/db/`.
2. **Team spelling.** The plan's team table spells names differently from the cover page
   (for example "Tshomologo"/"Makwena" vs "Tshimologo"/"Mmakwena") and one student number differs
   (`St10458052` in the table vs `ST104588052` on the cover).
3. The plan's order statuses (Received, Preparing, Ready, Delivered) match the app.

## Suggested Azure DevOps work items (Task 2)

Google sign-in, Settings screen, Help system + guides, Database (11 tables, 10+ records), Item details screen,
Home dashboard, GitHub Actions testing, User documentation, Demo video, Website update.

## Suggested demo-video order (about 5 minutes, with voice-over)

1. GitHub repository and the green GitHub Actions run (open the test summary table).
2. Register a new user, then log out and log in; mention the hashed password.
3. Continue with Google.
4. Home, Menu, item details, add to cart, checkout.
5. Orders: advance the status with the demo button and show the notification.
6. Loyalty rewards, review, Settings (default time and payment), Help & Support.
7. Show the database tables (Android Studio > App Inspection > Database Inspector) with 10+ rows each.

## Known limitations

- The Google ID token is not verified by a server (there is no server); see SSO_SETUP.md.
- The **Demo: next status** button stands in for the kitchen staff app.
- The R25 fee is charged on every order, including collection orders (existing behaviour, not changed).
- Sample promotions and sample customers are demo content.
- If the phone cannot create the encrypted preferences file, the session falls back to normal private storage.
