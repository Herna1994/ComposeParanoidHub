package co.aospa.hub.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import co.aospa.hub.utils.VibrateUtils

@Composable
fun CurrentVersionInformation() {
    val context = LocalContext.current

    val navController = rememberNavController()

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp),
        onClick = {
            navController.navigate("destination_fragment_id")
            VibrateUtils.softVibration()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Uvite 2.1 changelog",
                modifier = Modifier.padding(start = 8.dp)
            )
            IconButton(
                onClick = {
                    navController.navigate("destination_fragment_id")
                    VibrateUtils.softVibration()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Arrow Forward"
                )
            }
        }
    }
}