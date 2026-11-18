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
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.NotificationItem
import com.example.domain.NotificationType
import com.example.domain.TimeUtils
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.DiriddleTheme
import com.example.ui.viewmodel.MainUiState

private fun iconForNotification(type: NotificationType): ImageVector = when (type) {
    NotificationType.COMMENT_REPLY -> Icons.Outlined.ChatBubbleOutline
    NotificationType.REACTION -> Icons.Outlined.FavoriteBorder
    NotificationType.COMMUNITY_POST -> Icons.Outlined.Groups
    NotificationType.SYSTEM -> Icons.Outlined.VerifiedUser
}

@Composable
fun ActivityScreen(
    state: MainUiState,
    onMarkRead: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onOpenPost: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = DiriddleTheme.colors
    val unreadCount = state.unreadNotificationsCount

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(modifier = Modifier.widthIn(max = 680.dp).fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Activity",
                        style = MaterialTheme.typography.headlineSmall,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = if (unreadCount > 0) "$unreadCount unread updates" else "All caught up",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textSecondary,
                        fontWeight = FontWeight.Normal,
                    )
                }

                if (unreadCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable(onClick = onMarkAllRead)
                            .testTag("mark_all_read_button"),
                    ) {
                        Text(
                            text = "Mark all read",
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.accent,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        )
                    }
                }
            }

            if (state.notifications.isEmpty()) {
                Box(modifier = Modifier.padding(16.dp)) {
                    EmptyStateView(
                        icon = Icons.Outlined.NotificationsOff,
                        title = "No notifications yet",
                        message = "Replies, reactions, and community updates will appear here.",
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    items(state.notifications, key = { it.id }) { item ->
                        NotificationCard(
                            item = item,
                            onClick = {
                                if (!item.isRead) onMarkRead(item.id)
                                item.referenceId?.let(onOpenPost)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    item: NotificationItem,
    onClick: () -> Unit,
) {
    val palette = DiriddleTheme.colors
    val unread = !item.isRead
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (unread) palette.cardElevated else palette.card,
        border = BorderStroke(1.dp, if (unread) palette.accentBorder else palette.border),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("notification_${item.id}"),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (unread) palette.accentMuted else palette.surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = iconForNotification(item.type),
                    contentDescription = null,
                    tint = if (unread) palette.accent else palette.textSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    if (unread) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(palette.accent),
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = item.body,
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.textSecondary,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = TimeUtils.formatRelativeTime(item.createdAtMillis),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}
