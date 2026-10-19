package com.example.monatechickensteak.data

import com.example.monatechickensteak.auth.CredentialAuth
import com.example.monatechickensteak.auth.CredentialResult
import com.example.monatechickensteak.model.Reward
import com.example.monatechickensteak.model.User
import com.example.monatechickensteak.util.PasswordHasher
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Accounts, passwords, Google single sign-on and loyalty points.
 * The rules live here; the database work is done by the [UserStore].
 * When [credentials] is set (Firebase), passwords are checked online and the app keeps no copy.
 */
object AuthRepository {

    const val PROVIDER_PASSWORD = "password"
    const val PROVIDER_GOOGLE = "google"

    private const val WRONG_LOGIN = "Incorrect email or password"
    private const val ACCOUNT_GONE = "Account not found. Please log in again."
    private const val GOOGLE_ACCOUNT = "This account uses Google sign-in. Tap \"Continue with Google\"."
    private const val EMAIL_TAKEN = "An account with this email already exists"

    /** Set once at app start (see Graph) or to a fake store in unit tests. */
    lateinit var store: UserStore

    /** The online login service. Null in unit tests, which then use local password hashes. */
    var credentials: CredentialAuth? = null

    suspend fun login(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        val online = credentials
        if (online != null) return@withContext onlineLogin(online, cleanEmail, password)

        val record = store.findByEmail(cleanEmail)
            ?: return@withContext AuthResult.Error(WRONG_LOGIN)
        val hash = record.passwordHash
            ?: return@withContext AuthResult.Error(GOOGLE_ACCOUNT)
        if (PasswordHasher.verify(password, hash)) {
            AuthResult.Success(record.toUser())
        } else {
            AuthResult.Error(WRONG_LOGIN)
        }
    }

    suspend fun register(name: String, email: String, password: String): AuthResult =
        withContext(Dispatchers.IO) {
            val cleanEmail = email.trim()
            val online = credentials
            if (online != null) return@withContext onlineRegister(online, name.trim(), cleanEmail, password)

            if (store.findByEmail(cleanEmail) != null) {
                return@withContext AuthResult.Error(EMAIL_TAKEN)
            }
            val record = store.insert(
                name = name.trim(),
                email = cleanEmail,
                passwordHash = PasswordHasher.encode(password),
                provider = PROVIDER_PASSWORD
            )
            AuthResult.Success(record.toUser())
        }

    private suspend fun onlineLogin(online: CredentialAuth, email: String, password: String): AuthResult {
        val local = store.findByEmail(email)
        if (local != null && local.provider == PROVIDER_GOOGLE) return AuthResult.Error(GOOGLE_ACCOUNT)
        return when (val result = online.login(email, password)) {
            is CredentialResult.Success -> {
                // The local record keeps the orders and points; create it if this device has none yet
                val name = result.displayName.orEmpty().ifBlank { email.substringBefore('@') }
                val record = local ?: store.insert(name, email, null, PROVIDER_PASSWORD)
                AuthResult.Success(record.toUser())
            }
            is CredentialResult.Failure -> AuthResult.Error(messageFor(result.reason))
        }
    }

    private suspend fun onlineRegister(
        online: CredentialAuth,
        name: String,
        email: String,
        password: String
    ): AuthResult {
        val local = store.findByEmail(email)
        if (local != null && local.provider == PROVIDER_GOOGLE) return AuthResult.Error(EMAIL_TAKEN)
        return when (val result = online.register(name, email, password)) {
            is CredentialResult.Success -> {
                // Reusing an existing local record keeps that customer's orders and points
                val record = local ?: store.insert(name, email, null, PROVIDER_PASSWORD)
                AuthResult.Success(record.toUser())
            }
            is CredentialResult.Failure -> AuthResult.Error(messageFor(result.reason))
        }
    }

    private fun messageFor(reason: CredentialResult.Reason): String = when (reason) {
        CredentialResult.Reason.WRONG_CREDENTIALS -> WRONG_LOGIN
        CredentialResult.Reason.EMAIL_TAKEN -> EMAIL_TAKEN
        CredentialResult.Reason.WEAK_PASSWORD -> "Choose a stronger password"
        CredentialResult.Reason.NETWORK -> "No connection. Check your internet and try again."
        CredentialResult.Reason.TOO_MANY_ATTEMPTS -> "Too many attempts. Please wait a minute and try again."
        CredentialResult.Reason.RECENT_LOGIN_NEEDED -> "Please log in again and retry."
        CredentialResult.Reason.OTHER -> "Something went wrong. Please try again."
    }

