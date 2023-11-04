package co.aospa.paranoidhub.data.api

import co.aospa.paranoidhub.GlobalConstants
import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json

object ApiClient {

    private var BASE_URL = GlobalConstants.API_URL

    private val mHttpClient = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(
                    contentType = ContentType.Text.Any
                )
            }
    }

    private val httpClient: HttpClient
        get() = mHttpClient

    suspend fun get(endpoint: String): HttpResponse {
        return httpClient.get("$BASE_URL/$endpoint")
    }
}