# Monate Chicken & Steak: Android App

![Android CI](https://github.com/Tlogelo/monate-android/actions/workflows/android.yml/badge.svg)

Android ordering app for **Monate Chicken & Steak**, built for module **XISD6329MM**.
Customers can browse the menu, order online, track their orders, earn loyalty points
and get help, replacing the restaurant's paper-based process.

## Features

- Register and log in (passwords are salted and hashed, never stored as plain text)
- Menu with category filters and search
- Cart and checkout with collection time and payment method
- Order tracking (Received, Preparing, Ready, Delivered) and order history
- Profile and settings: change name, change password, notification switch
- Loyalty points, tiers and rewards
- Reviews and ratings
- Help and Support with FAQs, call and email shortcuts

## Tech stack

- Kotlin, XML layouts, Material 3
- Jetpack Navigation, ViewModel/LiveData, RecyclerView
- Retrofit for the API (Python/Flask backend with MySQL)
- Encrypted SharedPreferences for the session
- JUnit unit tests, GitHub Actions for CI

## Project structure

```
app/src/main/java/com/example/monatechickensteak/
  data/    repositories and session handling
  model/   data classes (menu items, cart, orders, users, reviews)
  ui/      screens (fragments) and adapters
  util/    validators, loyalty rules, helpers
```

## Run the app

1. Clone the repository and open it in Android Studio.
2. Let Gradle sync.
3. Run the `app` configuration on a physical Android phone (USB debugging on) or an emulator.

Demo account (mock data): `demo@monate.co.za` / `Monate@123`

## Run the tests

```
./gradlew testDebugUnitTest
```

On Windows use `gradlew.bat testDebugUnitTest`.

## Continuous integration

Every push and pull request triggers the **Android CI** workflow
(`.github/workflows/android.yml`), which builds the app, runs the unit tests and
uploads the test report and a debug APK. If a build or test fails, the change is
sent back to development for correction.

## Team

Mentor: Lukheli

Tlogelo Molele, Katlego Motsoaledi, Tshimologo Mokiba, Mmakwena Masenya