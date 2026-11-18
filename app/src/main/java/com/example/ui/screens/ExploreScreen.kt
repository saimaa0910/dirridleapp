package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SeedData
import com.example.domain.CommunityItem
import com.example.domain.IndiaCity
import com.example.domain.LocationCatalog
import com.example.domain.TimeUtils
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.DiriddleTheme
import com.example.ui.viewmodel.MainUiState

@Composable
fun ExploreScreen(
    state: MainUiState,
    onSelectCity: (IndiaCity) -> Unit,
    onToggleJoin: (String) -> Unit,
    onOpenCommunity: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = DiriddleTheme.colors
    var query by remember { mutableStateOf("") }
    var cityFilter by remember { mutableStateOf<String?>(null) }

    val filteredCommunities = remember(state.communities, cityFilter, query) {
        state.communities.filter { c ->
            val cityOk = cityFilter == null || c.city.equals(cityFilter, ignoreCase = true)
            val q = query.trim().lowercase()
            val queryOk = q.isEmpty() ||
                c.name.lowercase().contains(q) ||
                c.city.lowercase().contains(q) ||
                c.type.name.lowercase().contains(q) ||
                c.description.lowercase().contains(q)
            cityOk && queryOk
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(modifier = Modifier.widthIn(max = 680.dp).fillMaxSize()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text(
                    text = "Explore India",
                    style = MaterialTheme.typography.headlineSmall,
                    color = palette.textPrimary,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Browse cities, neighborhoods, campuses, and local buzz.",
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.textSecondary,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            "Search cities, neighborhoods, campuses...",
                            color = palette.textMuted,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = palette.textSecondary,
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Clear search",
                                    tint = palette.textSecondary,
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = palette.surface,
                        unfocusedContainerColor = palette.surface,
                        focusedBorderColor = palette.accent,
                        unfocusedBorderColor = palette.border,
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary,
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_input"),
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
            ) {
                item {
                    Text(
                        text = "TRENDING TOPICS",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textSecondary,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 8.dp),
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SeedData.trendingTopics.forEach { topic ->
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = palette.card,
                                border = BorderStroke(1.dp, palette.border),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .clickable { query = topic.removePrefix("#") }
                                    .testTag("topic_${topic.removePrefix("#")}"),
                            ) {
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = palette.accent,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "MAJOR INDIAN CITIES",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textSecondary,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val allActive = cityFilter == null
                        CityFilterChip(
                            label = "All Cities",
                            active = allActive,
                            onClick = { cityFilter = null },
                        )
                        LocationCatalog.indiaCities.forEach { city ->
                            val active = cityFilter == city.name
                            val suffix = if (state.activeCity.name == city.name) " •" else ""
                            CityFilterChip(
                                label = "${city.name}$suffix",
                                active = active,
                                onClick = {
                                    if (active) {
                                        cityFilter = null
                                    } else {
                                        cityFilter = city.name
                                        onSelectCity(city)
                                    }
                                },
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "COMMUNITIES (${filteredCommunities.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textSecondary,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }

                if (filteredCommunities.isEmpty()) {
                    item {
                        EmptyStateView(
                            icon = Icons.Outlined.Search,
                            title = "No communities found",
                            message = "Try another city or search keyword.",
                        )
                    }
                } else {
                    items(filteredCommunities, key = { it.id }) { community ->
                        CommunityCard(
                            community = community,
                            onOpen = { onOpenCommunity(community.id) },
                            onToggleJoin = { onToggleJoin(community.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CityFilterChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val palette = DiriddleTheme.colors
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (active) palette.accentMuted else palette.surface,
        border = BorderStroke(1.dp, if (active) palette.accent else palette.border),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (active) palette.accent else palette.textSecondary,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
        )
    }
}

@Composable
private fun CommunityCard(
    community: CommunityItem,
    onOpen: () -> Unit,
    onToggleJoin: () -> Unit,
) {
    val palette = DiriddleTheme.colors
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.card,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onOpen)
            .testTag("community_card_${community.id}"),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = community.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary,
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                    ) {
                        Text(
                            text = community.type.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "${community.city}, ${community.state} • ${TimeUtils.formatIndianCount(community.memberCount)} members",
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.textSecondary,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = community.description,
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.textMuted,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (community.isJoined) {
                OutlinedButton(
                    onClick = onToggleJoin,
                    border = BorderStroke(1.dp, palette.border),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier
                        .heightIn(min = 40.dp)
                        .testTag("join_community_${community.id}"),
                ) {
                    Text("Joined", color = palette.textSecondary, style = MaterialTheme.typography.labelLarge)
                }
            } else {
                Button(
                    onClick = onToggleJoin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = palette.background,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier
                        .heightIn(min = 40.dp)
                        .testTag("join_community_${community.id}"),
                ) {
                    Text("Join", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
