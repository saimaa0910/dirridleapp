package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.unit.sp
import com.example.domain.FeedTab
import com.example.domain.PostCategory
import com.example.domain.ReactionType
import com.example.domain.ReportReason
import com.example.ui.components.CategoryChipsRow
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FeedbackBanner
import com.example.ui.components.PostCard
import com.example.ui.components.ReportBottomSheet
import com.example.ui.theme.DiriddleTheme
import com.example.ui.viewmodel.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedScreen(
    state: MainUiState,
    onSelectTab: (FeedTab) -> Unit,
    onSelectCategory: (PostCategory?) -> Unit,
    onRefresh: () -> Unit,
    onOpenCityPicker: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPost: (String) -> Unit,
    onNavigateCreate: () -> Unit,
    onReact: (String, ReactionType) -> Unit,
    onSave: (String) -> Unit,
    onSubmitReport: (postId: String, reason: ReportReason, description: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = DiriddleTheme.colors
    var reportTargetPostId by remember { mutableStateOf<String?>(null) }

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
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "DIRIDDLE",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.accent,
                        letterSpacing = 1.4.sp,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onOpenCityPicker)
                            .padding(vertical = 2.dp, horizontal = 2.dp)
                            .testTag("home_city_picker"),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Selected city",
                            tint = palette.accent,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = state.activeCity.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = palette.textPrimary,
                        )
                        Spacer(Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Change city",
                            tint = palette.textSecondary,
                            modifier = Modifier.size(18.dp),
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
                        modifier = Modifier.testTag("home_settings_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.VerifiedUser,
                            contentDescription = "Open settings and safety",
                            tint = palette.textPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }

            // Segmented Feed Tabs
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    FeedTab.entries.forEach { tab ->
                        val active = state.feedTab == tab
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (active) palette.card else palette.surface,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSelectTab(tab) }
                                .testTag("feed_tab_${tab.name.lowercase()}"),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 9.dp),
                            ) {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (active) palette.accent else palette.textSecondary,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                )
                            }
                        }
                    }
                }
            }

            CategoryChipsRow(
                selected = state.selectedCategory,
                onSelect = onSelectCategory,
            )

            state.feedbackBanner?.let { banner ->
                FeedbackBanner(
                    message = banner,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            PullToRefreshBox(
                isRefreshing = state.isRefreshingFeed,
                onRefresh = onRefresh,
                modifier = Modifier.weight(1f),
            ) {
                if (state.feedPosts.isEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        item {
                            EmptyStateView(
                                icon = Icons.Outlined.Forum,
                                title = "Quiet in ${state.activeCity.name}",
                                message = "No anonymous posts match this filter yet. Start the conversation!",
                                actionLabel = "Create Post",
                                onAction = onNavigateCreate,
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(state.feedPosts, key = { it.id }) { post ->
                            PostCard(
                                post = post,
                                onClick = { onOpenPost(post.id) },
                                onReact = { reaction -> onReact(post.id, reaction) },
                                onSave = { onSave(post.id) },
                                onReport = { reportTargetPostId = post.id },
                            )
                        }
                    }
                }
            }
        }
    }

    reportTargetPostId?.let { targetId ->
        ReportBottomSheet(
            onDismiss = { reportTargetPostId = null },
            onSubmit = { reason, desc ->
                onSubmitReport(targetId, reason, desc)
                reportTargetPostId = null
            },
        )
    }
}
