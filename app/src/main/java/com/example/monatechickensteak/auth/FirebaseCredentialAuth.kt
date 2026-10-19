package com.example.monatechickensteak.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.google.firebase.auth.GoogleAuthProvider

/** Email and password accounts stored online by Firebase Authentication. */
class FirebaseCredentialAuth : CredentialAuth {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    override suspend fun register(name: String, email: String, password: String): CredentialResult =
        attempt {
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
            if (user != null) {
                try {
                    val change = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    user.updateProfile(change).await()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // The display name is optional online; the app keeps its own copy
                }
            }
            CredentialResult.Success(name)
        }

    override suspend fun login(email: String, password: String): CredentialResult = attempt {
        val user = auth.signInWithEmailAndPassword(email, password).await().user
        CredentialResult.Success(user?.displayName)
    }

    override suspend fun signInWithGoogle(idToken: String): CredentialResult = attempt {
        // Firebase checks the Google token's signature online before it accepts it
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val user = auth.signInWithCredential(credential).await().user
        CredentialResult.Success(user?.displayName)
    }

    override suspend fun changePassword(
        email: String,
        currentPassword: String,
        newPassword: String
    ): CredentialResult = attempt {
        val current = auth.currentUser?.takeIf { it.email.equals(email, ignoreCase = true) }
        if (current != null) {
            // Firebase wants proof of the old password before it allows a change
            current.reauthenticate(EmailAuthProvider.getCredential(email, currentPassword)).await()
            current.updatePassword(newPassword).await()
        } else {
            val signedIn = auth.signInWithEmailAndPassword(email, currentPassword).await().user
            signedIn?.updatePassword(newPassword)?.await()
        }
        CredentialResult.Success()
    }

    override suspend fun deleteCurrentUser(email: String): CredentialResult = attempt {
        val user = auth.currentUser
        if (user != null && user.email.equals(email, ignoreCase = true)) {
            user.delete().await()
        }
        CredentialResult.Success()
    }

    override fun signOut() {
        auth.signOut()
    }

    private suspend fun attempt(block: suspend () -> CredentialResult): CredentialResult =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CredentialResult.Failure(reasonFor(e))
        }

    // Order matters: the weak-password error is a kind of invalid-credentials error
    private fun reasonFor(e: Exception): CredentialResult.Reason = when (e) {
        is FirebaseAuthUserCollisionException -> CredentialResult.Reason.EMAIL_TAKEN
        is FirebaseAuthWeakPasswordException -> CredentialResult.Reason.WEAK_PASSWORD
        is FirebaseAuthInvalidUserException,
        is FirebaseAuthInvalidCredentialsException -> CredentialResult.Reason.WRONG_CREDENTIALS
        is FirebaseAuthRecentLoginRequiredException -> CredentialResult.Reason.RECENT_LOGIN_NEEDED
        is FirebaseNetworkException -> CredentialResult.Reason.NETWORK
        is FirebaseTooManyRequestsException -> CredentialResult.Reason.TOO_MANY_ATTEMPTS
        else -> CredentialResult.Reason.OTHER
    }
}

/** Lets a coroutine wait for a Firebase task without blocking. */
private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resumeWithException(it) }
    addOnCanceledListener { cont.cancel() }
}