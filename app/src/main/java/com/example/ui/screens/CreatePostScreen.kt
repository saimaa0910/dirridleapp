package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.domain.PostCategory
import com.example.domain.Visibility
import com.example.ui.components.ErrorBanner
import com.example.ui.theme.DiriddleTheme
import com.example.ui.viewmodel.MainUiState

private const val MAX_POST_CHARS = 2000

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatePostScreen(
    state: MainUiState,
    onPublish: (
        communityId: String,
        category: PostCategory,
        content: String,
        visibility: Visibility,
        commentsEnabled: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) -> Unit,
    onPublishedNavigateHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = DiriddleTheme.colors
    var category by remember { mutableStateOf(PostCategory.CONFESSION) }
    var communityId by remember(state.activeCity.communityId) { mutableStateOf(state.activeCity.communityId) }
    var content by remember { mutableStateOf("") }
    var commentsEnabled by remember { mutableStateOf(true) }
    var visibility by remember { mutableStateOf(Visibility.COMMUNITY) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .imePadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = "Post Anonymously",
                style = MaterialTheme.typography.headlineSmall,
                color = palette.textPrimary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Posting as a community-scoped anonymous identity. Your profile remains completely hidden.",
                style = MaterialTheme.typography.labelLarge,
                color = palette.textSecondary,
                fontWeight = FontWeight.Normal,
            )
            Spacer(Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.accentMuted,
                border = BorderStroke(1.dp, palette.accentBorder),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = palette.accent,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Never share real names, phone numbers, addresses, or targeted harassment. Automated PII checks apply.",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }

            errorMsg?.let {
                Spacer(Modifier.height(12.dp))
                ErrorBanner(message = it)
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Category (9 available)",
                style = MaterialTheme.typography.labelLarge,
                color = palette.textSecondary,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PostCategory.entries.forEach { cat ->
                    val active = category == cat
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (active) palette.accentMuted else palette.surface,
                        border = BorderStroke(1.dp, if (active) palette.accent else palette.border),
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { category = cat }
                            .testTag("create_category_${cat.name.lowercase()}"),
                    ) {
                        Text(
                            text = "${cat.emoji} ${cat.label}",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (active) palette.accent else palette.textSecondary,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Community",
                style = MaterialTheme.typography.labelLarge,
                color = palette.textSecondary,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.communities.forEach { comm ->
                    val active = communityId == comm.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (active) palette.accentMuted else palette.card,
                        border = BorderStroke(1.dp, if (active) palette.accent else palette.border),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { communityId = comm.id }
                            .testTag("create_community_${comm.id}"),
                    ) {
                        Text(
                            text = comm.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (active) palette.accent else palette.textSecondary,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Your Message",
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.textSecondary,
                )
                Text(
                    text = "${content.length}/$MAX_POST_CHARS",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted,
                )
            }
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { if (it.length <= MAX_POST_CHARS) content = it },
                placeholder = {
                    Text(
                        "What is happening in your city or neighborhood?",
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
                shape = RoundedCornerShape(16.dp),
                minLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_post_content_input"),
            )

            Spacer(Modifier.height(14.dp))

            // Allow comments card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Allow Comments",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = "Let others reply to your anonymous thread",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                    Switch(
                        checked = commentsEnabled,
                        onCheckedChange = { commentsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = palette.accent,
                            checkedTrackColor = palette.accentMuted,
                            uncheckedThumbColor = palette.textSecondary,
                            uncheckedTrackColor = palette.border,
                        ),
                        modifier = Modifier.testTag("create_allow_comments_switch"),
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Visibility toggle card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Visibility",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = if (visibility == Visibility.COMMUNITY) {
                                "Community members only"
                            } else {
                                "Visible in broader city discovery"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = palette.card,
                        border = BorderStroke(1.dp, palette.accentBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable {
                                visibility = if (visibility == Visibility.COMMUNITY) {
                                    Visibility.PUBLIC
                                } else {
                                    Visibility.COMMUNITY
                                }
                            }
                            .testTag("create_visibility_toggle"),
                    ) {
                        Text(
                            text = visibility.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.accent,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    errorMsg = null
                    onPublish(
                        communityId,
                        category,
                        content,
                        visibility,
                        commentsEnabled,
                        {
                            content = ""
                            onPublishedNavigateHome()
                        },
                        { err -> errorMsg = err },
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.accent,
                    contentColor = palette.background,
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("publish_post_button"),
            ) {
                Text(
                    text = "Publish Anonymously",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}
