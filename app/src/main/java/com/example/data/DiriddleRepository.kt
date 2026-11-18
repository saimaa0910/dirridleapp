package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.AnonymousIdentityItem
import com.example.domain.AuthSessionState
import com.example.domain.BlockedAccountItem
import com.example.domain.CommentItem
import com.example.domain.CommentReply
import com.example.domain.IndiaCity
import com.example.domain.ModerationEngine
import com.example.domain.MyReportItem
import com.example.domain.NotificationItem
import com.example.domain.NotificationType
import com.example.domain.PersistedSocialStore
import com.example.domain.PostCategory
import com.example.domain.PostItem
import com.example.domain.PostStatus
import com.example.domain.ReactionType
import com.example.domain.ReportReason
import com.example.domain.SafetyAction
import com.example.domain.UserMessageForCode
import com.example.domain.Visibility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.random.Random

private val Context.diriddleDataStore: DataStore<Preferences> by preferencesDataStore(name = "diriddle_prefs")

sealed class RepoResult<out T> {
    data class Success<T>(val data: T) : RepoResult<T>()
    data class Error(val code: String, val message: String) : RepoResult<Nothing>()
}

class DiriddleRepository private constructor(private val appContext: Context) {

    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val IS_SIGNED_IN = booleanPreferencesKey("is_signed_in")
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        val AGE_VERIFIED = booleanPreferencesKey("age_verified")
        val TERMS_ACCEPTED = booleanPreferencesKey("terms_accepted")
        val PUSH_ENABLED = booleanPreferencesKey("push_notifications_enabled")
        val SELECTED_CITY = stringPreferencesKey("selected_city")
        val SELECTED_STATE = stringPreferencesKey("selected_state")
        val SELECTED_COMMUNITY_ID = stringPreferencesKey("selected_community_id")
        val LOCATION_GRANTED = booleanPreferencesKey("location_permission_granted")
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    private val storeFile = File(appContext.filesDir, "diriddle_social_store_v1.json")
    private val mutex = Mutex()
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _socialStore = MutableStateFlow(loadOrSeedStore())
    val socialStore: StateFlow<PersistedSocialStore> = _socialStore.asStateFlow()

    val authSession: Flow<AuthSessionState> = appContext.diriddleDataStore.data.map { prefs ->
        AuthSessionState(
            userId = prefs[Keys.USER_ID],
            isSignedIn = prefs[Keys.IS_SIGNED_IN] ?: false,
            hasSeenOnboarding = prefs[Keys.HAS_SEEN_ONBOARDING] ?: false,
            ageVerified = prefs[Keys.AGE_VERIFIED] ?: false,
            termsAccepted = prefs[Keys.TERMS_ACCEPTED] ?: false,
            pushNotificationsEnabled = prefs[Keys.PUSH_ENABLED] ?: true,
            selectedCity = prefs[Keys.SELECTED_CITY] ?: "Bengaluru",
            selectedState = prefs[Keys.SELECTED_STATE] ?: "Karnataka",
            selectedCommunityId = prefs[Keys.SELECTED_COMMUNITY_ID] ?: "city-bengaluru",
            locationPermissionGranted = prefs[Keys.LOCATION_GRANTED] ?: false,
        )
    }

    private fun loadOrSeedStore(): PersistedSocialStore {
        return try {
            if (storeFile.exists()) {
                val raw = storeFile.readText()
                json.decodeFromString<PersistedSocialStore>(raw)
            } else {
                SeedData.createInitialStore().also { saveStoreSync(it) }
            }
        } catch (_: Exception) {
            SeedData.createInitialStore().also { saveStoreSync(it) }
        }
    }

    private fun saveStoreSync(store: PersistedSocialStore) {
        runCatching {
            storeFile.writeText(json.encodeToString(store))
        }
    }

    private suspend fun updateStore(transform: (PersistedSocialStore) -> PersistedSocialStore) {
        mutex.withLock {
            val next = transform(_socialStore.value)
            _socialStore.value = next
            ioScope.launch {
                saveStoreSync(next)
            }
        }
    }

