package com.example.monatechickensteak

import com.example.monatechickensteak.data.AuthRepository
import com.example.monatechickensteak.data.AuthResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileTest {

    @Test
    fun updateNameChangesTheStoredName() = runTest {
        AuthRepository.register("Old Name", "profile.one@monate.co.za", "Monate@123")
        val result = AuthRepository.updateName("profile.one@monate.co.za", "New Name")
        assertTrue(result is AuthResult.Success)
        assertEquals("New Name", (result as AuthResult.Success).user.name)
    }

    @Test
    fun wrongCurrentPasswordIsRejected() = runTest {
        AuthRepository.register("Two", "profile.two@monate.co.za", "Monate@123")
        val result = AuthRepository.changePassword(
            "profile.two@monate.co.za", "Wrong@123", "Newpass@123"
        )
        assertTrue(result is AuthResult.Error)
        assertEquals("Current password is incorrect", (result as AuthResult.Error).message)
    }

    @Test
    fun newPasswordWorksAndOldOneStops() = runTest {
        AuthRepository.register("Three", "profile.three@monate.co.za", "Monate@123")
        val change = AuthRepository.changePassword(
            "profile.three@monate.co.za", "Monate@123", "Newpass@123"
        )
        assertTrue(change is AuthResult.Success)
        assertTrue(AuthRepository.login("profile.three@monate.co.za", "Newpass@123") is AuthResult.Success)
        assertTrue(AuthRepository.login("profile.three@monate.co.za", "Monate@123") is AuthResult.Error)
    }

    @Test
    fun samePasswordIsRejected() = runTest {
        AuthRepository.register("Four", "profile.four@monate.co.za", "Monate@123")
        val result = AuthRepository.changePassword(
            "profile.four@monate.co.za", "Monate@123", "Monate@123"
        )
        assertTrue(result is AuthResult.Error)
    }

    @Test
    fun demoUserHasLoyaltyPoints() {
        assertEquals(120, AuthRepository.loyaltyPoints("demo@monate.co.za"))
    }
}