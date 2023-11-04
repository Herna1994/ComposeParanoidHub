package co.aospa.hub.ui.components

import android.content.Context
import android.content.Intent
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.aospa.hub.R
import co.aospa.hub.data.api.model.State
import co.aospa.hub.utils.VibrateUtils
import co.aospa.hub.MainActivityViewModel

@Composable
fun StateButtons(
    viewModel: MainActivityViewModel,
    context: Context,
    uiState: MainActivityViewModel.MainActivityUIState,
    view: View
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom,
    ) {
            when (uiState.state) {
                is State.NONE -> {
                }

                is State.NO_NETWORK -> {
                    Button(
                        onClick = {
                            VibrateUtils.softVibration()
                            viewModel.openInternetSettings(context)
                        },
                        Modifier.scale(1.1F)
                    ) {
                        Text(text = "Open Network Settings")
                    }
                }

                is State.CAN_SEARCH -> {
                    Button(
                        onClick = {
                            VibrateUtils.softVibration()
                            viewModel.searchForUpdate(context)
                        },
                        Modifier.scale(1.1F)
                    ) {
                        Text(text = stringResource(R.string.find_update_button))
                    }
                }

                is State.READY_TO_INSTALL -> {
                    Button(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            viewModel.startInstallation(context)
                        },
                        Modifier.scale(1.1F)
                    ) {
                        Text(text = stringResource(R.string.install_update_button))
                    }
                }

                is State.DOWNLOADING -> {
                    Button(
                        onClick = {
                            VibrateUtils.errorVibration()
                            val cancelIntent = Intent("CANCEL_DOWNLOAD")
                            context.sendBroadcast(cancelIntent)
                        },
                        Modifier.scale(1.1F),
                        colors = ButtonDefaults.textButtonColors(containerColor = Color.Red, contentColor = Color.White)
                    ) {
                        Text(text = stringResource(R.string.cancel_button))
                    }
                }

                is State.INSTALLING -> {
                        Button(
                            onClick = {
                                VibrateUtils.errorVibration()
                                viewModel.cancelDownload(context)
                                      },
                            Modifier.scale(1.1F),
                            colors = ButtonDefaults.textButtonColors(containerColor = Color.Red, contentColor = Color.White)
                        ) {
                            Text(text = stringResource(R.string.cancel_button))
                        }
                }

                is State.SEARCHING -> {}
            }
    }
}