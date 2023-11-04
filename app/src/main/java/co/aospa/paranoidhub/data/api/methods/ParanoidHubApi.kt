package co.aospa.paranoidhub.data.api.methods

import co.aospa.paranoidhub.data.api.ApiClient
import co.aospa.paranoidhub.data.api.methods.getdeviceinformation.GetDeviceInformationResponse
import io.ktor.client.call.body

interface ParanoidHubApi {

    suspend fun getDeviceInformation(codename: String): GetDeviceInformationResponse {
        return ApiClient.get("updates/$codename").body()
    }

    companion object {
        fun getApi(): ParanoidHubApi {
            return object : ParanoidHubApi {}
        }
    }
}

