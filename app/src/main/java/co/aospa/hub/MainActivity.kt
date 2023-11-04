package co.aospa.hub

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import co.aospa.hub.data.api.model.State
import co.aospa.hub.ui.components.LocalUpdateBottomSheet
import co.aospa.hub.ui.components.Changelog
import co.aospa.hub.ui.components.NewUpdateCard
import co.aospa.hub.ui.components.ParanoidAndroidVersion
import co.aospa.hub.ui.components.ProgressBar
import co.aospa.hub.ui.components.SettingsDialog
import co.aospa.hub.ui.components.StateButtons
import co.aospa.hub.ui.theme.ParanoidHubTheme
import co.aospa.hub.utils.VibrateUtils
import co.aospa.hub.utils.Scheduler

private lateinit var viewModel: MainActivityViewModel
private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
private lateinit var broadcastReceiver: BroadcastReceiver

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        VibrateUtils.initialize(this)
        viewModel = MainActivityViewModel()
        viewModel.observeNetworkStatus(this)

        // Only required to test on a non PA/non system permission apk
        //if (!hasManageExternalStorage()) requestScopedStorage()
        //requestInstallPermissions()

        val serviceIntent = Intent(this, Scheduler::class.java)

        if (isServiceRunning(this, Scheduler::class.java)) {
            // Service is already running
        } else {
            val channelId = "2"
            val channelName = "Your Service Channel"

            val notificationManager = NotificationManagerCompat.from(this)
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, "2")
                .setContentTitle("Your Foreground Service")
                .setContentText("Service is running...")
                .setSmallIcon(R.drawable.logo_dark)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()

            //startForeground(2, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            startService(serviceIntent)
        }

        enableEdgeToEdge()

        setContent {
            ParanoidHubTheme {
                MainView()
            }
        }

        // Only required to test on a non PA/non system permission apk
        //requestNotificationPermission()
        //requestSystemPermissions()

        // Only required if we want to enable to auto-update the apk
        /*runBlocking {
            launch(Dispatchers.IO) {
                ReleaseCheckerService().checkAndInstallUpdates(this@MainActivity)
            }
        }
        */

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "DOWNLOAD") {
                    viewModel.startInstallation(context)
                }
                if (intent.action == "CANCEL_DOWNLOAD") {
                    viewModel.cancelDownload(context)
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction("DOWNLOAD")
        intentFilter.addAction("CANCEL_DOWNLOAD")

        registerReceiver(broadcastReceiver, intentFilter, RECEIVER_EXPORTED)

        filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedUri = result.data?.data

                viewModel.installLocalUpdate(selectedUri!!, this)

            }
        }
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun hasManageExternalStorage(): Boolean {
        return Environment.isExternalStorageManager()
    }

    private fun requestScopedStorage() {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.parse(String.format("package:%s", packageName))
        onRequestScopedStorage.launch(intent)
    }

    private fun requestInstallPermissions() {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.data = Uri.parse(String.format("package:%s", packageName))
        this.startActivity(intent)
    }

    private fun requestSystemPermissions() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse(String.format("package:%s", packageName))
        this.startActivity(intent)
    }

    private val onRequestScopedStorage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (!Environment.isExternalStorageManager()) {
            requestScopedStorage()
        }
    }

    private fun requestNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                123
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView() {

    val activity = (LocalContext.current as? Activity)

    val uiState by viewModel.uiState.collectAsState()

    val showBottomSheetState = remember { mutableStateOf(false) }
    val showBottomSheet by showBottomSheetState
    var showSettingsDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val view = LocalView.current
    val context = LocalContext.current

    val isProgressBarShowing by rememberUpdatedState(uiState.state == State.SEARCHING || uiState.state == State.DOWNLOADING)

    LaunchedEffect(uiState.state, uiState.installationProgress) {
        if (uiState.state == State.READY_TO_INSTALL
            || uiState.state == State.DOWNLOADING) {
            viewModel.showNotification(context)
        } else {
            viewModel.cancelNotification(context)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { activity?.finish() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    AnimatedVisibility(
                        visible = isProgressBarShowing,
                        enter = fadeIn(animationSpec = tween(delayMillis = 500)),
                        exit = fadeOut()
                    ) {
                        ProgressBar(
                            progress = { uiState.installationProgress },
                            isSearching = uiState.state == State.SEARCHING,
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = !isProgressBarShowing,
                        enter = scaleIn(),
                        exit = scaleOut()
                    ) {
                        OutlinedButton(
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            onClick = {
                                VibrateUtils.softVibration()
                                showBottomSheetState.value = true
                            })
                        {
                            Text(text = stringResource(R.string.local_update))
                        }
                    }

                    AnimatedVisibility(
                        visible = !isProgressBarShowing,
                        enter = slideInHorizontally(initialOffsetX = { 70 },),
                        exit = slideOutHorizontally(targetOffsetX = { 140 })
                    ) {
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                }
            )
        },

        ) { innerPadding ->

        if (showSettingsDialog) {
            if (activity != null) {
                SettingsDialog(onDismiss = {
                    showSettingsDialog = false
                }, viewModel = viewModel,
                    initialIsBetaEnabled = uiState.isBetaEnabled,
                    context = context)
            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(
                PaddingValues(
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    innerPadding.calculateTopPadding(),
                    innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                )
            )) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.animateContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(uiState.statusText.isNotBlank())
                    {
                        Text(modifier = Modifier.padding(vertical = 8.dp), text = uiState.statusText, style = MaterialTheme.typography.titleMedium)
                    }

                    ParanoidAndroidVersion(viewModel)

                    if (uiState.update != null) {
                        AnimatedVisibility(uiState.state != State.SEARCHING)
                        {
                            NewUpdateCard(uiState.update!!)
                        }

                        AnimatedVisibility(
                            visible = uiState.state != State.SEARCHING
                        ) {
                            Changelog(uiState.update!!)
                        }
                    }

                    AnimatedVisibility(
                        visible = uiState.informationText.isNotBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(vertical = 24.dp),
                                    text = uiState.informationText,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (uiState.state == State.CAN_SEARCH) {
                                    val title = stringResource(R.string.last_checked)
                                    Text(text = "$title ${uiState.lastCheckedDate}", style = MaterialTheme.typography.titleSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = uiState.state != State.SEARCHING && uiState.state != State.NONE,
        enter = slideInVertically(initialOffsetY = { 300 }),
        exit = slideOutVertically(targetOffsetY = { -160 })
    ) {
        StateButtons(viewModel, context, uiState, view)
    }

    if (showBottomSheet) {
        LocalUpdateBottomSheet(
            showBottomSheetState = showBottomSheetState,
            filePickerLauncher = filePickerLauncher
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ParanoidHubTheme {
        MainView()
    }
}