# Firebase login and Google sign-in setup

The app uses **Firebase Authentication** for two things: email and password accounts, and **Continue with Google**
(single sign-on). The Firebase project is created once by the team. It runs on the free **Spark** plan, so no
billing account is needed.

## One-time setup (Firebase console, <https://console.firebase.google.com>)

1. **Create the project** and add an **Android app** with the package name `com.example.monatechickensteak`.
   Use a Google account the whole team can open, because the console is also what you show in the demo video.
2. **Authentication > Sign-in method**: enable **Email/Password**, then **Add new provider > Google**, enable it,
   choose a public name and a support email, and save.
3. **Copy the Web client ID**: open the Google provider again and expand *Web SDK configuration*. Paste the ID into
   `app/src/main/res/values/strings.xml`:

   ```xml
   <string name="google_web_client_id" translatable="false">1234567890-abc.apps.googleusercontent.com</string>
   ```

   The ID goes **between** the tags. Putting it inside `name="..."` makes the build fail with
   "The resource name must start with a letter".
4. **Register the SHA-1 fingerprint** of the key that signs the app: **Project settings > General > Your apps >
   Add fingerprint**. For the debug build run `./gradlew signingReport` (Windows: `.\gradlew signingReport`) and copy
   the SHA-1 of the `debug` variant.
   Every computer has its own debug key (`C:\Users\<name>\.android\debug.keystore`), so add the SHA-1 of every
   computer that will run the app, including the one used to record the demo video.
5. **Download `google-services.json` again** after steps 2 and 4 and put it in the `app` folder. The file must be named
   exactly `google-services.json`. If your browser saves it as `google-services (1).json`, rename it.
6. The file is **committed to the repository**, because the GitHub Actions build fails without it. The API key inside it
   identifies the project and is not a password. If the repository is public, also restrict the key in
   Google Cloud Console under **APIs & Services > Credentials**, using *Android apps* with the package name and SHA-1.
7. Run on a phone that has a Google account signed in.

## How it works in the app

- **Email and password.** `FirebaseCredentialAuth` registers, logs in, changes passwords and deletes accounts through
  Firebase. Firebase stores the passwords as salted hashes; the app keeps no copy. The app's own database keeps the
  customer's profile (name, email, points, orders), matched to the login by email.
- **Google.** `GoogleSignInHelper` asks Android's Credential Manager for a Google ID credential. `AuthRepository`
  hands the ID token to Firebase, which **verifies it online** before the app trusts the email. The first sign-in
  creates a customer profile (`auth_provider = google`); later sign-ins reuse it. Google accounts cannot use the
  password login or "Change password".
- Both kinds of account appear in **Firebase console > Authentication > Users**, with an email or Google icon in the
  *Providers* column. This is the evidence of an online-hosted authentication service for the demo.

## Limits to mention in the report

- Logging in, registering, changing a password and deleting an account need an internet connection.
- Only the login is online. Orders, points, reviews and the menu stay in the SQLite database on the phone, so a
  customer who logs in on a second phone gets their account but not their orders.
- Deleting an account asks Firebase to remove the login first. If Firebase wants a fresh login, the app keeps
  everything and asks the customer to log in again and retry.

## Troubleshooting

| Message or symptom | Cause |
|---|---|
| Build: "File google-services.json is missing" | the file is not in `app/`, or it is named `google-services (1).json` |
| Build: "The resource name must start with a letter" | the Web client ID was pasted inside `name="..."` instead of between the tags |
| "Google sign-in is not set up yet" | `google_web_client_id` still has the placeholder |
| "No Google account found on this phone" | add a Google account in the phone's settings |
| "Google sign-in failed" | wrong Web client ID, the SHA-1 of this computer is not registered (it can take a few minutes to apply), an old `google-services.json`, or no internet |
| "No connection. Check your internet and try again." | the phone cannot reach Firebase |
| "Too many attempts. Please wait a minute and try again." | Firebase is rate-limiting repeated failed logins |
