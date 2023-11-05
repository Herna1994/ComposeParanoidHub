package co.aospa.hub.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(
    isSearching: Boolean,
    progress: () -> Float
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .height(20.dp),
        shape = RoundedCornerShape(50),
    ) {
        if (isSearching) {
            LinearProgressIndicator()
        } else {
            LinearProgressIndicator(
                progress = progress,
            )
        }
    }
}