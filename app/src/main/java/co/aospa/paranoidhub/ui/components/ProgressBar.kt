package co.aospa.paranoidhub.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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