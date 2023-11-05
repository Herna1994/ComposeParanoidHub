package co.aospa.hub.ui.components

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.aospa.hub.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalUpdateBottomSheet(
    showBottomSheetState: MutableState<Boolean>,
    filePickerLauncher: ActivityResultLauncher<Intent>,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        windowInsets = WindowInsets(bottom = 0.dp),
        onDismissRequest = {
            showBottomSheetState.value = false
        },
        sheetState = sheetState
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            text = stringResource(R.string.local_update),
            style = MaterialTheme.typography.titleLarge
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.local_update_title),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = stringResource(R.string.local_update_summary),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.padding(vertical = 16.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                        .setType("application/zip")
                        .addCategory(Intent.CATEGORY_OPENABLE)
                    filePickerLauncher.launch(intent)
                },
            ) {
                Text(text = stringResource(R.string.install_local_update))
            }
        }
    }
}