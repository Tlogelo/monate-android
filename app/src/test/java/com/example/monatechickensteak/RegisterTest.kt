package com.example.monatechickensteak

import com.example.monatechickensteak.data.AuthRepository
import com.example.monatechickensteak.data.AuthResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterTest {

    @Test
    fun newUserCanRegisterThenLogIn() = runTest {
        val reg = AuthRepository.register("Thabi M", "thabi.test@monate.co.za", "Monate@123")
        assertTrue(reg is AuthResult.Success)

        val login = AuthRepository.login("thabi.test@monate.co.za", "Monate@123")
        assertTrue(login is AuthResult.Success)
    }

    @Test
    fun duplicateEmailIsRejected() = runTest {
        val result = AuthRepository.register("Someone", "demo@monate.co.za", "Monate@123")
        assertTrue(result is AuthResult.Error)
        assertEquals(
            "An account with this email already exists",
            (result as AuthResult.Error).message
        )
    }

    @Test
    fun wrongPasswordIsRejected() = runTest {
        val result = AuthRepository.login("demo@monate.co.za", "WrongPass@1")
        assertTrue(result is AuthResult.Error)
    }
}