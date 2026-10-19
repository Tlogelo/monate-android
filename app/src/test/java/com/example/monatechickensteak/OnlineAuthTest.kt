package com.example.monatechickensteak

import com.example.monatechickensteak.auth.CredentialAuth
import com.example.monatechickensteak.auth.CredentialResult
import com.example.monatechickensteak.data.AuthRepository
import com.example.monatechickensteak.data.AuthResult
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Stands in for Firebase so the online login rules can be tested with no phone and no internet. */
class FakeCredentialAuth : CredentialAuth {
    val accounts = mutableMapOf<String, String>() // email (lower case) -> password
    var offline = false
    var deleteFails = false
    var rejectGoogle = false

    override suspend fun register(name: String, email: String, password: String): CredentialResult {
        if (offline) return CredentialResult.Failure(CredentialResult.Reason.NETWORK)
        val key = email.lowercase()
        if (accounts.containsKey(key)) return CredentialResult.Failure(CredentialResult.Reason.EMAIL_TAKEN)
        accounts[key] = password
        return CredentialResult.Success(name)
    }

    override suspend fun login(email: String, password: String): CredentialResult {
        if (offline) return CredentialResult.Failure(CredentialResult.Reason.NETWORK)
        return if (accounts[email.lowercase()] == password) {
            CredentialResult.Success(null)
        } else {
            CredentialResult.Failure(CredentialResult.Reason.WRONG_CREDENTIALS)
        }
    }

    override suspend fun changePassword(
        email: String,
        currentPassword: String,
        newPassword: String
    ): CredentialResult {
        val key = email.lowercase()
        if (accounts[key] != currentPassword) {
            return CredentialResult.Failure(CredentialResult.Reason.WRONG_CREDENTIALS)
        }
        accounts[key] = newPassword
        return CredentialResult.Success()
    }

    override suspend fun deleteCurrentUser(email: String): CredentialResult {
        if (deleteFails) return CredentialResult.Failure(CredentialResult.Reason.RECENT_LOGIN_NEEDED)
        accounts.remove(email.lowercase())
        return CredentialResult.Success()
    }

    override suspend fun signInWithGoogle(idToken: String): CredentialResult =
        if (rejectGoogle) {
            CredentialResult.Failure(CredentialResult.Reason.WRONG_CREDENTIALS)
        } else {
            CredentialResult.Success(null)
        }

    override fun signOut() {}
}

class OnlineAuthTest {

    private lateinit var users: FakeUserStore
    private lateinit var online: FakeCredentialAuth

    @Before
    fun setUp() {
        users = FakeUserStore()
        online = FakeCredentialAuth()
        AuthRepository.store = users
        AuthRepository.credentials = online
    }

    @After
    fun tearDown() {
        // Other tests use the local passwords, so switch the online service off again
        AuthRepository.credentials = null
    }

    private suspend fun newUser(email: String): Int =
        (AuthRepository.register("Thabi", email, "Monate@123") as AuthResult.Success).user.id

    @Test
    fun registerCreatesTheOnlineAccountAndAPasswordlessLocalRecord() = runTest {
        newUser("online.one@monate.co.za")
        assertTrue(online.accounts.containsKey("online.one@monate.co.za"))
        val local = users.findByEmail("online.one@monate.co.za")
        assertNotNull(local)
        assertNull(local!!.passwordHash) // the app keeps no copy of the password
        assertEquals(AuthRepository.PROVIDER_PASSWORD, local.provider)
    }

    @Test
    fun loginWorksWithTheOnlinePasswordAndFailsWithAWrongOne() = runTest {
        newUser("online.two@monate.co.za")
        assertTrue(AuthRepository.login("online.two@monate.co.za", "Monate@123") is AuthResult.Success)
        assertTrue(AuthRepository.login("online.two@monate.co.za", "Wrong@123") is AuthResult.Error)
    }

    @Test
    fun accountMadeElsewhereCanLogInAndGetsALocalRecord() = runTest {
        online.accounts["elsewhere@monate.co.za"] = "Monate@123"
        val result = AuthRepository.login("elsewhere@monate.co.za", "Monate@123") as AuthResult.Success
        assertEquals("elsewhere", result.user.name)
        assertEquals(1, users.users.size)
    }

    @Test
    fun duplicateEmailIsRejectedOnline() = runTest {
        newUser("online.three@monate.co.za")
        val result = AuthRepository.register("Other", "ONLINE.THREE@monate.co.za", "Monate@123")
        assertTrue(result is AuthResult.Error)
        assertEquals(
            "An account with this email already exists",
            (result as AuthResult.Error).message
        )
    }

    @Test
    fun noConnectionGivesAClearMessage() = runTest {
        online.offline = true
        val result = AuthRepository.login("any@monate.co.za", "Monate@123") as AuthResult.Error
        assertTrue(result.message.contains("connection"))
    }

    @Test
    fun afterChangingThePasswordOnlyTheNewOneWorks() = runTest {
        val id = newUser("online.four@monate.co.za")
        assertTrue(AuthRepository.changePassword(id, "Monate@123", "Newpass@123") is AuthResult.Success)
        assertTrue(AuthRepository.login("online.four@monate.co.za", "Newpass@123") is AuthResult.Success)
        assertTrue(AuthRepository.login("online.four@monate.co.za", "Monate@123") is AuthResult.Error)
    }

    @Test
    fun wrongCurrentPasswordIsReported() = runTest {
        val id = newUser("online.five@monate.co.za")
        val result = AuthRepository.changePassword(id, "Wrong@123", "Newpass@123") as AuthResult.Error
        assertEquals("Current password is incorrect", result.message)
    }

    @Test
    fun deleteRemovesTheOnlineAndLocalAccount() = runTest {
        val id = newUser("online.six@monate.co.za")
        assertTrue(AuthRepository.deleteAccount(id))
        assertFalse(online.accounts.containsKey("online.six@monate.co.za"))
        assertNull(users.findById(id))
    }

    @Test
    fun deleteKeepsEverythingWhenFirebaseWantsARecentLogin() = runTest {
        val id = newUser("online.seven@monate.co.za")
        online.deleteFails = true
        assertFalse(AuthRepository.deleteAccount(id))
        assertNotNull(users.findById(id))
    }

    @Test
    fun googleAccountsKeepTheirOwnMessage() = runTest {
        AuthRepository.signInWithGoogle("g.online@gmail.com", "G Online")
        val result = AuthRepository.login("g.online@gmail.com", "Monate@123") as AuthResult.Error
        assertTrue(result.message.contains("Google"))
    }

    @Test
    fun googleSignInIsCheckedOnlineAndCreatesAGoogleAccount() = runTest {
        val result = AuthRepository.signInWithGoogle("g.verified@gmail.com", "G Verified", "good-token")
        assertTrue(result is AuthResult.Success)
        assertEquals(
            AuthRepository.PROVIDER_GOOGLE,
            users.findByEmail("g.verified@gmail.com")!!.provider
        )
    }

    @Test
    fun googleSignInIsRefusedWhenTheTokenIsRejected() = runTest {
        online.rejectGoogle = true
        val result = AuthRepository.signInWithGoogle("g.fake@gmail.com", "Fake", "bad-token")
        assertTrue(result is AuthResult.Error)
        assertNull(users.findByEmail("g.fake@gmail.com"))
    }

}