package com.example.monatechickensteak.auth

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

sealed class GoogleSignInResult {
    data class Success(val email: String, val name: String, val idToken: String) : GoogleSignInResult()
    data class Failure(val message: String) : GoogleSignInResult()
}

/**
 * Single sign-on with Google using Android's Credential Manager.
 * Needs a Web client ID from Google Cloud Console, see docs/SSO_SETUP.md.
 */
object GoogleSignInHelper {

    /** False while the placeholder from strings.xml has not been replaced yet. */
    fun isConfigured(clientId: String): Boolean =
        clientId.isNotBlank() && !clientId.startsWith("YOUR_")

    suspend fun signIn(activity: Activity, serverClientId: String): GoogleSignInResult {
        if (!isConfigured(serverClientId)) {
            return GoogleSignInResult.Failure(
                "Google sign-in is not set up yet. Add the Web client ID (see docs/SSO_SETUP.md)."
            )
        }
        return try {
            val option = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()
            val response = CredentialManager.create(activity).getCredential(activity, request)
            val credential = response.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val google = GoogleIdTokenCredential.createFrom(credential.data)
                GoogleSignInResult.Success(google.id, google.displayName ?: "", google.idToken)
            } else {
                GoogleSignInResult.Failure("Google sign-in returned something unexpected. Please try again.")
            }
        } catch (e: GetCredentialCancellationException) {
            GoogleSignInResult.Failure("Sign-in cancelled")
        } catch (e: NoCredentialException) {
            GoogleSignInResult.Failure("No Google account found on this phone. Add one in Settings and try again.")
        } catch (e: GetCredentialException) {
            GoogleSignInResult.Failure("Google sign-in failed. Check your connection and try again.")
        } catch (e: GoogleIdTokenParsingException) {
            GoogleSignInResult.Failure("Could not read the Google account details.")
        }
    }
}
