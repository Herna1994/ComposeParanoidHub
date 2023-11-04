package co.aospa.paranoidhub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.aospa.paranoidhub.R
import co.aospa.paranoidhub.data.api.methods.getdeviceinformation.Updates
import co.aospa.paranoidhub.utils.FileUtils
import co.aospa.paranoidhub.utils.UpdateUtils

@Composable
fun NewUpdateCard(update: Updates) {

    val downloadSize : String = FileUtils.humanSize(update.size!!.toLong())

    var showDeltaDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val text = if (UpdateUtils.isStable(update.buildType)) {
        stringResource(R.string.available_to_download_stable, update.version, update.versionCode)
    } else {
        stringResource(R.string.available_to_download, update.version, update.buildType)
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
            Spacer(modifier = Modifier.width(8.dp)) // Add a spacer with 8dp width
            SuggestionChip(onClick = { showDeltaDialog = true }, label = { Text(text = "Delta") }, shape = ButtonDefaults.filledTonalShape)
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