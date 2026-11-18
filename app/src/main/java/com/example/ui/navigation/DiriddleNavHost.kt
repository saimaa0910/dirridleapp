package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ui.screens.ActivityScreen
import com.example.ui.screens.AgeEligibilityScreen
import com.example.ui.screens.CommunityDetailScreen
import com.example.ui.screens.CreatePostScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeFeedScreen
import com.example.ui.screens.LocationSetupScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PostDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SafetyCenterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.DiriddleTheme
import com.example.ui.viewmodel.DiriddleViewModel

private enum class MainTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    EXPLORE("Explore", Icons.Filled.Explore, Icons.Outlined.Explore),
    CREATE("Create", Icons.Filled.AddCircle, Icons.Filled.AddCircle),
    ACTIVITY("Activity", Icons.Filled.Notifications, Icons.Outlined.Notifications),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person),
}

@Composable
fun DiriddleNavHost(
    viewModel: DiriddleViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
    ) {
        composable<SplashRoute> {
            SplashScreen(
                isReady = uiState.isReady,
                session = uiState.session,
                onNavigate = { target ->
                    val route: Any = when (target) {
                        "onboarding" -> OnboardingRoute
                        "login" -> LoginRoute
                        "age" -> AgeEligibilityRoute
                        else -> MainTabsRoute
                    }
                    navController.navigate(route) {
                        popUpTo<SplashRoute> { inclusive = true }
                    }
                },
            )
        }

        composable<OnboardingRoute> {
            OnboardingScreen(
                onFinish = {
                    viewModel.markOnboardingSeen {
                        navController.navigate(LoginRoute) {
                            popUpTo<OnboardingRoute> { inclusive = true }
                        }
                    }
                },
            )
        }

        composable<LoginRoute> {
            LoginScreen(
                onVerifyOtp = { otp, onError ->
                    viewModel.verifyOtp(
                        otp = otp,
                        onSuccess = {
                            navController.navigate(AgeEligibilityRoute) {
                                popUpTo<LoginRoute> { inclusive = true }
                            }
                        },
                        onError = onError,
                    )
                },
                onQuickDemo = {
                    viewModel.quickDemoSignIn {
                        navController.navigate(AgeEligibilityRoute) {
                            popUpTo<LoginRoute> { inclusive = true }
                        }
                    }
                },
            )
        }

        composable<AgeEligibilityRoute> {
            AgeEligibilityScreen(
                onConfirm = {
                    viewModel.confirmAgeAndTerms {
                        navController.navigate(LocationSetupRoute(fromSettings = false)) {
                            popUpTo<AgeEligibilityRoute> { inclusive = true }
                        }
                    }
                },
            )
        }

        composable<LocationSetupRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<LocationSetupRoute>()
            LocationSetupScreen(
                currentCityName = uiState.activeCity.name,
                fromSettings = route.fromSettings,
                onBack = { navController.popBackStack() },
                onConfirmCity = { city, granted ->
                    viewModel.setCity(city, granted) {
                        if (route.fromSettings) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(MainTabsRoute) {
                                popUpTo<LocationSetupRoute> { inclusive = true }
                            }
                        }
                    }
                },
            )
        }

        composable<MainTabsRoute> {
            MainTabsShell(
                viewModel = viewModel,
                onOpenCityPicker = { navController.navigate(LocationSetupRoute(fromSettings = true)) },
                onOpenSettings = { navController.navigate(SettingsRoute) },
                onOpenSafety = { navController.navigate(SafetyCenterRoute) },
                onOpenPost = { postId -> navController.navigate(PostDetailRoute(postId)) },
                onOpenCommunity = { communityId -> navController.navigate(CommunityDetailRoute(communityId)) },
            )
        }

        composable<PostDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<PostDetailRoute>()
            PostDetailScreen(
                postId = route.postId,
                state = uiState,
                onBack = { navController.popBackStack() },
                onReact = viewModel::toggleReaction,
                onAddComment = viewModel::addComment,
                onDeletePost = viewModel::deletePost,
                onBlockAuthor = viewModel::blockAuthor,
                onSubmitReport = { type, id, reason, desc ->
                    viewModel.submitReport(type, id, reason, desc) {}
                },
            )
        }

        composable<CommunityDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<CommunityDetailRoute>()
            CommunityDetailScreen(
                communityId = route.communityId,
                state = uiState,
                onBack = { navController.popBackStack() },
                onToggleJoin = viewModel::toggleJoinCommunity,
                onOpenPost = { postId -> navController.navigate(PostDetailRoute(postId)) },
                onNavigateCreate = {
                    navController.navigate(MainTabsRoute) {
                        popUpTo<MainTabsRoute> { inclusive = true }
                    }
                },
                onReact = viewModel::toggleReaction,
                onSave = viewModel::toggleSave,
                onSubmitReport = { postId, reason, desc ->
                    viewModel.submitReport("POST", postId, reason, desc) {}
                },
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                state = uiState,
                onBack = { navController.popBackStack() },
                onTogglePush = viewModel::setPushNotificationsEnabled,
                onOpenCityPicker = { navController.navigate(LocationSetupRoute(fromSettings = true)) },
                onOpenSafetyCenter = { navController.navigate(SafetyCenterRoute) },
                onSignOut = {
                    viewModel.signOut {
                        navController.navigate(LoginRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
            )
        }

        composable<SafetyCenterRoute> {
            SafetyCenterScreen(
                state = uiState,
                onBack = { navController.popBackStack() },
                onUnblock = viewModel::unblockAuthor,
                onDeleteAccount = {
                    viewModel.deleteAccount {
                        navController.navigate(LoginRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun MainTabsShell(
    viewModel: DiriddleViewModel,
    onOpenCityPicker: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSafety: () -> Unit,
    onOpenPost: (String) -> Unit,
    onOpenCommunity: (String) -> Unit,
) {
    val palette = DiriddleTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var activeTab by rememberSaveable { mutableStateOf(MainTab.HOME) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            containerColor = palette.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (!useRail) {
                    Box {
                        HorizontalDivider(color = palette.border, thickness = 1.dp)
                        NavigationBar(
                            containerColor = palette.surface,
                            tonalElevation = 0.dp,
                        ) {
                            MainTab.entries.forEach { tab ->
                                val selected = activeTab == tab
                                val isCreate = tab == MainTab.CREATE
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { activeTab = tab },
                                    icon = {
                                        val iconVector = if (selected) tab.selectedIcon else tab.unselectedIcon
                                        val iconTint = when {
                                            isCreate -> palette.accent
                                            selected -> palette.accent
                                            else -> palette.textSecondary
                                        }
                                        if (tab == MainTab.ACTIVITY && uiState.unreadNotificationsCount > 0) {
                                            BadgedBox(
                                                badge = {
                                                    Badge(containerColor = palette.accent, contentColor = palette.background) {
                                                        Text(uiState.unreadNotificationsCount.toString())
                                                    }
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = iconVector,
                                                    contentDescription = tab.label,
                                                    tint = iconTint,
                                                )
                                            }
                                        } else {
                                            Icon(
                                                imageVector = iconVector,
                                                contentDescription = tab.label,
                                                tint = iconTint,
                                                modifier = Modifier.size(if (isCreate) 28.dp else 24.dp),
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = tab.label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = palette.accent,
                                        selectedTextColor = palette.accent,
                                        unselectedIconColor = palette.textSecondary,
                                        unselectedTextColor = palette.textSecondary,
                                        indicatorColor = palette.accentMuted,
                                    ),
                                    modifier = Modifier.testTag("bottom_tab_${tab.name.lowercase()}"),
                                )
                            }
                        }
                    }
                }
            },
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                if (useRail) {
                    NavigationRail(
                        containerColor = palette.surface,
                    ) {
                        MainTab.entries.forEach { tab ->
                            val selected = activeTab == tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { activeTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label,
                                    )
                                },
                                label = { Text(tab.label, fontSize = 11.sp) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = palette.accent,
                                    selectedTextColor = palette.accent,
                                    unselectedIconColor = palette.textSecondary,
                                    unselectedTextColor = palette.textSecondary,
                                    indicatorColor = palette.accentMuted,
                                ),
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (activeTab) {
                        MainTab.HOME -> HomeFeedScreen(
                            state = uiState,
                            onSelectTab = viewModel::setFeedTab,
                            onSelectCategory = viewModel::setSelectedCategory,
                            onRefresh = viewModel::refreshFeed,
                            onOpenCityPicker = onOpenCityPicker,
                            onOpenSettings = onOpenSettings,
                            onOpenPost = onOpenPost,
                            onNavigateCreate = { activeTab = MainTab.CREATE },
                            onReact = viewModel::toggleReaction,
                            onSave = viewModel::toggleSave,
                            onSubmitReport = { postId, reason, desc ->
                                viewModel.submitReport("POST", postId, reason, desc) {}
                            },
                        )

                        MainTab.EXPLORE -> ExploreScreen(
                            state = uiState,
                            onSelectCity = { city -> viewModel.setCity(city) },
                            onToggleJoin = viewModel::toggleJoinCommunity,
                            onOpenCommunity = onOpenCommunity,
                        )

                        MainTab.CREATE -> CreatePostScreen(
                            state = uiState,
                            onPublish = { communityId, category, content, visibility, commentsEnabled, onSuccess, onError ->
                                viewModel.createPost(
                                    communityId = communityId,
                                    category = category,
                                    content = content,
                                    visibility = visibility,
                                    commentsEnabled = commentsEnabled,
                                    onSuccess = { onSuccess() },
                                    onError = onError,
                                )
                            },
                            onPublishedNavigateHome = { activeTab = MainTab.HOME },
                        )

                        MainTab.ACTIVITY -> ActivityScreen(
                            state = uiState,
                            onMarkRead = viewModel::markNotificationRead,
                            onMarkAllRead = viewModel::markAllNotificationsRead,
                            onOpenPost = onOpenPost,
                        )

                        MainTab.PROFILE -> ProfileScreen(
                            state = uiState,
                            onOpenSafety = onOpenSafety,
                            onOpenSettings = onOpenSettings,
                            onOpenCommunity = onOpenCommunity,
                            onOpenPost = onOpenPost,
                            onNavigateCreate = { activeTab = MainTab.CREATE },
                            onReact = viewModel::toggleReaction,
                            onSave = viewModel::toggleSave,
                            onSubmitReport = { postId, reason, desc ->
                                viewModel.submitReport("POST", postId, reason, desc) {}
                            },
                        )
                    }
                }
            }
        }
    }
}
