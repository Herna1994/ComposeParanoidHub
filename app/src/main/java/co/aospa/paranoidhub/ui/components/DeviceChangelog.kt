package co.aospa.paranoidhub.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.aospa.paranoidhub.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceChangelog(changelogDevice: String?) {

    val expanded = remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (expanded.value) 180f else 0f,
        label = ""
    )

    LaunchedEffect(true) {
        kotlinx.coroutines.delay(1000)
        expanded.value = true
    }

    val title = stringResource(R.string.device_changelog)
    val itemList = changelogDevice?.let { parseTextToList(it) }

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
                modifier = Modifier.padding(8.dp)
            )

            AnimatedVisibility(visible = expanded.value) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    if (itemList != null) {
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

fun parseTextToList(text: String): List<String> {
    return text.split(", ")
}