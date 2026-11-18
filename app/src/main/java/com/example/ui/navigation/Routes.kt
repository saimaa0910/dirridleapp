package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashRoute

@Serializable
object OnboardingRoute

@Serializable
object LoginRoute

@Serializable
object AgeEligibilityRoute

@Serializable
data class LocationSetupRoute(val fromSettings: Boolean = false)

@Serializable
object MainTabsRoute

@Serializable
data class PostDetailRoute(val postId: String)

@Serializable
data class CommunityDetailRoute(val communityId: String)

@Serializable
object SettingsRoute

@Serializable
object SafetyCenterRoute