    suspend fun markOnboardingSeen() {
        appContext.diriddleDataStore.edit { prefs ->
            prefs[Keys.HAS_SEEN_ONBOARDING] = true
        }
    }

    suspend fun signInWithOtp(otp: String): RepoResult<String> {
        if (otp.trim() != "123456") {
            return RepoResult.Error("INVALID_OTP", UserMessageForCode.message("INVALID_OTP"))
        }
        val uid = "demo-user"
        appContext.diriddleDataStore.edit { prefs ->
            prefs[Keys.USER_ID] = uid
            prefs[Keys.IS_SIGNED_IN] = true
            prefs[Keys.HAS_SEEN_ONBOARDING] = true
        }
        return RepoResult.Success(uid)
    }

    suspend fun quickDemoSignIn() {
        appContext.diriddleDataStore.edit { prefs ->
            prefs[Keys.USER_ID] = "demo-user"
            prefs[Keys.IS_SIGNED_IN] = true
            prefs[Keys.HAS_SEEN_ONBOARDING] = true
        }
    }

    suspend fun confirmAgeAndTerms() {
        appContext.diriddleDataStore.edit { prefs ->
            prefs[Keys.AGE_VERIFIED] = true
            prefs[Keys.TERMS_ACCEPTED] = true
        }
    }

    suspend fun setCity(city: IndiaCity, permissionGranted: Boolean? = null) {
        appContext.diriddleDataStore.edit { prefs ->
            prefs[Keys.SELECTED_CITY] = city.name
            prefs[Keys.SELECTED_STATE] = city.state
            prefs[Keys.SELECTED_COMMUNITY_ID] = city.communityId
            if (permissionGranted != null) {
                prefs[Keys.LOCATION_GRANTED] = permissionGranted
            }
        }
    }

    suspend fun setPushNotificationsEnabled(enabled: Boolean) {
        appContext.diriddleDataStore.edit { prefs ->
            prefs[Keys.PUSH_ENABLED] = enabled
        }
    }

    suspend fun signOut() {
        appContext.diriddleDataStore.edit { prefs ->
            val seenOnboarding = prefs[Keys.HAS_SEEN_ONBOARDING] ?: true
            prefs.clear()
            prefs[Keys.HAS_SEEN_ONBOARDING] = seenOnboarding
        }
    }

    suspend fun deleteAccount() {
        signOut()
        mutex.withLock {
            val fresh = SeedData.createInitialStore()
            _socialStore.value = fresh
            saveStoreSync(fresh)
        }
    }

    suspend fun createPost(
        communityId: String,
        category: PostCategory,
        content: String,
        visibility: Visibility,
        commentsEnabled: Boolean,
        userId: String,
    ): RepoResult<PostItem> {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) {
            return RepoResult.Error("VALIDATION_ERROR", "Write something before posting.")
        }

        val safety = ModerationEngine.checkContentSafety(trimmed)
        if (!safety.allowed || safety.action == SafetyAction.REJECT) {
            val code = safety.errorCode ?: "CONTENT_MODERATION_REJECTED"
            return RepoResult.Error(code, UserMessageForCode.message(code))
        }

        var createdPost: PostItem? = null
        val now = System.currentTimeMillis()
        updateStore { store ->
            val community = store.communities.find { it.id == communityId }
                ?: return@updateStore store
            val existingIdentity = store.identities.find { it.communityId == communityId }
            val identity = existingIdentity ?: AnonymousIdentityItem(
                id = "id-${now}",
                communityId = community.id,
                communityName = community.name,
                displayCode = "Anonymous #${Random.nextInt(1000, 9999)}",
                createdAtMillis = now,
            )
            val nextIdentities = if (existingIdentity == null) {
                listOf(identity) + store.identities
            } else {
                store.identities
            }

            val postStatus = if (safety.action == SafetyAction.HOLD_FOR_REVIEW) {
                PostStatus.UNDER_REVIEW
            } else {
                PostStatus.PUBLISHED
            }

            val post = PostItem(
                id = "post-$now",
                anonymousName = identity.displayCode,
                authorUserId = userId,
                category = category,
                content = trimmed,
                status = postStatus,
                communityId = community.id,
                communityName = community.name,
                city = community.city,
                commentCount = 0,
                reactionCount = 0,
                reactionCounts = emptyMap(),
                myReaction = null,
                isSaved = false,
                commentsEnabled = commentsEnabled,
                visibility = visibility,
                createdAtMillis = now,
            )
            createdPost = post
            store.copy(
                posts = listOf(post) + store.posts,
                identities = nextIdentities,
            )
        }

