package co.aospa.hub.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.aospa.hub.R
import co.aospa.hub.data.api.methods.getdeviceinformation.Update
import co.aospa.hub.utils.FileUtils
import co.aospa.hub.utils.UpdateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Changelog(update: Update) {

    val expanded = remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        kotlinx.coroutines.delay(500)
        expanded.value = true
    }

    val deviceChangelogTitle = stringResource(R.string.device_changelog)
    val deviceChangelog = update.changelog_device?.let { parseTextToList(it) }

    val platformChangelogTitle = if (UpdateUtils.isStable(update.build_type)) {
        stringResource(R.string.platform_changelog, update.version, update.version_code)
    } else {
        stringResource(R.string.platform_changelog, update.version, update.build_type)
    }

    val hiddenText =
        "Updated to November SPL, Added wireless reverse charging, Fixed smoothness issues, Improved Glyphs performance, Check for proximity on single double tap and pickup, Fixed offline charging, Updated vendor code to NothingOS 1.5.4, Enabled native carried video calling for Airtel India"
    val platformChangelog = parseTextToList(hiddenText)

    val downloadSize : String = FileUtils.humanSize(update.size!!.toLong())

    AnimatedVisibility(visible = expanded.value, enter = fadeIn(), exit = fadeOut()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = deviceChangelogTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                if (!deviceChangelog.isNullOrEmpty()) {
                    for (item in deviceChangelog) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("• ")
                            Text(item, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            if (platformChangelog.isNotEmpty()) {
                Text(
                    text = platformChangelogTitle,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(8.dp)
                )
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column {
                        for (item in platformChangelog) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("• ")
                                Text(item, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            Text(
                text = "Download size: $downloadSize",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(8.dp)
            )
    }
    }

    /*Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        //colors = CardDefaults.cardColors(containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White),
    ) {
        AnimatedVisibility(visible = expanded.value) {
                if (deviceChangelog.isNullOrEmpty() && platformChangelog.isEmpty()) {
                    Text(modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                        text = "No changelogs were found for this update",
                        style = MaterialTheme.typography.titleMedium)
                } else {
                    if (!deviceChangelog.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = deviceChangelogTitle,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                if (!deviceChangelog.isNullOrEmpty()) {
                                    for (item in deviceChangelog) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("• ")
                                            Text(item, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }

                            if (platformChangelog.isNotEmpty()) {
                                Text(
                                    text = platformChangelogTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .padding(8.dp)
                                )

                                Column(
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Column {
                                        for (item in platformChangelog) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("• ")
                                                Text(item, style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        }*/

}

fun parseTextToList(text: String): List<String> {
    return text.split(", ")
}