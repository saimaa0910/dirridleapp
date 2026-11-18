package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.sp
import com.example.domain.CommentItem
import com.example.domain.ReactionType
import com.example.domain.ReportReason
import com.example.domain.TimeUtils
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ErrorBanner
import com.example.ui.components.FeedbackBanner
import com.example.ui.components.PostCard
import com.example.ui.components.ReportBottomSheet
import com.example.ui.components.TopBackBar
import com.example.ui.theme.DiriddleTheme
import com.example.ui.viewmodel.MainUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostDetailScreen(
    postId: String,
    state: MainUiState,
    onBack: () -> Unit,
    onReact: (String, ReactionType) -> Unit,
    onAddComment: (
        postId: String,
        content: String,
        replyToCommentId: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) -> Unit,
    onDeletePost: (String) -> Unit,
    onBlockAuthor: (authorUserId: String, displayLabel: String, onDone: (String) -> Unit) -> Unit,
    onSubmitReport: (targetType: String, targetId: String, reason: ReportReason, description: String) -> Unit,
) {
    val palette = DiriddleTheme.colors
    val post = state.allPosts.find { it.id == postId }
    val comments = state.comments.filter { it.postId == postId }
    val isOwnPost = post != null && post.authorUserId == (state.session.userId ?: "demo-user")

    var commentText by remember { mutableStateOf("") }
    var replyTo by remember { mutableStateOf<Pair<String, String>?>(null) } // commentId to authorName
    var reportTarget by remember { mutableStateOf<Pair<String, String>?>(null) } // type to id
    var actionMsg by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBackBar(
            title = "Thread",
            onBack = onBack,
            trailingContent = {
                if (post != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(end = 12.dp),
                    ) {
                        if (isOwnPost) {
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = palette.surface,
                                border = BorderStroke(1.dp, palette.danger),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .clickable {
                                        onDeletePost(post.id)
                                        onBack()
                                    }
                                    .testTag("delete_post_button"),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteOutline,
                                        contentDescription = "Delete post",
                                        tint = palette.danger,
                                        modifier = Modifier.size(15.dp),
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "Delete",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = palette.danger,
                                    )
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = palette.surface,
                                border = BorderStroke(1.dp, palette.border),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .clickable { reportTarget = "POST" to post.id }
                                    .testTag("detail_report_button"),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Flag,
                                        contentDescription = "Report post",
                                        tint = palette.textSecondary,
                                        modifier = Modifier.size(15.dp),
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "Report",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = palette.textSecondary,
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = palette.surface,
                                border = BorderStroke(1.dp, palette.border),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .clickable {
                                        onBlockAuthor(post.authorUserId, post.anonymousName) { msg ->
                                            actionMsg = msg
                                        }
                                    }
                                    .testTag("detail_block_button"),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Block,
                                        contentDescription = "Block author",
                                        tint = palette.danger,
                                        modifier = Modifier.size(15.dp),
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "Block",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = palette.danger,
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxWidth()
                .weight(1f),
        ) {
            actionMsg?.let { msg ->
                item { FeedbackBanner(message = msg) }
            }
            errorMsg?.let { msg ->
                item { ErrorBanner(message = msg) }
            }

            if (post == null) {
                item {
                    EmptyStateView(
                        icon = Icons.Outlined.Forum,
                        title = "Post unavailable",
                        message = "This post may have been removed or its author was blocked.",
                        actionLabel = "Go Back",
                        onAction = onBack,
                    )
                }
            } else {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.card,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = post.anonymousName,
                                style = MaterialTheme.typography.titleMedium,
                                color = palette.textPrimary,
                            )
                            Text(
                                text = "${post.communityName} • ${TimeUtils.formatRelativeTime(post.createdAtMillis)}",
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.textSecondary,
                                fontWeight = FontWeight.Normal,
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = post.content,
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, lineHeight = 24.sp),
                                color = palette.textPrimary,
                            )
                            Spacer(Modifier.height(14.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                ReactionType.entries.forEach { reaction ->
                                    val selected = post.myReaction == reaction
                                    val count = post.reactionCounts[reaction] ?: 0
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = if (selected) palette.accentMuted else palette.surface,
                                        border = BorderStroke(1.dp, if (selected) palette.accent else palette.border),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(999.dp))
                                            .clickable { onReact(post.id, reaction) }
                                            .testTag("detail_react_${reaction.name.lowercase()}"),
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
                                                    color = if (selected) palette.accent else palette.textSecondary,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "COMMENTS (${comments.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textSecondary,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }

                if (comments.isEmpty()) {
                    item {
                        EmptyStateView(
                            icon = Icons.Outlined.ChatBubbleOutline,
                            title = "No comments yet",
                            message = "Start the anonymous discussion below.",
                        )
                    }
                } else {
                    items(comments, key = { it.id }) { comment ->
                        CommentCard(
                            comment = comment,
                            onReply = { replyTo = comment.id to comment.anonymousName },
                            onReport = { reportTarget = "COMMENT" to comment.id },
                        )
                    }
                }
            }
        }

        if (post != null && post.commentsEnabled) {
            Surface(
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 680.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                ) {
                    replyTo?.let { (_, author) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Replying to $author",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.accent,
                            )
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Cancel reply",
                                tint = palette.textSecondary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { replyTo = null },
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { if (it.length <= 1000) commentText = it },
                            placeholder = {
                                Text(
                                    if (replyTo != null) "Write a reply..." else "Add an anonymous comment...",
                                    color = palette.textMuted,
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = palette.card,
                                unfocusedContainerColor = palette.card,
                                focusedBorderColor = palette.accent,
                                unfocusedBorderColor = palette.border,
                                focusedTextColor = palette.textPrimary,
                                unfocusedTextColor = palette.textPrimary,
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input"),
                        )
                        Surface(
                            shape = CircleShape,
                            color = if (commentText.trim().isNotEmpty()) palette.accent else palette.accent.copy(alpha = 0.45f),
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable(enabled = commentText.trim().isNotEmpty()) {
                                    errorMsg = null
                                    onAddComment(
                                        post.id,
                                        commentText,
                                        replyTo?.first,
                                        {
                                            commentText = ""
                                            replyTo = null
                                        },
                                        { err -> errorMsg = err },
                                    )
                                }
                                .testTag("send_comment_button"),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send comment",
                                    tint = palette.background,
                                    modifier = Modifier.size(19.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    reportTarget?.let { (type, id) ->
        ReportBottomSheet(
            onDismiss = { reportTarget = null },
            onSubmit = { reason, desc ->
                onSubmitReport(type, id, reason, desc)
                actionMsg = "Report submitted to our safety team."
                reportTarget = null
            },
        )
    }
}

@Composable
private fun CommentCard(
    comment: CommentItem,
    onReply: () -> Unit,
    onReport: () -> Unit,
) {
    val palette = DiriddleTheme.colors
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = comment.anonymousName,
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.accent,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = TimeUtils.formatRelativeTime(comment.createdAtMillis),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted,
                    fontWeight = FontWeight.Normal,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyLarge,
                color = palette.textPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Reply",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary,
                    modifier = Modifier
                        .clickable(onClick = onReply)
                        .padding(vertical = 4.dp)
                        .testTag("reply_comment_${comment.id}"),
                )
                Text(
                    text = "Report",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted,
                    modifier = Modifier
                        .clickable(onClick = onReport)
                        .padding(vertical = 4.dp)
                        .testTag("report_comment_${comment.id}"),
                )
            }

            comment.replies.forEach { reply ->
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.padding(start = 12.dp)) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(44.dp)
                            .background(palette.accentBorder),
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = reply.anonymousName,
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.accent,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = TimeUtils.formatRelativeTime(reply.createdAtMillis),
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textMuted,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = reply.content,
                            style = MaterialTheme.typography.bodyLarge,
                            color = palette.textPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityDetailScreen(
    communityId: String,
    state: MainUiState,
    onBack: () -> Unit,
    onToggleJoin: (String) -> Unit,
    onOpenPost: (String) -> Unit,
    onNavigateCreate: () -> Unit,
    onReact: (String, ReactionType) -> Unit,
    onSave: (String) -> Unit,
    onSubmitReport: (postId: String, reason: ReportReason, description: String) -> Unit,
) {
    val palette = DiriddleTheme.colors
    val community = state.communities.find { it.id == communityId }
    val posts = state.allPosts.filter { it.communityId == communityId }
    var reportPostId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBackBar(title = community?.name ?: "Community", onBack = onBack)

        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize(),
        ) {
            if (community != null) {
                Surface(
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = community.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = palette.textPrimary,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "${community.type.name} • ${community.city} • ${TimeUtils.formatIndianCount(community.memberCount)} members",
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.accent,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = community.description,
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.textSecondary,
                                fontWeight = FontWeight.Normal,
                            )
                        }

                        if (community.isJoined) {
                            OutlinedButton(
                                onClick = { onToggleJoin(community.id) },
                                border = BorderStroke(1.dp, palette.border),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.heightIn(min = 42.dp),
                            ) {
                                Text("Joined", color = palette.textSecondary)
                            }
                        } else {
                            Button(
                                onClick = { onToggleJoin(community.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = palette.accent,
                                    contentColor = palette.background,
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.heightIn(min = 42.dp),
                            ) {
                                Text("Join", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (posts.isEmpty()) {
                Box(modifier = Modifier.padding(16.dp)) {
                    EmptyStateView(
                        icon = Icons.Outlined.Forum,
                        title = "No posts here yet",
                        message = "Be the first to share something anonymously in this community.",
                        actionLabel = "Create Post",
                        onAction = onNavigateCreate,
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    items(posts, key = { it.id }) { post ->
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
