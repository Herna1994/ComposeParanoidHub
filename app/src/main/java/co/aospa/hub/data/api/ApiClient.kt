package co.aospa.hub.data.api

import co.aospa.hub.BuildConfig
import co.aospa.hub.GlobalConstants
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiClient {

    private var BASE_URL = GlobalConstants.API_URL

    private val mHttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(
                contentType = ContentType.Text.Any
            )
        }
        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }
        defaultRequest {
            url {
                host = BASE_URL
            }
        }
    }

    private val httpClient: HttpClient
        get() = mHttpClient

    suspend fun get(endpoint: String): HttpResponse {
        return httpClient.get(endpoint)
    }
}