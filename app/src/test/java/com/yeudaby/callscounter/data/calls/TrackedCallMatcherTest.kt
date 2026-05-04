package com.yeudaby.callscounter.data.calls

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackedCallMatcherTest {

    @Test
    fun `matches international formatting of tracked mobile number`() {
        assertTrue(
            TrackedCallMatcher.matches(
                number = "+972 53-313-1310",
                trackedNumbers = setOf("0533131310"),
            )
        )
    }

    @Test
    fun `does not suffix-match short hotline numbers`() {
        assertFalse(
            TrackedCallMatcher.matches(
                number = "9991230",
                trackedNumbers = setOf("1230"),
            )
        )
    }

    @Test
    fun `builds same comparable key for local and international form`() {
        assertEquals(
            TrackedCallMatcher.comparableKey("0533131310"),
            TrackedCallMatcher.comparableKey("+972 53 313 1310"),
        )
    }
}
