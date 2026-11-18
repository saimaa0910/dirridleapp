package com.example

import com.example.domain.LocationCatalog
import com.example.domain.ModerationEngine
import com.example.domain.SafetyAction
import com.example.domain.TimeUtils
import com.example.domain.UserMessageForCode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModerationAndLocationTest {

    @Test
    fun moderationRejectsIndianPhoneAndAadhaar() {
        val phoneRes = ModerationEngine.checkContentSafety("Call me at +91 9876543210")
        assertFalse(phoneRes.allowed)
        assertEquals(SafetyAction.REJECT, phoneRes.action)
        assertEquals("CONTENT_BLOCKED_PII", phoneRes.errorCode)

        val aadhaarRes = ModerationEngine.checkContentSafety("My ID is 1234 5678 9012")
        assertFalse(aadhaarRes.allowed)
        assertEquals(SafetyAction.REJECT, aadhaarRes.action)
    }

    @Test
    fun moderationAllowsCleanLocalPost() {
        val clean = ModerationEngine.checkContentSafety("Cubbon Park morning walk was peaceful today!")
        assertTrue(clean.allowed)
        assertEquals(SafetyAction.PUBLISH, clean.action)
    }

    @Test
    fun nearestCityResolvesBengaluruAndMumbai() {
        assertEquals("Bengaluru", LocationCatalog.findNearestCity(12.97, 77.59).name)
        assertEquals("Mumbai", LocationCatalog.findNearestCity(19.07, 72.87).name)
    }

    @Test
    fun indianGroupingAndErrorCopyWork() {
        assertEquals("14,820", TimeUtils.formatIndianCount(14820))
        assertEquals(
            "This post is no longer available.",
            UserMessageForCode.message("POST_NOT_FOUND"),
        )
    }
}
