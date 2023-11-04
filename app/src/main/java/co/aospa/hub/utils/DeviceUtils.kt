package co.aospa.hub.utils

import android.os.Build
import android.os.SystemProperties
import co.aospa.hub.GlobalConstants
import co.aospa.hub.data.api.model.Flavor

class DeviceUtils {

    fun getCodename(): String {
        return SystemProperties.get(GlobalConstants.PROP_DEVICE)
    }

    fun getParanoidAndroidVersion(): String {
        val flavor = getCurrentVariant()
        val version = getCurrentVersion()
        val number = getCurrentNumber()

        return if (flavor == Flavor.Stable) {
            "$version $number"
        } else {
            "$version $flavor"
        }
    }

    private fun getCurrentVersion(): String {
        return SystemProperties.get(GlobalConstants.PROP_VERSION_MAJOR)
    }

    fun getCurrentVariant(): Flavor {
        val variant = SystemProperties.get(GlobalConstants.PROP_BUILD_TYPE)
        val flavorMap = mapOf(
            "Release" to Flavor.Stable,
            "Beta" to Flavor.Beta,
            "Alpha" to Flavor.Alpha,
            "Unofficial" to Flavor.Unofficial
        )

        return flavorMap[variant] ?: Flavor.Unofficial
    }

    fun getCurrentNumber(): String {
        return SystemProperties.get(GlobalConstants.PROP_VERSION_MINOR)
    }

    fun getAndroidVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun getSpl(): String {
        return Build.VERSION.SECURITY_PATCH
    }

    fun isABDevice() : Boolean {
        return SystemProperties.getBoolean(GlobalConstants.PROP_AB_DEVICE, false)
    }

    fun getBuildDate(): String {
        return Build.TIME.toString()
    }
}