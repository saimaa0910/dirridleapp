# diriddle — Anonymous Local Social Network (Android)

`diriddle` is an anonymous, hyperlocal social network built for Indian cities, neighborhoods, and campuses in Kotlin and Jetpack Compose.

## Features

- **Onboarding & 18+ Safety Gate**: Splash, 3-slide privacy onboarding, Phone (+91) / Email OTP sign-in (with local demo OTP `123456` and quick demo login), mandatory 18+ & community rules confirmation, and Indian city selection with optional coarse location detection.
- **Hyperlocal Feeds (`HomeFeedScreen`)**: City-scoped feed with `Latest`, `Trending`, and `Nearby` sorting, pull-to-refresh, and 9 post categories (`Confession`, `Story`, `Question`, `Gossip`, `Rant`, `Campus`, `Neighborhood`, `Local Alert`, `Poll`).
- **Explore India (`ExploreScreen` & `CommunityDetailScreen`)**: Search communities, tap trending topics, filter by major Indian cities, join/leave city, neighborhood, and campus communities, and browse community feeds.
- **Anonymous Posting & PII Protection (`CreatePostScreen`)**: Community-scoped anonymous handles (e.g. `Anonymous #0421`), visibility & comment toggles, and automated PII/safety moderation checks (blocking Indian phone numbers, emails, Aadhaar, PAN, and severe safety keywords).
- **Threaded Discussions (`PostDetailScreen`)**: 6 emoji reactions, nested comment replies, author blocking, and confidential content reporting (`ReportBottomSheet`).
- **Activity & Profile (`ActivityScreen` & `ProfileScreen`)**: Unread notification tracking, isolated community-scoped anonymous identities, authored posts, and saved bookmarks.
- **Settings & Safety Center (`SettingsScreen` & `SafetyCenterScreen`)**: Push alert preference, city switcher, blocked accounts management, submitted reports log, and account deletion confirmation.

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose, Material Design 3, Navigation Compose (`@Serializable` type-safe routes), Edge-to-Edge adaptive `NavigationBar` / `NavigationRail`
- **Persistence**: Jetpack DataStore Preferences + local JSON persistence (`kotlinx.serialization`)
- **Build**: Gradle Kotlin DSL, AGP 9.1.1, Kotlin 2.2.21, Target/Compile SDK 36, Min SDK 26

## Build & Test

```bash
gradle assembleDebug
gradle :app:testDebugUnitTest
```
