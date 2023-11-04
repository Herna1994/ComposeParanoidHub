package co.aospa.hub.data.api.methods.getdeviceinformation

import kotlinx.serialization.Serializable

@Serializable
data class GetDeviceInformationResponse(
    val updates: List<Update>
) {

    @Serializable
    data class Update(
        val date: String,
        val datetime: String,
        val filename: String?,
        val delta: String? = "",
        val delta_sha256: String? = "",
        val url: String?,
        val recovery_sha256: String? = "",
        val fastboot: String? = "",
        val fastboot_sha256: String? = "",
        val mirror: String? = "",
        val telegram: String? = "",
        val id: String?,
        val size: String?,
        val build_type: String,
        val version_code: String,
        val version: String,
        val android_version: String,
        val android_spl: String,
        val changelog_device: String?
    )

}
