package co.aospa.hub.ui.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import co.aospa.hub.R
import co.aospa.hub.utils.VibrateUtils
import co.aospa.hub.MainActivityViewModel

@Composable
fun SettingsSwitchRow(
    label: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Box(Modifier.padding(8.dp)) {
            Switch(
                checked = value,
                onCheckedChange = {
                    VibrateUtils.softVibration()
                    onValueChange(it)
                },
            )
        }
    }
}

@Composable
private fun SettingsDialogSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    viewModel: MainActivityViewModel,
    initialIsBetaEnabled: Boolean,
    context: Context
) {
    val configuration = LocalConfiguration.current
    var isBetaEnabled by remember { mutableStateOf(initialIsBetaEnabled) }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = context.getString(R.string.settings),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            HorizontalDivider()
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(top = 32.dp)
            ) {
                Column {
                    SettingsDialogSectionTitle(text = "Beta")
                    SettingsSwitchRow(
                        label = context.getString(R.string.enable_beta_description),
                        value = isBetaEnabled,
                        onValueChange = { isBetaEnabled = it }
                    )
                }
            }
        },
        confirmButton = {
            Text(
                text = "OK",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
                        viewModel.switchBeta(isBetaEnabled, context)
                        onDismiss() },
            )
        },
    )
}