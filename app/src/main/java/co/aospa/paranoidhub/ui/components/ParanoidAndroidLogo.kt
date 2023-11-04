package co.aospa.paranoidhub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.aospa.paranoidhub.R

@Composable
fun ParanoidAndroidLogo(
    drawableResource: Int,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier
            .size(60.dp)
            .clip(CircleShape),
        painter = painterResource(id = drawableResource),
        contentDescription = "Paranoid Android Logo",
    )
}