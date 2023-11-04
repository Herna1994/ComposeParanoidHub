package co.aospa.hub.data.api.methods

import co.aospa.hub.data.api.ApiClient
import co.aospa.hub.data.api.methods.getdeviceinformation.GetDeviceInformationResponse
import co.aospa.hub.data.api.methods.getplatformchangelog.GetPlatformChangelogResponse
import io.ktor.client.call.body

interface ParanoidHubApi {

    suspend fun getDeviceInformation(codename: String): GetDeviceInformationResponse {
        return ApiClient.get("updates/$codename").body()
    }

    suspend fun getPlatformChangelog(): GetPlatformChangelogResponse {
        return ApiClient.get("changelog").body()
    }

    suspend fun getChangelogMarkDown(id: String): String? {
        return try {
            val response = ApiClient.get("changelogs/${id}.md")

            if (response.status.value == 200) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        fun getApi(): ParanoidHubApi {
            return object : ParanoidHubApi {}
        }
    }
}