        val result = createdPost
            ?: return RepoResult.Error("COMMUNITY_NOT_FOUND", UserMessageForCode.message("COMMUNITY_NOT_FOUND"))
        return RepoResult.Success(result)
    }

    suspend fun deletePost(postId: String) {
        updateStore { store ->
            store.copy(
                posts = store.posts.filterNot { it.id == postId },
                comments = store.comments.filterNot { it.postId == postId },
            )
        }
    }

    suspend fun toggleReaction(postId: String, reaction: ReactionType) {
        updateStore { store ->
            val updatedPosts = store.posts.map { post ->
                if (post.id != postId) return@map post
                val counts = post.reactionCounts.toMutableMap()
                post.myReaction?.let { prev ->
                    val dec = ((counts[prev] ?: 1) - 1).coerceAtLeast(0)
                    if (dec == 0) counts.remove(prev) else counts[prev] = dec
                }
                val nextReaction = if (post.myReaction == reaction) {
                    null
                } else {
                    counts[reaction] = (counts[reaction] ?: 0) + 1
                    reaction
                }
                val total = counts.values.sum()
                post.copy(
                    myReaction = nextReaction,
                    reactionCounts = counts,
                    reactionCount = total,
                )
            }
            store.copy(posts = updatedPosts)
        }
    }

    suspend fun toggleSave(postId: String) {
        updateStore { store ->
            store.copy(
                posts = store.posts.map { post ->
                    if (post.id == postId) post.copy(isSaved = !post.isSaved) else post
                },
            )
        }
    }

    suspend fun addComment(postId: String, content: String): RepoResult<CommentItem> {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) {
            return RepoResult.Error("VALIDATION_ERROR", "Write a comment before sending.")
        }
        val safety = ModerationEngine.checkContentSafety(trimmed)
        if (!safety.allowed || safety.action == SafetyAction.REJECT) {
            val code = safety.errorCode ?: "CONTENT_MODERATION_REJECTED"
            return RepoResult.Error(code, UserMessageForCode.message(code))
        }

        val now = System.currentTimeMillis()
        var created: CommentItem? = null
        updateStore { store ->
            val post = store.posts.find { it.id == postId } ?: return@updateStore store
            if (!post.commentsEnabled) return@updateStore store
            val identity = store.identities.find { it.communityId == post.communityId }
            val displayCode = identity?.displayCode ?: "Anonymous #0317"
            val comment = CommentItem(
                id = "c-$now",
                postId = postId,
                anonymousName = displayCode,
                content = trimmed,
                createdAtMillis = now,
                replies = emptyList(),
            )
            created = comment
            val updatedPosts = store.posts.map {
                if (it.id == postId) it.copy(commentCount = it.commentCount + 1) else it
            }
            val newNotif = if (post.authorUserId == "demo-user") {
                listOf(
                    NotificationItem(
                        id = "notif-$now",
                        type = NotificationType.COMMENT_REPLY,
                        referenceId = postId,
                        title = "New comment on your post",
                        body = "$displayCode commented in ${post.communityName}: \"${trimmed.take(60)}\"",
                        isRead = false,
                        createdAtMillis = now,
                    ),
                )
            } else emptyList()
            store.copy(
                posts = updatedPosts,
                comments = store.comments + comment,
                notifications = newNotif + store.notifications,
            )
        }

        val res = created ?: return RepoResult.Error("COMMENTS_DISABLED", UserMessageForCode.message("COMMENTS_DISABLED"))
        return RepoResult.Success(res)
    }

    suspend fun addReply(postId: String, commentId: String, content: String): RepoResult<CommentReply> {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) {
            return RepoResult.Error("VALIDATION_ERROR", "Write a reply before sending.")
        }
        val safety = ModerationEngine.checkContentSafety(trimmed)
        if (!safety.allowed || safety.action == SafetyAction.REJECT) {
            val code = safety.errorCode ?: "CONTENT_MODERATION_REJECTED"
            return RepoResult.Error(code, UserMessageForCode.message(code))
        }

        val now = System.currentTimeMillis()
        var createdReply: CommentReply? = null
        updateStore { store ->
            val post = store.posts.find { it.id == postId } ?: return@updateStore store
            val identity = store.identities.find { it.communityId == post.communityId }
            val displayCode = identity?.displayCode ?: "Anonymous #0317"
            val reply = CommentReply(
                id = "r-$now",
                anonymousName = displayCode,
                content = trimmed,
                createdAtMillis = now,
            )
            createdReply = reply
            val updatedComments = store.comments.map { comment ->
                if (comment.id == commentId) {
                    comment.copy(replies = comment.replies + reply)
                } else comment
            }
            val updatedPosts = store.posts.map {
                if (it.id == postId) it.copy(commentCount = it.commentCount + 1) else it
            }
            store.copy(posts = updatedPosts, comments = updatedComments)
        }

        val res = createdReply ?: return RepoResult.Error("NOT_FOUND", UserMessageForCode.message("NOT_FOUND"))
        return RepoResult.Success(res)
    }

    suspend fun toggleJoinCommunity(communityId: String) {
        updateStore { store ->
            store.copy(
                communities = store.communities.map { community ->
                    if (community.id != communityId) community
                    else if (community.isJoined) {
                        community.copy(isJoined = false, memberCount = (community.memberCount - 1).coerceAtLeast(0))
                    } else {
                        community.copy(isJoined = true, memberCount = community.memberCount + 1)
                    }
                },
            )
        }
    }

    suspend fun markNotificationRead(notificationId: String) {
        updateStore { store ->
            store.copy(
                notifications = store.notifications.map { n ->
                    if (n.id == notificationId) n.copy(isRead = true) else n
                },
            )
        }
    }

    suspend fun markAllNotificationsRead() {
        updateStore { store ->
            store.copy(
                notifications = store.notifications.map { it.copy(isRead = true) },
            )
        }
    }

    suspend fun submitReport(
        targetType: String,
        targetId: String,
        reason: ReportReason,
        description: String,
    ): RepoResult<MyReportItem> {
        val current = _socialStore.value
        if (current.reports.any { it.targetType == targetType && it.targetId == targetId }) {
            return RepoResult.Error("DUPLICATE_REPORT", UserMessageForCode.message("DUPLICATE_REPORT"))
        }
        val report = MyReportItem(
            id = "rep-${System.currentTimeMillis()}",
            targetType = targetType,
            targetId = targetId,
            reason = reason,
            description = description.trim(),
            status = "RECEIVED",
            createdAtMillis = System.currentTimeMillis(),
        )
        updateStore { store ->
            store.copy(reports = listOf(report) + store.reports)
        }
        return RepoResult.Success(report)
    }

    suspend fun blockAuthor(
        authorUserId: String,
        displayLabel: String,
        currentUserId: String?,
    ): RepoResult<Unit> {
        if (authorUserId == (currentUserId ?: "demo-user")) {
            return RepoResult.Error("CANNOT_BLOCK_SELF", UserMessageForCode.message("CANNOT_BLOCK_SELF"))
        }
        updateStore { store ->
            if (store.blockedAccounts.any { it.blockedId == authorUserId }) return@updateStore store
            val item = BlockedAccountItem(
                blockedId = authorUserId,
                displayLabel = displayLabel,
                createdAtMillis = System.currentTimeMillis(),
            )
            store.copy(blockedAccounts = listOf(item) + store.blockedAccounts)
        }
        return RepoResult.Success(Unit)
    }

    suspend fun unblockAuthor(blockedId: String) {
        updateStore { store ->
            store.copy(
                blockedAccounts = store.blockedAccounts.filterNot { it.blockedId == blockedId },
            )
        }
    }

    companion object {
        @Volatile
        private var instance: DiriddleRepository? = null

        fun getInstance(context: Context): DiriddleRepository {
            return instance ?: synchronized(this) {
                instance ?: DiriddleRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
