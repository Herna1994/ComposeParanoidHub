package co.aospa.paranoidhub.data.api.methods.getdeviceinformation

import com.google.gson.annotations.SerializedName

data class GetDeviceInformationResponse(

    @SerializedName("updates" ) var updates : ArrayList<Updates> = arrayListOf()

)

data class Updates (

    @SerializedName("date"             ) var date            : String = "",
    @SerializedName("datetime"         ) var datetime        : String = "",
    @SerializedName("filename"         ) var filename        : String? = null,
    @SerializedName("delta"            ) var delta           : String? = null,
    @SerializedName("url"              ) var url             : String? = null,
    @SerializedName("recovery_sha256"  ) var recoverySha256  : String? = null,
    @SerializedName("fastboot"         ) var fastboot        : String? = null,
    @SerializedName("fastboot_sha256"  ) var fastbootSha256  : String? = null,
    @SerializedName("mirror"           ) var mirror          : String? = null,
    @SerializedName("telegram"         ) var telegram        : String? = null,
    @SerializedName("id"               ) var id              : String? = null,
    @SerializedName("size"             ) var size            : String? = null,
    @SerializedName("build_type"       ) var buildType       : String = "",
    @SerializedName("version_code"     ) var versionCode     : String = "",
    @SerializedName("version"          ) var version         : String = "",
    @SerializedName("android_version"  ) var androidVersion  : String = "",
    @SerializedName("android_spl"      ) var androidSpl      : String = "",
    @SerializedName("changelog_device" ) var changelogDevice : String? = null

)
