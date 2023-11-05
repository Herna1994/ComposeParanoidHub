package co.aospa.hub.utils

object UpdateUtils {
    fun isStable(buildType: String): Boolean {
        return buildType == "Stable"
    }
}