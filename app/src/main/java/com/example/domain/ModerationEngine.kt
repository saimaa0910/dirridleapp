package com.example.domain

enum class SafetyAction {
    PUBLISH,
    HOLD_FOR_REVIEW,
    REJECT,
}

data class ContentSafetyResult(
    val allowed: Boolean,
    val action: SafetyAction,
    val errorCode: String? = null,
    val detectedPatterns: List<String> = emptyList(),
)

object ModerationEngine {
    private val indianPhoneRegex = Regex("""(?:\+91[\s-]?)?[6-9]\d{9}\b""")
    private val emailRegex = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}""")
    private val aadhaarRegex = Regex("""\b\d{4}[\s-]?\d{4}[\s-]?\d{4}\b""")
    private val panRegex = Regex("""\b[A-Z]{5}\d{4}[A-Z]\b""")
    private val urlRegex = Regex("""https?://\S+""", RegexOption.IGNORE_CASE)
    private val repeatedCharRegex = Regex("""(.)\1{14,}""")
    private val severeKeywords = listOf("doxx", "kill yourself", "kys", "bomb threat", "leaked address")

    fun checkContentSafety(content: String): ContentSafetyResult {
        val detected = mutableListOf<String>()

        if (indianPhoneRegex.containsMatchIn(content)) detected += "PHONE_NUMBER"
        if (emailRegex.containsMatchIn(content)) detected += "EMAIL"
        if (aadhaarRegex.containsMatchIn(content)) detected += "AADHAAR_NUMBER"
        if (panRegex.containsMatchIn(content)) detected += "PAN_NUMBER"

        val lower = content.lowercase()
        if (severeKeywords.any { lower.contains(it) }) {
            detected += "SAFETY_KEYWORD"
        }

        val urlCount = urlRegex.findAll(content).count()
        if (urlCount >= 4 || repeatedCharRegex.containsMatchIn(content)) {
            detected += "SPAM_PATTERN"
        }

        if ("AADHAAR_NUMBER" in detected || "PAN_NUMBER" in detected || "PHONE_NUMBER" in detected || "EMAIL" in detected) {
            return ContentSafetyResult(
                allowed = false,
                action = SafetyAction.REJECT,
                errorCode = "CONTENT_BLOCKED_PII",
                detectedPatterns = detected,
            )
        }

        if ("SAFETY_KEYWORD" in detected) {
            return ContentSafetyResult(
                allowed = false,
                action = SafetyAction.REJECT,
                errorCode = "CONTENT_MODERATION_REJECTED",
                detectedPatterns = detected,
            )
        }

        if (detected.isNotEmpty()) {
            return ContentSafetyResult(
                allowed = true,
                action = SafetyAction.HOLD_FOR_REVIEW,
                detectedPatterns = detected,
            )
        }

        return ContentSafetyResult(
            allowed = true,
            action = SafetyAction.PUBLISH,
        )
    }
}

object UserMessageForCode {
    private val safeMessages = mapOf(
        "UNAUTHORIZED" to "Your session has expired. Please sign in again.",
        "INVALID_OTP" to "That verification code did not match. Please double-check and try again.",
        "OTP_EXPIRED" to "This verification code has expired. Request a new one to continue.",
        "OTP_RATE_LIMITED" to "Too many code requests. Please wait a minute before trying again.",
        "AGE_VERIFICATION_REQUIRED" to "You must confirm you are 18 or older to use diriddle.",
        "ACCOUNT_SUSPENDED" to "This account is temporarily restricted. Visit Safety Center for details.",
        "FORBIDDEN" to "You do not have permission to perform this action.",
        "NOT_FOUND" to "This item could not be found or has already been removed.",
        "COMMUNITY_NOT_FOUND" to "That community is currently unavailable.",
        "POST_NOT_FOUND" to "This post is no longer available.",
        "COMMENTS_DISABLED" to "Comments have been turned off for this post.",
        "CONTENT_BLOCKED_PII" to "For everyone’s privacy, posts cannot include phone numbers, emails, Aadhaar, PAN, or personal identifiers.",
        "CONTENT_MODERATION_REJECTED" to "Your text violates community safety guidelines. Please edit and try again.",
        "RATE_LIMITED" to "You are doing that a bit too fast. Please pause and try again shortly.",
        "DUPLICATE_REPORT" to "You have already reported this item. Our safety team is reviewing it.",
        "CANNOT_BLOCK_SELF" to "You cannot block your own anonymous profile.",
        "VALIDATION_ERROR" to "Please check your input and try again.",
        "INTERNAL_SERVER_ERROR" to "Something went wrong on our side. Please try again in a moment.",
    )

    fun message(code: String?): String {
        val key = code?.trim()?.uppercase()?.replace(Regex("""[\s-]+"""), "_")
        return safeMessages[key] ?: safeMessages.getValue("INTERNAL_SERVER_ERROR")
    }
}
