package co.aospa.hub.utils

import android.content.Context
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import co.aospa.hub.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import io.ktor.util.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import kotlin.math.max

class ReleaseCheckerService {
    private val githubRepo = "Herna1994/ComposeParanoidHub"
    private val githubToken = "INSERT YOUR TOKEN IF THE REPO IS PRIVATE"

    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }
    }

    suspend fun checkAndInstallUpdates(context: Context) {
        val currentVersion = getCurrentAppVersion(context)
        val releases = fetchGitHubReleases()

        for (release in releases) {
            if (currentVersion?.let { isNewerVersion(release.tag_name, it) } == true) {
                val asset = getReleaseAsset(release.assets)
                if (asset != null) {
                    val downloadUrl = asset.browser_download_url
                    downloadAndInstallUpdate(downloadUrl, context)
                }
            }
        }
    }

    private suspend fun fetchGitHubReleases(): List<Release> {
        val response: HttpResponse = httpClient.get("https://api.github.com/repos/$githubRepo/releases") {
            // Only needed if the repository is private
            /*headers {
                append("Authorization", "token $githubToken")
            }*/
        }

        return if (response.status.value in 200..299) {
            response.body()
        } else {
            emptyList()
        }
    }

    @OptIn(InternalAPI::class)
    private suspend fun downloadAndInstallUpdate(downloadUrl: String, context: Context) {
        val response: HttpResponse = httpClient.get(downloadUrl) {
            // Only needed if the repository is private
            /* headers {
                append("Authorization", "token $githubToken")
            }*/
        }

        if (response.status.value in 200..299) {
            val fileName = downloadUrl.substringAfterLast("/")
            val storageDir = withContext(Dispatchers.IO) {
                val dir = File(Environment.getExternalStorageDirectory(), "ota")
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                dir
            }
            val file = File(storageDir, fileName)

            val byteArray = response.content.toByteArray()
            file.writeBytes(byteArray)

            installApk(Uri.parse(file.absolutePath), context)
        } else {
            Log.e("Bad response", response.toString())
        }
    }

    private fun installApk(apkUri: Uri, context: Context) {
        val packageInstaller = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)

        params.setAppPackageName(context.packageName)
        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)

        try {
            val inputStream = context.contentResolver.openInputStream(apkUri)
            val outputStream = session.openWrite("package", 0, -1)

            inputStream.use { input ->
                outputStream.use { output ->
                    val buffer = ByteArray(65536)
                    var bytesRead = 0
                    while (true) {
                        if (input != null) {
                            bytesRead = input.read(buffer)
                        }
                        if (bytesRead == -1) {
                            break
                        }
                        output.write(buffer, 0, bytesRead)
                    }
                }
            }
            session.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        context.packageManager.packageInstaller.installExistingPackage("co.aospa.hub", PackageManager.INSTALL_REASON_UNKNOWN, null)
    }

    private fun getCurrentAppVersion(context: Context): String? {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    private fun isNewerVersion(newVersion: String, oldVersion: String): Boolean {
        val newVersionParts = newVersion.split(".")
        val oldVersionParts = oldVersion.split(".")

        val maxParts = max(newVersionParts.size, oldVersionParts.size)

        for (i in 0 until maxParts) {
            val newPart = newVersionParts.getOrNull(i)?.toIntOrNull() ?: 0
            val oldPart = oldVersionParts.getOrNull(i)?.toIntOrNull() ?: 0

            if (newPart > oldPart) {
                return true
            } else if (newPart < oldPart) {
                return false
            }
        }

        return false
    }

    private fun getReleaseAsset(assets: List<Asset>): Asset? {
        return if (assets.isNotEmpty()) assets[0] else null
    }

    @Serializable
    data class Release(
        val tag_name: String,
        val assets: List<Asset>
    )

    @Serializable
    data class Asset(
        val browser_download_url: String
    )
}