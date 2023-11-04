package co.aospa.hub.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.aospa.hub.R
import co.aospa.hub.data.api.methods.getdeviceinformation.GetDeviceInformationResponse
import co.aospa.hub.utils.FileUtils
import co.aospa.hub.utils.UpdateUtils

@Composable
fun NewUpdateCard(update: GetDeviceInformationResponse.Update) {

    val downloadSize : String = FileUtils.humanSize(update.size!!.toLong())

    var showDeltaDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val text = if (UpdateUtils.isStable(update.build_type)) {
        stringResource(R.string.available_to_download_stable, update.version, update.version_code)
    } else {
        stringResource(R.string.available_to_download, update.version, update.build_type)
    }

    DeltaExplanationDialog(showDeltaDialog = showDeltaDialog, onDismiss = { showDeltaDialog = false })

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            textAlign = TextAlign.Center,
            text = text,
            fontWeight = FontWeight.Black,
            maxLines = 1
        )

        if (!update.delta.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(8.dp))
            SuggestionChip(onClick = { showDeltaDialog = true }, label = { Text(text = "Delta") })
        }
    }

    /*Card(
        modifier = Modifier.padding(horizontal = 48.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            text = "${nextUpdate.version} ${nextUpdate.versionCode} is available to download")
        Text(
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            text = "Download size: $downloadSize")
    }*/
}

@Composable
private fun DeltaExplanationDialog(
    showDeltaDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDeltaDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "OK")
                }
            },
            title = {
                Text(text = "What is a delta update?")
            },
            text = {
                Column {
                    Text(
                        text = "Delta updates are incremental updates that contain only the changes between your current ROM version and the new version. This means the download size is smaller compared to full updates."
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        )
    }
}