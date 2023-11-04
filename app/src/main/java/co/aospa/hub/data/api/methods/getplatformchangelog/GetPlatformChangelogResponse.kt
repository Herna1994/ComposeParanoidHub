package co.aospa.hub.data.api.methods.getplatformchangelog

import kotlinx.serialization.Serializable

@Serializable
data class GetPlatformChangelogResponse(

    val changelog : List<Changelog>

) {

    @Serializable
    data class Changelog (

        val version       : String? = null,
        val version_code   : String? = null,
        val build_type     : String? = null,
        val id            : String? = null,
        val changelog_main : String? = null

    )

}
