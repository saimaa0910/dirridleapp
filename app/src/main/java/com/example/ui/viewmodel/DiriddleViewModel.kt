package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DiriddleRepository
import com.example.data.RepoResult
import com.example.domain.AnonymousIdentityItem
import com.example.domain.AuthSessionState
import com.example.domain.BlockedAccountItem
import com.example.domain.CommentItem
import com.example.domain.CommunityItem
import com.example.domain.FeedTab
import com.example.domain.IndiaCity
import com.example.domain.LocationCatalog
import com.example.domain.MyReportItem
import com.example.domain.NotificationItem
import com.example.domain.PostCategory
import com.example.domain.PostItem
import com.example.domain.PostStatus
import com.example.domain.ReactionType
import com.example.domain.ReportReason
import com.example.domain.Visibility
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val isReady: Boolean = false,
    val session: AuthSessionState = AuthSessionState(),
    val activeCity: IndiaCity = LocationCatalog.defaultCity(),
    val feedTab: FeedTab = FeedTab.LATEST,
    val selectedCategory: PostCategory? = null,
    val isRefreshingFeed: Boolean = false,
    val feedPosts: List<PostItem> = emptyList(),
    val allPosts: List<PostItem> = emptyList(),
    val communities: List<CommunityItem> = emptyList(),
    val comments: List<CommentItem> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    val unreadNotificationsCount: Int = 0,
    val identities: List<AnonymousIdentityItem> = emptyList(),
    val myPosts: List<PostItem> = emptyList(),
    val savedPosts: List<PostItem> = emptyList(),
    val blockedAccounts: List<BlockedAccountItem> = emptyList(),
    val myReports: List<MyReportItem> = emptyList(),
    val feedbackBanner: String? = null,
    val errorBanner: String? = null,
)

class DiriddleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DiriddleRepository.getInstance(application)

    private val feedTabFlow = MutableStateFlow(FeedTab.LATEST)
    private val selectedCategoryFlow = MutableStateFlow<PostCategory?>(null)
    private val isRefreshingFlow = MutableStateFlow(false)
    private val feedbackBannerFlow = MutableStateFlow<String?>(null)
    private val errorBannerFlow = MutableStateFlow<String?>(null)

    private data class UiControlState(
        val feedTab: FeedTab,
        val selectedCategory: PostCategory?,
        val isRefreshing: Boolean,
        val feedbackBanner: String?,
        val errorBanner: String?,
    )

    private val controlFlow = combine(
        feedTabFlow,
        selectedCategoryFlow,
        isRefreshingFlow,
        feedbackBannerFlow,
        errorBannerFlow,
    ) { tab, cat, refreshing, feedback, error ->
        UiControlState(tab, cat, refreshing, feedback, error)
    }

    val uiState: StateFlow<MainUiState> = combine(
        repository.authSession,
        repository.socialStore,
        controlFlow,
    ) { session, store, control ->
        val activeCity = LocationCatalog.findByName(session.selectedCity)
        val blockedIds = store.blockedAccounts.map { it.blockedId }.toSet()
        val visiblePosts = store.posts.filter {
            it.status == PostStatus.PUBLISHED && it.authorUserId !in blockedIds
        }

        val cityFiltered = visiblePosts.filter { it.city.equals(activeCity.name, ignoreCase = true) }
            .ifEmpty { visiblePosts }

        val categoryFiltered = if (control.selectedCategory == null) {
            cityFiltered
        } else {
            cityFiltered.filter { it.category == control.selectedCategory }
        }

        val sortedFeed = when (control.feedTab) {
            FeedTab.TRENDING -> categoryFiltered.sortedByDescending { it.reactionCount + it.commentCount }
            FeedTab.NEARBY -> {
                val joinedIds = store.communities.filter { it.isJoined }.map { it.id }.toSet()
                categoryFiltered.sortedWith(
                    compareByDescending<PostItem> { it.communityId in joinedIds }
                        .thenByDescending { it.createdAtMillis },
                )
            }
            FeedTab.LATEST -> categoryFiltered.sortedByDescending { it.createdAtMillis }
        }

        val currentUid = session.userId ?: "demo-user"

        MainUiState(
            isReady = true,
            session = session,
            activeCity = activeCity,
            feedTab = control.feedTab,
            selectedCategory = control.selectedCategory,
            isRefreshingFeed = control.isRefreshing,
            feedPosts = sortedFeed,
            allPosts = visiblePosts,
            communities = store.communities,
            comments = store.comments,
            notifications = store.notifications.sortedByDescending { it.createdAtMillis },
            unreadNotificationsCount = store.notifications.count { !it.isRead },
            identities = store.identities,
            myPosts = store.posts.filter { it.authorUserId == currentUid },
            savedPosts = visiblePosts.filter { it.isSaved },
            blockedAccounts = store.blockedAccounts,
            myReports = store.reports,
            feedbackBanner = control.feedbackBanner,
            errorBanner = control.errorBanner,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainUiState(),
    )

    fun clearBanners() {
        feedbackBannerFlow.value = null
        errorBannerFlow.value = null
    }

    fun showFeedback(message: String) {
        feedbackBannerFlow.value = message
        viewModelScope.launch {
            delay(3500)
            if (feedbackBannerFlow.value == message) {
                feedbackBannerFlow.value = null
            }
        }
    }

    fun setFeedTab(tab: FeedTab) {
        feedTabFlow.value = tab
    }

    fun setSelectedCategory(category: PostCategory?) {
        selectedCategoryFlow.value = category
    }

    fun refreshFeed() {
        viewModelScope.launch {
            isRefreshingFlow.value = true
            delay(450)
            isRefreshingFlow.value = false
        }
    }

    fun markOnboardingSeen(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.markOnboardingSeen()
            onDone()
        }
    }

    fun verifyOtp(otp: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val res = repository.signInWithOtp(otp)) {
                is RepoResult.Success -> onSuccess()
                is RepoResult.Error -> onError(res.message)
            }
        }
    }

    fun quickDemoSignIn(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.quickDemoSignIn()
            onSuccess()
        }
    }

    fun confirmAgeAndTerms(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.confirmAgeAndTerms()
            onSuccess()
        }
    }

    fun setCity(city: IndiaCity, permissionGranted: Boolean? = null, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.setCity(city, permissionGranted)
            onDone()
        }
    }

    fun setPushNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setPushNotificationsEnabled(enabled)
        }
    }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.signOut()
            onDone()
        }
    }

    fun deleteAccount(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteAccount()
            onDone()
        }
    }

    fun toggleReaction(postId: String, reaction: ReactionType) {
        viewModelScope.launch {
            repository.toggleReaction(postId, reaction)
        }
    }

    fun toggleSave(postId: String) {
        viewModelScope.launch {
            repository.toggleSave(postId)
        }
    }

    fun toggleJoinCommunity(communityId: String) {
        viewModelScope.launch {
            repository.toggleJoinCommunity(communityId)
        }
    }

    fun createPost(
        communityId: String,
        category: PostCategory,
        content: String,
        visibility: Visibility,
        commentsEnabled: Boolean,
        onSuccess: (PostItem) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val uid = uiState.value.session.userId ?: "demo-user"
            when (val res = repository.createPost(communityId, category, content, visibility, commentsEnabled, uid)) {
                is RepoResult.Success -> {
                    if (res.data.status == PostStatus.UNDER_REVIEW) {
                        showFeedback("Your post was flagged for moderator review before publishing.")
                    } else {
                        showFeedback("Published anonymously in ${res.data.communityName}.")
                    }
                    onSuccess(res.data)
                }
                is RepoResult.Error -> onError(res.message)
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
            showFeedback("Post removed.")
        }
    }

    fun addComment(
        postId: String,
        content: String,
        replyToCommentId: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val result = if (replyToCommentId != null) {
                repository.addReply(postId, replyToCommentId, content)
            } else {
                repository.addComment(postId, content)
            }
            when (result) {
                is RepoResult.Success -> onSuccess()
                is RepoResult.Error -> onError(result.message)
            }
        }
    }

    fun markNotificationRead(notificationId: String) {
        viewModelScope.launch {
            repository.markNotificationRead(notificationId)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun submitReport(
        targetType: String,
        targetId: String,
        reason: ReportReason,
        description: String,
        onComplete: (String) -> Unit,
    ) {
        viewModelScope.launch {
            when (val res = repository.submitReport(targetType, targetId, reason, description)) {
                is RepoResult.Success -> {
                    showFeedback("Report received. Thank you for keeping your community safe.")
                    onComplete("Report received.")
                }
                is RepoResult.Error -> {
                    onComplete(res.message)
                }
            }
        }
    }

    fun blockAuthor(authorUserId: String, displayLabel: String, onDone: (String) -> Unit) {
        viewModelScope.launch {
            val uid = uiState.value.session.userId
            when (val res = repository.blockAuthor(authorUserId, displayLabel, uid)) {
                is RepoResult.Success -> {
                    val msg = "Blocked $displayLabel. Their posts are now hidden."
                    showFeedback(msg)
                    onDone(msg)
                }
                is RepoResult.Error -> onDone(res.message)
            }
        }
    }

    fun unblockAuthor(blockedId: String) {
        viewModelScope.launch {
            repository.unblockAuthor(blockedId)
            showFeedback("Account unblocked.")
        }
    }
}