    /**
     * Single sign-on with a Google account. The first time, an account is created from the
     * Google name and email (no password is stored). After that the same account is reused.
     */
    suspend fun signInWithGoogle(email: String, name: String, idToken: String? = null): AuthResult =
        withContext(Dispatchers.IO) {
            val cleanEmail = email.trim()
            if (cleanEmail.isEmpty()) return@withContext AuthResult.Error("Google did not return an email address")

            // With Firebase, the Google token is verified online before we trust the email
            val online = credentials
            if (online != null && idToken != null) {
                val check = online.signInWithGoogle(idToken)
                if (check is CredentialResult.Failure) {
                    return@withContext AuthResult.Error(messageFor(check.reason))
                }
            }

            val existing = store.findByEmail(cleanEmail)
            if (existing != null) return@withContext AuthResult.Success(existing.toUser())
            val displayName = name.trim().ifEmpty { cleanEmail.substringBefore('@') }
            val record = store.insert(displayName, cleanEmail, null, PROVIDER_GOOGLE)
            AuthResult.Success(record.toUser())
        }

    suspend fun updateName(userId: Int, newName: String): AuthResult = withContext(Dispatchers.IO) {
        val record = store.findById(userId) ?: return@withContext AuthResult.Error(ACCOUNT_GONE)
        store.updateName(userId, newName.trim())
        AuthResult.Success(record.copy(name = newName.trim()).toUser())
    }

    suspend fun changePassword(userId: Int, currentPassword: String, newPassword: String): AuthResult =
        withContext(Dispatchers.IO) {
            val record = store.findById(userId) ?: return@withContext AuthResult.Error(ACCOUNT_GONE)
            val online = credentials

            if (online != null) {
                if (record.provider == PROVIDER_GOOGLE) {
                    return@withContext AuthResult.Error("Google accounts have no password to change")
                }
                if (currentPassword == newPassword) {
                    return@withContext AuthResult.Error("New password must be different from the current one")
                }
                return@withContext when (val result =
                    online.changePassword(record.email, currentPassword, newPassword)) {
                    is CredentialResult.Success -> AuthResult.Success(record.toUser())
                    is CredentialResult.Failure -> AuthResult.Error(
                        if (result.reason == CredentialResult.Reason.WRONG_CREDENTIALS) {
                            "Current password is incorrect"
                        } else {
                            messageFor(result.reason)
                        }
                    )
                }
            }

            val hash = record.passwordHash
                ?: return@withContext AuthResult.Error("Google accounts have no password to change")
            if (!PasswordHasher.verify(currentPassword, hash)) {
                return@withContext AuthResult.Error("Current password is incorrect")
            }
            if (currentPassword == newPassword) {
                return@withContext AuthResult.Error("New password must be different from the current one")
            }
            store.updatePasswordHash(userId, PasswordHasher.encode(newPassword))
            AuthResult.Success(record.toUser())
        }

    /** Deletes the account together with its orders and redemptions. */
    suspend fun deleteAccount(userId: Int): Boolean = withContext(Dispatchers.IO) {
        val record = store.findById(userId) ?: return@withContext false
        val online = credentials
        if (online != null && record.provider == PROVIDER_PASSWORD) {
            // Firebase may ask for a recent login first; then we keep everything and the screen
            // tells the customer to log in again
            if (online.deleteCurrentUser(record.email) is CredentialResult.Failure) {
                return@withContext false
            }
        }
        store.delete(userId)
        true
    }

    suspend fun loyaltyPoints(userId: Int): Int = withContext(Dispatchers.IO) {
        store.findById(userId)?.loyaltyPoints ?: 0
    }

    suspend fun addLoyaltyPoints(userId: Int, points: Int) {
        if (points <= 0) return
        withContext(Dispatchers.IO) {
            val record = store.findById(userId) ?: return@withContext
            store.setPoints(userId, record.loyaltyPoints + points)
        }
    }

    /** Spends the points and returns the till code, or null when there are not enough points. */
    suspend fun redeem(userId: Int, reward: Reward): String? = withContext(Dispatchers.IO) {
        val record = store.findById(userId) ?: return@withContext null
        if (reward.cost <= 0 || record.loyaltyPoints < reward.cost) return@withContext null
        val code = "MON-" + Random.nextInt(1000, 10000)
        store.setPoints(userId, record.loyaltyPoints - reward.cost)
        store.addRedemption(userId, reward.id, code)
        code
    }
}