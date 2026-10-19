package com.example.monatechickensteak.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.monatechickensteak.model.User

/** Remembers who is logged in and the customer's settings, in encrypted storage on the phone. */
object SessionManager {

    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_NAME = "user_name"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_PROVIDER = "user_provider"
    private const val KEY_NOTIFICATIONS = "notifications_enabled"
    private const val KEY_DEFAULT_TIME = "default_collection_time"
    private const val KEY_DEFAULT_PAYMENT = "default_payment"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs != null) return
        val app = context.applicationContext
        prefs = try {
            val masterKey = MasterKey.Builder(app)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                app,
                "monate_secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Some phones cannot create the encrypted file; fall back so the app still works
            app.getSharedPreferences("monate_prefs", Context.MODE_PRIVATE)
        }
    }

    fun saveSession(user: User) {
        prefs?.edit()
            ?.putBoolean(KEY_LOGGED_IN, true)
            ?.putInt(KEY_USER_ID, user.id)
            ?.putString(KEY_NAME, user.name)
            ?.putString(KEY_EMAIL, user.email)
            ?.putString(KEY_PROVIDER, user.provider)
            ?.apply()
    }

    fun isLoggedIn(): Boolean =
        (prefs?.getBoolean(KEY_LOGGED_IN, false) ?: false) && userId() > 0

    fun userId(): Int = prefs?.getInt(KEY_USER_ID, 0) ?: 0

    fun userName(): String = prefs?.getString(KEY_NAME, "") ?: ""

    fun userEmail(): String = prefs?.getString(KEY_EMAIL, "") ?: ""

    fun provider(): String = prefs?.getString(KEY_PROVIDER, AuthRepository.PROVIDER_PASSWORD)
        ?: AuthRepository.PROVIDER_PASSWORD

    fun notificationsEnabled(): Boolean = prefs?.getBoolean(KEY_NOTIFICATIONS, true) ?: true

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs?.edit()?.putBoolean(KEY_NOTIFICATIONS, enabled)?.apply()
    }

    fun defaultCollectionTime(): String? = prefs?.getString(KEY_DEFAULT_TIME, null)

    fun setDefaultCollectionTime(value: String) {
        prefs?.edit()?.putString(KEY_DEFAULT_TIME, value)?.apply()
    }

    fun defaultPayment(): String? = prefs?.getString(KEY_DEFAULT_PAYMENT, null)

    fun setDefaultPayment(value: String) {
        prefs?.edit()?.putString(KEY_DEFAULT_PAYMENT, value)?.apply()
    }

    fun logout() {
        AuthRepository.credentials?.signOut()

        val notifications = notificationsEnabled()
        val time = defaultCollectionTime()
        val payment = defaultPayment()
        val editor = prefs?.edit() ?: return
        editor.clear().putBoolean(KEY_NOTIFICATIONS, notifications)
        if (time != null) editor.putString(KEY_DEFAULT_TIME, time)
        if (payment != null) editor.putString(KEY_DEFAULT_PAYMENT, payment)
        editor.apply()
    }
}
