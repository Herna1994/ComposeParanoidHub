package co.aospa.paranoidhub.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.aospa.paranoidhub.R
import co.aospa.paranoidhub.data.api.methods.getdeviceinformation.Updates
import co.aospa.paranoidhub.utils.UpdateUtils
import kotlinx.coroutines.delay

@Composable
fun PlatformChangelog(update: Updates) {

    val context = LocalContext.current
    val url = "https://paranoidandroid.co/changelog"

    val expanded = remember { mutableStateOf(false) }

    val title = if (UpdateUtils.isStable(update.buildType)) {
        stringResource(R.string.platform_changelog, update.version, update.versionCode)
    } else {
        stringResource(R.string.platform_changelog, update.version, update.buildType)
    }

    val hiddenText =
        "Updated to November SPL, Added wireless reverse charging, Fixed smoothness issues, Improved Glyphs performance, Check for proximity on single double tap and pickup, Fixed offline charging, Updated vendor code to NothingOS 1.5.4, Enabled native carried video calling for Airtel India"

    LaunchedEffect(true) {
        delay(1000)
        expanded.value = true
    }

    val itemList = parseTextToList(hiddenText)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        /*onClick = {
            expanded.value = !expanded.value
            VibrateUtils.softVibration()
        }*/
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(8.dp)
            )

            AnimatedVisibility(visible = expanded.value) {
                Box(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column {
                        for (item in itemList) {
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

fun openWebsiteUrl(url: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No app to handle this request", Toast.LENGTH_SHORT).show()
    }
}