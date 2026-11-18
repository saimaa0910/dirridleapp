package com.example.domain

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager

object LocationCatalog {
    val indiaCities: List<IndiaCity> = listOf(
        IndiaCity("Mumbai", "Maharashtra", "mumbai", 19.0760, 72.8777, "city-mumbai"),
        IndiaCity("Delhi", "Delhi", "delhi", 28.6139, 77.2090, "city-delhi"),
        IndiaCity("Bengaluru", "Karnataka", "bengaluru", 12.9716, 77.5946, "city-bengaluru"),
        IndiaCity("Hyderabad", "Telangana", "hyderabad", 17.3850, 78.4867, "city-hyderabad"),
        IndiaCity("Chennai", "Tamil Nadu", "chennai", 13.0827, 80.2707, "city-chennai"),
        IndiaCity("Kolkata", "West Bengal", "kolkata", 22.5726, 88.3639, "city-kolkata"),
        IndiaCity("Pune", "Maharashtra", "pune", 18.5204, 73.8567, "city-pune"),
        IndiaCity("Ahmedabad", "Gujarat", "ahmedabad", 23.0225, 72.5714, "city-ahmedabad"),
        IndiaCity("Jaipur", "Rajasthan", "jaipur", 26.9124, 75.7873, "city-jaipur"),
        IndiaCity("Lucknow", "Uttar Pradesh", "lucknow", 26.8467, 80.9462, "city-lucknow"),
    )

    fun defaultCity(): IndiaCity = indiaCities[2] // Bengaluru

    fun findByName(name: String?): IndiaCity {
        return indiaCities.find { it.name.equals(name, ignoreCase = true) } ?: defaultCity()
    }

    fun findNearestCity(lat: Double, lng: Double): IndiaCity {
        return indiaCities.minByOrNull { city ->
            val dLat = city.lat - lat
            val dLng = city.lng - lng
            dLat * dLat + dLng * dLng
        } ?: defaultCity()
    }

    @SuppressLint("MissingPermission")
    fun detectLastKnownCity(context: Context): IndiaCity? {
        return try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
            val providers = lm.getProviders(true)
            val bestLocation = providers
                .mapNotNull { provider -> lm.getLastKnownLocation(provider) }
                .maxByOrNull { it.time }
            bestLocation?.let { findNearestCity(it.latitude, it.longitude) }
        } catch (_: SecurityException) {
            null
        } catch (_: Exception) {
            null
        }
    }
}

object TimeUtils {
    fun formatRelativeTime(createdAtMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
        val diffSeconds = ((nowMillis - createdAtMillis) / 1000L).coerceAtLeast(0L)
        return when {
            diffSeconds < 60L -> "Just now"
            diffSeconds < 3600L -> "${diffSeconds / 60L}m ago"
            diffSeconds < 86_400L -> "${diffSeconds / 3600L}h ago"
            else -> "${diffSeconds / 86_400L}d ago"
        }
    }

    fun formatIndianCount(count: Int): String {
        val s = count.toString()
        if (s.length <= 3) return s
        val lastThree = s.takeLast(3)
        val rest = s.dropLast(3)
        val grouped = rest.reversed().chunked(2).joinToString(",").reversed()
        return "$grouped,$lastThree"
    }
}
