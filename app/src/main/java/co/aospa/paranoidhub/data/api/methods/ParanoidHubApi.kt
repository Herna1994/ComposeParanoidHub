package co.aospa.paranoidhub.data.api.methods

import co.aospa.paranoidhub.data.api.ApiClient
import co.aospa.paranoidhub.data.api.methods.getdeviceinformation.GetDeviceInformationResponse
import retrofit2.Response
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

interface ParanoidHubApi {

    @GET("updates/{codename}")
    suspend fun getDeviceInformation(
        @Path("codename") codename: String
    ): Response<GetDeviceInformationResponse>

    companion object {
        fun getApi(): ParanoidHubApi? {
            return ApiClient.client?.create<ParanoidHubApi>()
        }
    }
}