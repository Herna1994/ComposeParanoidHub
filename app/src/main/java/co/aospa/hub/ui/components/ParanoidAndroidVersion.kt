package co.aospa.hub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.aospa.hub.R
import co.aospa.hub.data.api.model.DeviceInformation
import co.aospa.hub.MainActivityViewModel
import co.aospa.hub.utils.DateUtils

@Composable
fun ParanoidAndroidVersion(viewModel: MainActivityViewModel) {
    val uiState = viewModel.uiState.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        //colors = CardDefaults.cardColors(containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White)
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Header(uiState.value.deviceInformation)

            Spacer(modifier = Modifier.height(4.dp))

            DeviceDetails(uiState.value.deviceInformation)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(
                text = "#stayparanoid",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
            Image(
                painter = painterResource(if (!isSystemInDarkTheme()) R.drawable.logo_dark else R.drawable.logo_light),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .scale(2.5f)
                    .graphicsLayer {
                        alpha = 0.5f
                    },
                colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.Black, BlendMode.DstIn)
            )
        }
    }
}


@Composable
private fun Header(deviceInformation: DeviceInformation?) {

    val subtitle = stringResource(R.string.banner_subtitle)

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            deviceInformation?.paranoidAndroidVersion?.let { Text(text = it, style = MaterialTheme.typography.headlineLarge) }
            Text(text = subtitle, style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.outline))
        }
    }
}

@Composable
private fun DeviceDetails(deviceInformation: DeviceInformation?) {
    val bodyTextStyle = MaterialTheme.typography.bodyMedium
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    val androidVersion = stringResource(R.string.device_android_version)
    val securityVersion = stringResource(R.string.device_security_patchset)

    deviceInformation?.let {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Column {
                Text(
                    text = "$androidVersion: ${it.androidVersion}",
                    style = bodyTextStyle,
                    color = textColor,
                    modifier = Modifier.alpha(0.5f)
                )
                Text(
                    text = "$securityVersion: ${it.securityPatch?.let { it1 -> DateUtils().formatDate(it1) }}",
                    style = bodyTextStyle,
                    color = textColor,
                    modifier = Modifier.alpha(0.5f)
                )
            }
        }
    }
}
