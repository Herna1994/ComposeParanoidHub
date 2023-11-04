package co.aospa.hub.data.api.model

data class DeviceInformation (
    val codename: String?,
    val paranoidAndroidVersion: String?,
    val paranoidAndroidFlavor: Flavor?,
    val paranoidAndroidNumber: String?,
    val isABDevice: Boolean?,
    val buildDate: String?,
    val androidVersion: String?,
    val securityPatch: String?
)