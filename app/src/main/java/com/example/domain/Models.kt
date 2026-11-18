package com.example.domain

import kotlinx.serialization.Serializable

@Serializable
enum class PostCategory(val label: String, val emoji: String) {
    CONFESSION("Confession", "🤫"),
    STORY("Story", "📖"),
    QUESTION("Question", "❓"),
    GOSSIP("Gossip", "💬"),
    RANT("Rant", "😤"),
    CAMPUS("Campus", "🎓"),
    NEIGHBORHOOD("Neighborhood", "🏘️"),
    LOCAL_ALERT("Local Alert", "🚨"),
    POLL("Poll", "📊"),
}

@Serializable
enum class PostStatus {
    PUBLISHED,
    DRAFT,
    UNDER_REVIEW,
    REMOVED,
    EXPIRED,
}

@Serializable
enum class CommunityType {
    CITY,
    NEIGHBORHOOD,
    CAMPUS,
    SPECIAL,
}

@Serializable
enum class ReactionType(val emoji: String, val label: String) {
    LIKE("👍", "Like"),
    HEART("❤️", "Love"),
    LAUGH("😂", "Haha"),
    WOW("😮", "Wow"),
    SAD("😢", "Sad"),
    ANGRY("😡", "Angry"),
}

@Serializable
enum class ReportReason(val label: String, val description: String) {
    HARASSMENT("Harassment", "Bullying, threats, or targeted abuse"),
    DOXXING("Doxxing / PII", "Exposing real names, phones, or addresses"),
    HATE_SPEECH("Hate Speech", "Attacks based on protected characteristics"),
    SPAM("Spam", "Promotions, scams, or repetitive junk"),
    MISINFORMATION("Misinformation", "Harmful false local alerts or rumors"),
    NSFW("NSFW / Explicit", "Inappropriate sexual or graphic content"),
    IMPERSONATION("Impersonation", "Pretending to be someone else"),
    OTHER("Other", "Another community guideline violation"),
}

@Serializable
enum class NotificationType {
    COMMENT_REPLY,
    REACTION,
    COMMUNITY_POST,
    SYSTEM,
}

@Serializable
enum class Visibility {
    COMMUNITY,
    PUBLIC,
}

enum class FeedTab(val label: String) {
    LATEST("Latest"),
    TRENDING("Trending"),
    NEARBY("Nearby"),
}

@Serializable
data class IndiaCity(
    val name: String,
    val state: String,
    val slug: String,
    val lat: Double,
    val lng: Double,
    val communityId: String,
)

@Serializable
data class PostItem(
    val id: String,
    val anonymousName: String,
    val authorUserId: String,
    val category: PostCategory,
    val content: String,
    val status: PostStatus = PostStatus.PUBLISHED,
    val communityId: String,
    val communityName: String,
    val city: String,
    val commentCount: Int = 0,
    val reactionCount: Int = 0,
    val reactionCounts: Map<ReactionType, Int> = emptyMap(),
    val myReaction: ReactionType? = null,
    val isSaved: Boolean = false,
    val commentsEnabled: Boolean = true,
    val visibility: Visibility = Visibility.COMMUNITY,
    val createdAtMillis: Long,
)

@Serializable
data class CommentReply(
    val id: String,
    val anonymousName: String,
    val content: String,
    val createdAtMillis: Long,
)

@Serializable
data class CommentItem(
    val id: String,
    val postId: String,
    val anonymousName: String,
    val content: String,
    val createdAtMillis: Long,
    val replies: List<CommentReply> = emptyList(),
)

@Serializable
data class CommunityItem(
    val id: String,
    val name: String,
    val slug: String,
    val type: CommunityType,
    val city: String,
    val state: String,
    val memberCount: Int,
    val isJoined: Boolean,
    val description: String,
)

@Serializable
data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val referenceId: String? = null,
    val title: String,
    val body: String,
    val isRead: Boolean = false,
    val createdAtMillis: Long,
)

@Serializable
data class AnonymousIdentityItem(
    val id: String,
    val communityId: String,
    val communityName: String,
    val displayCode: String,
    val createdAtMillis: Long,
)

@Serializable
data class BlockedAccountItem(
    val blockedId: String,
    val displayLabel: String,
    val createdAtMillis: Long,
)

@Serializable
data class MyReportItem(
    val id: String,
    val targetType: String,
    val targetId: String,
    val reason: ReportReason,
    val description: String = "",
    val status: String = "RECEIVED",
    val createdAtMillis: Long,
)

@Serializable
data class AuthSessionState(
    val userId: String? = null,
    val isSignedIn: Boolean = false,
    val hasSeenOnboarding: Boolean = false,
    val ageVerified: Boolean = false,
    val termsAccepted: Boolean = false,
    val pushNotificationsEnabled: Boolean = true,
    val selectedCity: String = "Bengaluru",
    val selectedState: String = "Karnataka",
    val selectedCommunityId: String = "city-bengaluru",
    val locationPermissionGranted: Boolean = false,
)

@Serializable
data class PersistedSocialStore(
    val communities: List<CommunityItem>,
    val posts: List<PostItem>,
    val comments: List<CommentItem>,
    val notifications: List<NotificationItem>,
    val identities: List<AnonymousIdentityItem>,
    val blockedAccounts: List<BlockedAccountItem>,
    val reports: List<MyReportItem>,
)
