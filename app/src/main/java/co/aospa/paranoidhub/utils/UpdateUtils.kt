package co.aospa.paranoidhub.utils

object UpdateUtils {
    fun isStable(buildType: String): Boolean {
        return buildType == "Stable"
    }
}