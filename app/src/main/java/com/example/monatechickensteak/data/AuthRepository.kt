package com.example.monatechickensteak.data

import com.example.monatechickensteak.model.User
import com.example.monatechickensteak.util.PasswordHasher
import kotlinx.coroutines.delay

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * MOCK backend for now. When the Flask API is ready, only this file changes
 * (it will call Retrofit instead of checking a local list).
 */
object AuthRepository {

    private data class StoredUser(val user: User, val salt: String, val passwordHash: String)

    private val users = mutableListOf<StoredUser>()

    init {
        val salt = PasswordHasher.newSalt()
        users.add(
            StoredUser(
                user = User(1, "Demo Customer", "demo@monate.co.za", 120),
                salt = salt,
                passwordHash = PasswordHasher.hash("Monate@123", salt)
            )
        )
    }

    private fun indexOf(email: String): Int =
        users.indexOfFirst { it.user.email.equals(email.trim(), ignoreCase = true) }

    suspend fun login(email: String, password: String): AuthResult {
        delay(700) // pretend we're waiting for the network
        val stored = users.firstOrNull { it.user.email.equals(email.trim(), ignoreCase = true) }
            ?: return AuthResult.Error("Incorrect email or password")
        val attempt = PasswordHasher.hash(password, stored.salt)
        return if (attempt == stored.passwordHash) {
            AuthResult.Success(stored.user)
        } else {
            AuthResult.Error("Incorrect email or password")
        }
    }

    suspend fun register(name: String, email: String, password: String): AuthResult {
        delay(700)
        val cleanEmail = email.trim()
        if (users.any { it.user.email.equals(cleanEmail, ignoreCase = true) }) {
            return AuthResult.Error("An account with this email already exists")
        }
        val salt = PasswordHasher.newSalt()
        val user = User(
            id = (users.maxOfOrNull { it.user.id } ?: 0) + 1,
            name = name.trim(),
            email = cleanEmail,
            loyaltyPoints = 0
        )
        users.add(StoredUser(user, salt, PasswordHasher.hash(password, salt)))
        return AuthResult.Success(user)
    }

    suspend fun updateName(email: String, newName: String): AuthResult {
        delay(400)
        val i = indexOf(email)
        if (i < 0) return AuthResult.Error("Account not found. Please log in again.")
        val updated = users[i].user.copy(name = newName.trim())
        users[i] = users[i].copy(user = updated)
        return AuthResult.Success(updated)
    }

    suspend fun changePassword(email: String, currentPassword: String, newPassword: String): AuthResult {
        delay(500)
        val i = indexOf(email)
        if (i < 0) return AuthResult.Error("Account not found. Please log in again.")
        val stored = users[i]
        if (PasswordHasher.hash(currentPassword, stored.salt) != stored.passwordHash) {
            return AuthResult.Error("Current password is incorrect")
        }
        if (currentPassword == newPassword) {
            return AuthResult.Error("New password must be different from the current one")
        }
        val salt = PasswordHasher.newSalt()
        users[i] = stored.copy(salt = salt, passwordHash = PasswordHasher.hash(newPassword, salt))
        return AuthResult.Success(stored.user)
    }

    fun loyaltyPoints(email: String): Int =
        users.getOrNull(indexOf(email))?.user?.loyaltyPoints ?: 0


    fun addLoyaltyPoints(email: String, points: Int) {
        val i = indexOf(email)
        if (i < 0 || points <= 0) return
        val user = users[i].user
        users[i] = users[i].copy(user = user.copy(loyaltyPoints = user.loyaltyPoints + points))
    }

    /** Returns true when the points were deducted, false when there are not enough. */
    fun redeemPoints(email: String, cost: Int): Boolean {
        val i = indexOf(email)
        if (i < 0) return false
        val user = users[i].user
        if (cost <= 0 || user.loyaltyPoints < cost) return false
        users[i] = users[i].copy(user = user.copy(loyaltyPoints = user.loyaltyPoints - cost))
        return true
    }
}