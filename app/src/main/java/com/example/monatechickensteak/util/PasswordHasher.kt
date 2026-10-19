package com.example.monatechickensteak.util

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Passwords are never stored as plain text. Each password gets its own random "salt"
 * and is scrambled with PBKDF2-HMAC-SHA256 (120 000 rounds), so two users with the same
 * password end up with different stored values.
 *
 * Stored format:  pbkdf2_sha256$<rounds>$<salt hex>$<hash hex>
 * The rounds are stored with the hash so they can be raised later without breaking old accounts.
 */
object PasswordHasher {

    private const val SCHEME = "pbkdf2_sha256"
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val SEPARATOR = "$"
    const val ITERATIONS = 120_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_BYTES = 16

    fun newSalt(): String {
        val bytes = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    /** Hashes a new password with a fresh salt. Returns the string to store. */
    fun encode(password: String): String = encode(password, newSalt(), ITERATIONS)

    fun encode(password: String, saltHex: String, iterations: Int): String =
        listOf(SCHEME, iterations.toString(), saltHex, derive(password, saltHex, iterations))
            .joinToString(SEPARATOR)

    /** True when [password] matches a value produced by [encode]. */
    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split(SEPARATOR)
        if (parts.size != 4 || parts[0] != SCHEME) return false
        return try {
            val iterations = parts[1].toInt()
            val actual = derive(password, parts[2], iterations)
            // constant-time comparison so timing does not leak how much matched
            MessageDigest.isEqual(actual.toByteArray(), parts[3].toByteArray())
        } catch (e: Exception) {
            false
        }
    }

    private fun derive(password: String, saltHex: String, iterations: Int): String {
        val spec = PBEKeySpec(password.toCharArray(), saltHex.hexToBytes(), iterations, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return factory.generateSecret(spec).encoded.toHex()
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.hexToBytes(): ByteArray =
        ByteArray(length / 2) { substring(it * 2, it * 2 + 2).toInt(16).toByte() }
}
