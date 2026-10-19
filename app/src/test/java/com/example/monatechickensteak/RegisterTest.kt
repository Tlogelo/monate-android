package com.example.monatechickensteak

import com.example.monatechickensteak.data.AuthRepository
import com.example.monatechickensteak.data.AuthResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegisterTest {

    private lateinit var users: FakeUserStore

    @Before
    fun setUp() {
        users = FakeUserStore()
        AuthRepository.store = users
    }

    @Test
    fun newUserCanRegisterThenLogIn() = runTest {
        val reg = AuthRepository.register("Thabi M", "thabi.test@monate.co.za", "Monate@123")
        assertTrue(reg is AuthResult.Success)

        val login = AuthRepository.login("thabi.test@monate.co.za", "Monate@123")
        assertTrue(login is AuthResult.Success)
    }

    @Test
    fun registeredPasswordIsStoredHashedNotPlain() = runTest {
        AuthRepository.register("Thabi M", "hash.check@monate.co.za", "Monate@123")
        val stored = users.findByEmail("hash.check@monate.co.za")!!.passwordHash
        assertNotNull(stored)
        assertTrue(stored != "Monate@123" && !stored!!.contains("Monate@123"))
    }

    @Test
    fun duplicateEmailIsRejectedEvenWithDifferentCase() = runTest {
        AuthRepository.register("First", "demo@monate.co.za", "Monate@123")
        val result = AuthRepository.register("Someone", "DEMO@monate.co.za", "Monate@123")
        assertTrue(result is AuthResult.Error)
        assertEquals(
            "An account with this email already exists",
            (result as AuthResult.Error).message
        )
    }

    @Test
    fun wrongPasswordIsRejected() = runTest {
        AuthRepository.register("First", "demo@monate.co.za", "Monate@123")
        val result = AuthRepository.login("demo@monate.co.za", "WrongPass@1")
        assertTrue(result is AuthResult.Error)
    }

    @Test
    fun unknownEmailGivesTheSameMessageAsWrongPassword() = runTest {
        AuthRepository.register("First", "demo@monate.co.za", "Monate@123")
        val wrong = AuthRepository.login("demo@monate.co.za", "WrongPass@1") as AuthResult.Error
        val unknown = AuthRepository.login("nobody@monate.co.za", "Monate@123") as AuthResult.Error
        assertEquals(wrong.message, unknown.message)
    }
}

class GoogleSignInTest {

    private lateinit var users: FakeUserStore

    @Before
    fun setUp() {
        users = FakeUserStore()
        AuthRepository.store = users
    }

    @Test
    fun firstGoogleSignInCreatesAnAccountWithoutAPassword() = runTest {
        val result = AuthRepository.signInWithGoogle("g.user@gmail.com", "Google User")
        assertTrue(result is AuthResult.Success)
        val record = users.findByEmail("g.user@gmail.com")!!
        assertNull(record.passwordHash)
        assertEquals(AuthRepository.PROVIDER_GOOGLE, record.provider)
        assertEquals("Google User", record.name)
    }

    @Test
    fun secondGoogleSignInReusesTheSameAccount() = runTest {
        val first = AuthRepository.signInWithGoogle("g.user@gmail.com", "Google User") as AuthResult.Success
        val second = AuthRepository.signInWithGoogle("g.user@gmail.com", "Google User") as AuthResult.Success
        assertEquals(first.user.id, second.user.id)
        assertEquals(1, users.users.size)
    }

    @Test
    fun missingGoogleNameFallsBackToTheEmailName() = runTest {
        AuthRepository.signInWithGoogle("no.name@gmail.com", "")
        assertEquals("no.name", users.findByEmail("no.name@gmail.com")!!.name)
    }

    @Test
    fun googleAccountCannotLogInWithAPassword() = runTest {
        AuthRepository.signInWithGoogle("g.user@gmail.com", "Google User")
        val result = AuthRepository.login("g.user@gmail.com", "Monate@123")
        assertTrue(result is AuthResult.Error)
    }

    @Test
    fun blankGoogleEmailIsRejected() = runTest {
        assertTrue(AuthRepository.signInWithGoogle("  ", "Nobody") is AuthResult.Error)
    }
}
