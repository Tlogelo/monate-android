package com.example.monatechickensteak

import com.example.monatechickensteak.util.PasswordHasher
import com.example.monatechickensteak.util.Validators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {

    @Test fun blankEmailGivesError() = assertNotNull(Validators.emailError(""))

    @Test fun malformedEmailGivesError() = assertNotNull(Validators.emailError("abc@"))

    @Test fun validEmailPasses() = assertNull(Validators.emailError("demo@monate.co.za"))

    @Test fun shortPasswordRejected() = assertNotNull(Validators.newPasswordError("Ab1@"))

    @Test fun passwordWithoutSpecialCharRejected() =
        assertNotNull(Validators.newPasswordError("Monate1234"))

    @Test fun passwordWithoutUppercaseRejected() =
        assertNotNull(Validators.newPasswordError("monate@123"))

    @Test fun strongPasswordAccepted() = assertNull(Validators.newPasswordError("Monate@123"))

    @Test fun mismatchedConfirmationRejected() =
        assertNotNull(Validators.confirmPasswordError("Monate@123", "Monate@124"))

    @Test fun loginRequiresAPassword() = assertNotNull(Validators.loginPasswordError(""))

    @Test fun shortNameRejected() = assertNotNull(Validators.nameError("A"))
}

class PasswordHasherTest {

    @Test fun correctPasswordVerifies() {
        val stored = PasswordHasher.encode("Monate@123")
        assertTrue(PasswordHasher.verify("Monate@123", stored))
    }

    @Test fun wrongPasswordDoesNotVerify() {
        val stored = PasswordHasher.encode("Monate@123")
        assertFalse(PasswordHasher.verify("Monate@124", stored))
    }

    @Test fun samePasswordGivesDifferentStoredValues() {
        assertNotEquals(PasswordHasher.encode("Monate@123"), PasswordHasher.encode("Monate@123"))
    }

    @Test fun storedValueIsNotThePlainPassword() {
        val stored = PasswordHasher.encode("Monate@123")
        assertFalse(stored.contains("Monate@123"))
        assertTrue(stored.startsWith("pbkdf2_sha256\$${PasswordHasher.ITERATIONS}\$"))
    }

    @Test fun garbageStoredValueIsRejectedNotCrashed() {
        assertFalse(PasswordHasher.verify("Monate@123", "not-a-hash"))
        assertFalse(PasswordHasher.verify("Monate@123", "pbkdf2_sha256\$abc\$zz\$00"))
    }

    @Test fun fixedSaltGivesRepeatableHash() {
        val salt = "00112233445566778899aabbccddeeff"
        assertEquals(
            PasswordHasher.encode("Monate@123", salt, 1000),
            PasswordHasher.encode("Monate@123", salt, 1000)
        )
    }
}
