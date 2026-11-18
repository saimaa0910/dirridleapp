package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.PostCategory
import com.example.domain.PostItem
import com.example.domain.ReactionType
import com.example.domain.ReportReason
import com.example.domain.TimeUtils
import com.example.ui.theme.DiriddleTheme

@Composable
fun TopBackBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit = {},
) {
    val palette = DiriddleTheme.colors
    Surface(
        color = palette.background,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onBack)
                        .minimumInteractiveComponentSize()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("back_button"),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate back",
                        tint = palette.textPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary,
                    )
                }
                trailingContent()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(palette.border),
            )
        }
    }
}

@Composable
fun CategoryChipsRow(
    selected: PostCategory?,
    onSelect: (PostCategory?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = DiriddleTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val allActive = selected == null
        CategoryPill(
            label = "All",
            active = allActive,
            onClick = { onSelect(null) },
            testTag = "category_chip_all",
        )
        PostCategory.entries.forEach { cat ->
            val active = selected == cat
            CategoryPill(
                label = "${cat.emoji} ${cat.label}",
                active = active,
                onClick = { onSelect(if (active) null else cat) },
                testTag = "category_chip_${cat.name.lowercase()}",
            )
        }
    }
}

@Composable
private fun CategoryPill(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    testTag: String,
) {
    val palette = DiriddleTheme.colors
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (active) palette.accentMuted else palette.surface,
        border = BorderStroke(1.dp, if (active) palette.accent else palette.border),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .heightIn(min = 36.dp)
            .testTag(testTag),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (active) palette.accent else palette.textSecondary,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostCard(
    post: PostItem,
    onClick: () -> Unit,
    onReact: (ReactionType) -> Unit,
    onSave: () -> Unit,
    onReport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = DiriddleTheme.colors
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.card,
        border = BorderStroke(1.dp, palette.border),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("post_card_${post.id}"),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(palette.accentMuted)
                            .border(1.dp, palette.accentBorder, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = palette.accent,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = post.anonymousName,
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "${post.communityName} • ${TimeUtils.formatRelativeTime(post.createdAtMillis)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                ) {
                    Text(
                        text = "${post.category.emoji} ${post.category.label}",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.accent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                color = palette.textPrimary,
            )

            Spacer(Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ReactionType.entries.take(4).forEach { reaction ->
                    val isSelected = post.myReaction == reaction
                    val count = post.reactionCounts[reaction] ?: 0
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (isSelected) palette.accentMuted else palette.surface,
                        border = BorderStroke(1.dp, if (isSelected) palette.accent else palette.border),
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { onReact(reaction) }
                            .testTag("react_${post.id}_${reaction.name.lowercase()}"),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(text = reaction.emoji, fontSize = 14.sp)
                            if (count > 0) {
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) palette.accent else palette.textSecondary,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(palette.borderSubtle),
            )
            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onClick)
                        .minimumInteractiveComponentSize()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = palette.textSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${post.commentCount} comments",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textSecondary,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSave,
                        modifier = Modifier.testTag("save_post_${post.id}"),
                    ) {
                        Icon(
                            imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (post.isSaved) "Unsave post" else "Save post",
                            tint = if (post.isSaved) palette.accent else palette.textSecondary,
                            modifier = Modifier.size(19.dp),
                        )
                    }
                    IconButton(
                        onClick = onReport,
                        modifier = Modifier.testTag("report_post_${post.id}"),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = "Report post",
                            tint = palette.textSecondary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
) {
    val palette = DiriddleTheme.colors
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = palette.dangerMuted,
        border = BorderStroke(1.dp, palette.danger),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = "Error",
                tint = palette.danger,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = message,
                style = MaterialTheme.typography.labelLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
fun FeedbackBanner(
    message: String,
    isInfo: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val palette = DiriddleTheme.colors
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isInfo) palette.infoMuted else palette.successMuted,
        border = BorderStroke(1.dp, if (isInfo) palette.info else palette.success),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.labelLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(12.dp),
        )
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val palette = DiriddleTheme.colors
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(palette.accentMuted)
                    .border(1.dp, palette.accentBorder, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier.size(26.dp),
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = palette.textSecondary,
                textAlign = TextAlign.Center,
            )
            if (actionLabel != null && onAction != null) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = palette.background,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                ) {
                    Text(text = actionLabel, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportBottomSheet(
    onDismiss: () -> Unit,
    onSubmit: (ReportReason, String) -> Unit,
) {
    val palette = DiriddleTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedReason by remember { mutableStateOf(ReportReason.HARASSMENT) }
    var description by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.surface,
        contentColor = palette.textPrimary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
        ) {
            Text(
                text = "Report Content",
                style = MaterialTheme.typography.headlineSmall,
                color = palette.textPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Your report is confidential. Our safety team reviews all flags against community guidelines.",
                style = MaterialTheme.typography.labelLarge,
                color = palette.textSecondary,
                fontWeight = FontWeight.Normal,
            )
            Spacer(Modifier.height(14.dp))

            ReportReason.entries.forEach { reason ->
                val active = selectedReason == reason
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (active) palette.accentMuted else palette.card,
                    border = BorderStroke(1.dp, if (active) palette.accent else palette.border),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedReason = reason }
                        .testTag("report_reason_${reason.name.lowercase()}"),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = reason.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (active) palette.accent else palette.textPrimary,
                        )
                        Text(
                            text = reason.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 300) description = it },
                placeholder = {
                    Text(
                        "Optional context for moderators...",
                        color = palette.textMuted,
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = palette.card,
                    unfocusedContainerColor = palette.card,
                    focusedBorderColor = palette.accent,
                    unfocusedBorderColor = palette.border,
                    focusedTextColor = palette.textPrimary,
                    unfocusedTextColor = palette.textPrimary,
                ),
                shape = RoundedCornerShape(12.dp),
                minLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    border = BorderStroke(1.dp, palette.border),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("cancel_report_button"),
                ) {
                    Text("Cancel", color = palette.textSecondary)
                }
                Button(
                    onClick = { onSubmit(selectedReason, description) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = palette.background,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("submit_report_button"),
                ) {
                    Text("Submit Report", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
