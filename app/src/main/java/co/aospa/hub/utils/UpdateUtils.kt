package co.aospa.hub.utils

import android.util.Log
import co.aospa.hub.data.api.methods.ParanoidHubApi
import co.aospa.hub.data.api.methods.getdeviceinformation.GetDeviceInformationResponse

object UpdateUtils {
    fun isStable(buildType: String): Boolean {
        return buildType == "Stable"
    }

    suspend fun getPlatformChangelog(update: GetDeviceInformationResponse.Update): String? {
        val response = ParanoidHubApi.getApi().getPlatformChangelog()

        return response.changelog.firstOrNull {
            it.version == update.version && it.version_code == update.version_code && it.build_type == update.build_type
        }?.changelog_main

        /*
        // Markdown option
        val updateId = response.changelog.firstOrNull {
            it.version == update.version && it.version_code == update.version_code && it.build_type == update.build_type
        }?.id

        return updateId?.let { ParanoidHubApi.getApi().getChangelogMarkDown(it) }
        */
    }
}