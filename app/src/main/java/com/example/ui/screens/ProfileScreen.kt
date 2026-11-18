package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.AnonymousIdentityItem
import com.example.domain.ReactionType
import com.example.domain.ReportReason
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PostCard
import com.example.ui.components.ReportBottomSheet
import com.example.ui.theme.DiriddleTheme
import com.example.ui.viewmodel.MainUiState

private enum class ProfileTab {
    IDENTITIES,
    POSTS,
    SAVED,
}

@Composable
fun ProfileScreen(
    state: MainUiState,
    onOpenSafety: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCommunity: (String) -> Unit,
    onOpenPost: (String) -> Unit,
    onNavigateCreate: () -> Unit,
    onReact: (String, ReactionType) -> Unit,
    onSave: (String) -> Unit,
    onSubmitReport: (postId: String, reason: ReportReason, description: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = DiriddleTheme.colors
    var selectedTab by remember { mutableStateOf(ProfileTab.IDENTITIES) }
    var reportPostId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(modifier = Modifier.widthIn(max = 680.dp).fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Your Anonymous Space",
                        style = MaterialTheme.typography.headlineSmall,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Active City: ${state.activeCity.name}",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textSecondary,
                        fontWeight = FontWeight.Normal,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                    ) {
                        IconButton(
                            onClick = onOpenSafety,
                            modifier = Modifier.testTag("profile_safety_button"),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.VerifiedUser,
                                contentDescription = "Safety Center",
                                tint = palette.accent,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                    ) {
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.testTag("profile_settings_button"),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings",
                                tint = palette.textPrimary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
            ) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = palette.card,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = palette.accent,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = "Each community assigns you an isolated anonymous handle. Posts in one community cannot be linked to another.",
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.textSecondary,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            val tabs = listOf(
                                ProfileTab.IDENTITIES to "Identities (${state.identities.size})",
                                ProfileTab.POSTS to "My Posts (${state.myPosts.size})",
                                ProfileTab.SAVED to "Saved (${state.savedPosts.size})",
                            )
                            tabs.forEach { (tab, label) ->
                                val active = selectedTab == tab
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (active) palette.card else palette.surface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedTab = tab }
                                        .testTag("profile_tab_${tab.name.lowercase()}"),
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 9.dp, horizontal = 4.dp),
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (active) palette.accent else palette.textSecondary,
                                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                when (selectedTab) {
                    ProfileTab.IDENTITIES -> {
                        items(state.identities, key = { it.id }) { identity ->
                            IdentityRowCard(
                                identity = identity,
                                onView = { onOpenCommunity(identity.communityId) },
                            )
                        }
                    }
                    ProfileTab.POSTS -> {
                        if (state.myPosts.isEmpty()) {
                            item {
                                EmptyStateView(
                                    icon = Icons.Outlined.Forum,
                                    title = "No posts yet",
                                    message = "Your anonymous posts will appear here.",
                                    actionLabel = "Create Post",
                                    onAction = onNavigateCreate,
                                )
                            }
                        } else {
                            items(state.myPosts, key = { it.id }) { post ->
                                PostCard(
                                    post = post,
                                    onClick = { onOpenPost(post.id) },
                                    onReact = { onReact(post.id, it) },
                                    onSave = { onSave(post.id) },
                                    onReport = { reportPostId = post.id },
                                )
                            }
                        }
                    }
                    ProfileTab.SAVED -> {
                        if (state.savedPosts.isEmpty()) {
                            item {
                                EmptyStateView(
                                    icon = Icons.Outlined.BookmarkBorder,
                                    title = "No saved posts",
                                    message = "Tap the bookmark icon on any post to revisit it later.",
                                )
                            }
                        } else {
                            items(state.savedPosts, key = { it.id }) { post ->
                                PostCard(
                                    post = post,
                                    onClick = { onOpenPost(post.id) },
                                    onReact = { onReact(post.id, it) },
                                    onSave = { onSave(post.id) },
                                    onReport = { reportPostId = post.id },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    reportPostId?.let { targetId ->
        ReportBottomSheet(
            onDismiss = { reportPostId = null },
            onSubmit = { reason, desc ->
                onSubmitReport(targetId, reason, desc)
                reportPostId = null
            },
        )
    }
}

@Composable
private fun IdentityRowCard(
    identity: AnonymousIdentityItem,
    onView: () -> Unit,
) {
    val palette = DiriddleTheme.colors
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.card,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(palette.accentMuted)
                    .border(1.dp, palette.accentBorder, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = identity.displayCode,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                )
                Text(
                    text = "Community: ${identity.communityName}",
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.textSecondary,
                    fontWeight = FontWeight.Normal,
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onView)
                    .testTag("identity_view_${identity.communityId}"),
            ) {
                Text(
                    text = "View",
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.accent,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
    }
}
