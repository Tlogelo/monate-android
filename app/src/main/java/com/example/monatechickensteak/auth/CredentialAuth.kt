package com.example.monatechickensteak.auth

sealed class CredentialResult {
    data class Success(val displayName: String? = null) : CredentialResult()
    data class Failure(val reason: Reason) : CredentialResult()

    enum class Reason {
        WRONG_CREDENTIALS,
        EMAIL_TAKEN,
        WEAK_PASSWORD,
        NETWORK,
        TOO_MANY_ATTEMPTS,
        RECENT_LOGIN_NEEDED,
        OTHER
    }
}

/**
 * The online login service. The real one is Firebase (FirebaseCredentialAuth);
 * unit tests use a fake so no phone or internet is needed.
 */
interface CredentialAuth {
    suspend fun register(name: String, email: String, password: String): CredentialResult
    suspend fun login(email: String, password: String): CredentialResult
    suspend fun signInWithGoogle(idToken: String): CredentialResult
    suspend fun changePassword(email: String, currentPassword: String, newPassword: String): CredentialResult
    suspend fun deleteCurrentUser(email: String): CredentialResult
    fun signOut()
}