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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.sp
import com.example.domain.TimeUtils
import com.example.ui.components.EmptyStateView
import com.example.ui.components.TopBackBar
import com.example.ui.theme.DiriddleTheme
import com.example.ui.viewmodel.MainUiState

@Composable
fun SettingsScreen(
    state: MainUiState,
    onBack: () -> Unit,
    onTogglePush: (Boolean) -> Unit,
    onOpenCityPicker: () -> Unit,
    onOpenSafetyCenter: () -> Unit,
    onSignOut: () -> Unit,
) {
    val palette = DiriddleTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBackBar(title = "Settings & Privacy", onBack = onBack)

        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            SectionLabel("PREFERENCES")
            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.card,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Push & Activity Alerts",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = "Replies, reactions, and local safety updates",
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.textSecondary,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                    Switch(
                        checked = state.session.pushNotificationsEnabled,
                        onCheckedChange = onTogglePush,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = palette.accent,
                            checkedTrackColor = palette.accentMuted,
                            uncheckedThumbColor = palette.textSecondary,
                            uncheckedTrackColor = palette.border,
                        ),
                        modifier = Modifier.testTag("settings_push_switch"),
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.card,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onOpenCityPicker)
                    .testTag("settings_city_row"),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Primary City",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = state.activeCity.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.textSecondary,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Choose city",
                        tint = palette.textSecondary,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel("TRUST & SAFETY")
            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.card,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onOpenSafetyCenter)
                    .testTag("settings_safety_row"),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Safety Center, Blocks & Account Deletion",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = "Manage blocked accounts, review your reports, or delete your account",
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.textSecondary,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Outlined.VerifiedUser,
                        contentDescription = "Safety Center",
                        tint = palette.accent,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel("SESSION")
            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.dangerMuted,
                border = BorderStroke(1.dp, palette.danger),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onSignOut)
                    .testTag("settings_signout_button"),
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = "Sign Out",
                        tint = palette.danger,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Sign Out",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.danger,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
fun SafetyCenterScreen(
    state: MainUiState,
    onBack: () -> Unit,
    onUnblock: (String) -> Unit,
    onDeleteAccount: () -> Unit,
) {
    val palette = DiriddleTheme.colors
    var showDeleteBox by remember { mutableStateOf(false) }
    var deleteConfirmText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBackBar(title = "Safety Center", onBack = onBack)

        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.card,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Community Guidelines",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.accent,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "• Zero tolerance for doxxing, real-world threats, hate speech, or non-consensual content.\n" +
                            "• Never post personal phone numbers, Aadhaar, emails, or home addresses.\n" +
                            "• Moderators only see anonymized IDs by default; identity lookup is strictly audit-logged for legal safety.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.textSecondary,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel("BLOCKED ACCOUNTS (${state.blockedAccounts.size})")
            Spacer(Modifier.height(8.dp))

            if (state.blockedAccounts.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Outlined.Block,
                    title = "No blocked accounts",
                    message = "When you block an anonymous user on a post, they appear here.",
                )
            } else {
                state.blockedAccounts.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = palette.card,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = item.displayLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = palette.textPrimary,
                                )
                                Text(
                                    text = "Blocked ${TimeUtils.formatRelativeTime(item.createdAtMillis)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.textSecondary,
                                    fontWeight = FontWeight.Normal,
                                )
                            }
                            OutlinedButton(
                                onClick = { onUnblock(item.blockedId) },
                                border = BorderStroke(1.dp, palette.border),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("unblock_${item.blockedId}"),
                            ) {
                                Text("Unblock", color = palette.accent)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel("MY SUBMITTED REPORTS (${state.myReports.size})")
            Spacer(Modifier.height(8.dp))

            if (state.myReports.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "You have not submitted any reports yet.",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textSecondary,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            } else {
                state.myReports.forEach { report ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = palette.card,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${report.targetType} • ${report.reason.label}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = palette.textPrimary,
                                )
                                Text(
                                    text = TimeUtils.formatRelativeTime(report.createdAtMillis),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.textSecondary,
                                    fontWeight = FontWeight.Normal,
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = palette.accentMuted,
                            ) {
                                Text(
                                    text = report.status,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.accent,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel("DANGER ZONE")
            Spacer(Modifier.height(8.dp))

            if (!showDeleteBox) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.dangerMuted,
                    border = BorderStroke(1.dp, palette.danger),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showDeleteBox = true }
                        .testTag("delete_account_trigger"),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 14.dp),
                    ) {
                        Text(
                            text = "Delete My Account",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.danger,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.card,
                    border = BorderStroke(1.dp, palette.danger),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Permanently Delete Account",
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.danger,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "This removes your profile, anonymous identities, and saved posts. Type DELETE to confirm.",
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.textSecondary,
                            fontWeight = FontWeight.Normal,
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = deleteConfirmText,
                            onValueChange = { deleteConfirmText = it.uppercase() },
                            placeholder = { Text("Type DELETE", color = palette.textMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = palette.surface,
                                unfocusedContainerColor = palette.surface,
                                focusedBorderColor = palette.danger,
                                unfocusedBorderColor = palette.border,
                                focusedTextColor = palette.textPrimary,
                                unfocusedTextColor = palette.textPrimary,
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("delete_confirm_input"),
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            OutlinedButton(
                                onClick = {
                                    showDeleteBox = false
                                    deleteConfirmText = ""
                                },
                                border = BorderStroke(1.dp, palette.border),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp),
                            ) {
                                Text("Cancel", color = palette.textSecondary)
                            }
                            Button(
                                onClick = onDeleteAccount,
                                enabled = deleteConfirmText == "DELETE",
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = palette.danger,
                                    contentColor = palette.background,
                                    disabledContainerColor = palette.danger.copy(alpha = 0.4f),
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("confirm_delete_account_button"),
                            ) {
                                Text("Confirm Delete", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    val palette = DiriddleTheme.colors
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = palette.textSecondary,
        letterSpacing = 0.8.sp,
    )
}
