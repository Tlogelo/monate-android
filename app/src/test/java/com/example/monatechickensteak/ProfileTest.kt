package com.example.monatechickensteak

import com.example.monatechickensteak.data.AuthRepository
import com.example.monatechickensteak.data.AuthResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileTest {

    private lateinit var users: FakeUserStore

    @Before
    fun setUp() {
        users = FakeUserStore()
        AuthRepository.store = users
    }

    private suspend fun newUser(name: String, email: String): Int =
        (AuthRepository.register(name, email, "Monate@123") as AuthResult.Success).user.id

    @Test
    fun updateNameChangesTheStoredName() = runTest {
        val id = newUser("Old Name", "profile.one@monate.co.za")
        val result = AuthRepository.updateName(id, "New Name")
        assertTrue(result is AuthResult.Success)
        assertEquals("New Name", (result as AuthResult.Success).user.name)
        assertEquals("New Name", users.findById(id)!!.name)
    }

    @Test
    fun wrongCurrentPasswordIsRejected() = runTest {
        val id = newUser("Two", "profile.two@monate.co.za")
        val result = AuthRepository.changePassword(id, "Wrong@123", "Newpass@123")
        assertTrue(result is AuthResult.Error)
        assertEquals("Current password is incorrect", (result as AuthResult.Error).message)
    }

    @Test
    fun newPasswordWorksAndOldOneStops() = runTest {
        val id = newUser("Three", "profile.three@monate.co.za")
        val change = AuthRepository.changePassword(id, "Monate@123", "Newpass@123")
        assertTrue(change is AuthResult.Success)
        assertTrue(AuthRepository.login("profile.three@monate.co.za", "Newpass@123") is AuthResult.Success)
        assertTrue(AuthRepository.login("profile.three@monate.co.za", "Monate@123") is AuthResult.Error)
    }

    @Test
    fun samePasswordIsRejected() = runTest {
        val id = newUser("Four", "profile.four@monate.co.za")
        val result = AuthRepository.changePassword(id, "Monate@123", "Monate@123")
        assertTrue(result is AuthResult.Error)
    }

    @Test
    fun googleAccountCannotChangePassword() = runTest {
        val google = AuthRepository.signInWithGoogle("g@gmail.com", "G") as AuthResult.Success
        assertTrue(AuthRepository.changePassword(google.user.id, "x", "Newpass@123") is AuthResult.Error)
    }

    @Test
    fun deletingAnAccountRemovesIt() = runTest {
        val id = newUser("Five", "profile.five@monate.co.za")
        assertTrue(AuthRepository.deleteAccount(id))
        assertNull(users.findById(id))
        assertTrue(AuthRepository.login("profile.five@monate.co.za", "Monate@123") is AuthResult.Error)
    }

    @Test
    fun deletingAnUnknownAccountReturnsFalse() = runTest {
        assertFalse(AuthRepository.deleteAccount(999))
    }
}
