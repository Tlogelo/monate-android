package com.example.monatechickensteak

import com.example.monatechickensteak.data.SupportRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SupportRepositoryTest {

    @Test fun shortMessageIsRejected() = assertNotNull(SupportRepository.messageError("help"))

    @Test fun spacesDoNotCountTowardsTheLength() =
        assertNotNull(SupportRepository.messageError("   hi        "))

    @Test fun longEnoughMessageIsAccepted() =
        assertNull(SupportRepository.messageError("My order is late, please help."))

    @Test fun sentMessageIsSavedTrimmed() = runTest {
        val store = FakeSupportStore()
        SupportRepository.store = store
        SupportRepository.send(7, "  Where is my order?  ")
        assertEquals(1, store.countForUser(7))
        assertEquals("Where is my order?", store.messages.first().second)
    }
}
