package co.aospa.hub

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.aospa.hub.data.api.methods.ParanoidHubApi
import co.aospa.hub.data.api.methods.getdeviceinformation.GetDeviceInformationResponse
import co.aospa.hub.data.api.model.DeviceInformation
import co.aospa.hub.data.api.model.Flavor
import co.aospa.hub.data.api.model.State
import co.aospa.hub.utils.DeviceUtils
import co.aospa.hub.utils.FileUtils
import co.aospa.hub.utils.NetworkMonitor
import co.aospa.hub.utils.NotificationUtils
import co.aospa.hub.utils.UpdateService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.HttpsURLConnection

class MainActivityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUIState())
    val uiState: StateFlow<MainActivityUIState> = _uiState

    private var searchJob: Job? = null
    private var downloadJob: Job? = null

    private lateinit var deviceInformation: DeviceInformation

    private var isBetaEnabled = false
    private var downloadPackage = ""
    private var packageSHA256 = ""
    private var packageSize : Long = 0

    init {
        loadInformation()
    }

    fun observeNetworkStatus(context: Context) {
        viewModelScope.launch {
            NetworkMonitor(context).isOnline.collect { isOnline ->
                if (isOnline) {
                    searchForUpdate(context)
                } else {
                    _uiState.value = _uiState.value.copy(
                        state = State.NO_NETWORK,
                        informationText = "There is no internet connection",
                        update = null
                    )
                }
            }
        }
    }

    fun switchBeta(enabled: Boolean, context: Context) {
        if (isBetaEnabled != enabled) {
            isBetaEnabled = enabled

            searchJob?.cancel()

            _uiState.value = _uiState.value.copy(isBetaEnabled = isBetaEnabled, update = null, state = State.NONE)

            searchForUpdate(context)
        }
    }

    private fun loadInformation() {
        val deviceUtils = DeviceUtils()

        deviceInformation = DeviceInformation(
            codename = deviceUtils.getCodename(),
            paranoidAndroidVersion = deviceUtils.getParanoidAndroidVersion(),
            paranoidAndroidFlavor = deviceUtils.getCurrentVariant(),
            paranoidAndroidNumber = deviceUtils.getCurrentNumber(),
            isABDevice = deviceUtils.isABDevice(),
            buildDate = deviceUtils.getBuildDate(),
            androidVersion = deviceUtils.getAndroidVersion(),
            securityPatch = deviceUtils.getSpl()
        )

        isBetaEnabled = deviceInformation.paranoidAndroidFlavor == Flavor.Beta

        _uiState.value = MainActivityUIState(
            deviceInformation = deviceInformation,
            isBetaEnabled = isBetaEnabled,
            state = State.CAN_SEARCH
        )
    }

    fun searchForUpdate(context: Context) {
        searchJob = viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                update = null,
                informationText = context.getString(R.string.searching_for_update),
                state = State.SEARCHING
            )

            val formatter = DateTimeFormatter.ofPattern("d' 'MMMM' 'yyyy HH:mm")
            val formattedDate = LocalDateTime.now().format(formatter)
            try {
                val result = withContext(Dispatchers.IO) {
                    ParanoidHubApi.getApi().getDeviceInformation(deviceInformation.codename!!)
                }
                if (result.updates.isNotEmpty()) {
                    val matchingUpdates = result.updates.filter { update ->

                        if (isBetaEnabled) {
                            val isBetaUpdate = update.build_type == Flavor.Beta.toString()

                            // Check if the update's Android version is the same or higher
                            val isCompatibleAndroidVersion = update.android_version >= deviceInformation.androidVersion!!

                            // Check if the update's datetime is newer than the buildDate
                            val isNewerUpdate = update.datetime > deviceInformation.buildDate!!

                            isBetaUpdate && isCompatibleAndroidVersion && isNewerUpdate
                        } else {
                            // If not a beta update, filter by Android version and versionCode
                            update.android_version == deviceInformation.androidVersion!! && update.version_code > deviceInformation.paranoidAndroidNumber!!
                        }
                    }

                    if (matchingUpdates.isNotEmpty()) {
                        val sortedMatchingUpdates = matchingUpdates.sortedBy { it.version_code }
                        val beforeLastIndex = sortedMatchingUpdates.size - 2

                        if (beforeLastIndex >= 0 && deviceInformation.paranoidAndroidNumber!! == sortedMatchingUpdates[beforeLastIndex].version_code) {
                            // VersionCode is the before last in matchingUpdates
                            val nextMatchingUpdate = sortedMatchingUpdates[beforeLastIndex + 1]

                            if (nextMatchingUpdate.delta?.isBlank() == true && nextMatchingUpdate.url?.isBlank() == true) {
                                // Lets give it some animation
                                delay(1500)
                                _uiState.value = _uiState.value.copy(
                                    informationText = context.getString(R.string.system_up_to_date),
                                    state = State.CAN_SEARCH,
                                )
                                return@launch
                            }

                            if (nextMatchingUpdate.delta?.isNotBlank() == true) {
                                downloadPackage = nextMatchingUpdate.delta.toString()
                                packageSHA256 = nextMatchingUpdate.delta_sha256.toString()
                            } else {
                                downloadPackage = nextMatchingUpdate.url.toString()
                                packageSHA256 = nextMatchingUpdate.recovery_sha256.toString()
                            }
                        } else {
                            // VersionCode is not the before last in matchingUpdates
                            val latestMatchingUpdate = sortedMatchingUpdates.last()
                            downloadPackage = latestMatchingUpdate.url.toString()
                            packageSHA256 = latestMatchingUpdate.recovery_sha256.toString()
                        }

                        // Lets give it some animation
                        delay(1500)

                        _uiState.value = _uiState.value.copy(
                            informationText = "",
                            update = sortedMatchingUpdates.last(),
                            state = State.READY_TO_INSTALL,
                        )
                    } else {
                        // Lets give it some animation
                        delay(1500)
                        _uiState.value = _uiState.value.copy(
                            informationText = context.getString(R.string.system_up_to_date),
                            state = State.CAN_SEARCH,
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(state = State.CAN_SEARCH, informationText = "Couldn't find your device updates")
                }
            } catch (e: Exception) {
                Log.e("Error", e.toString())
                _uiState.value = _uiState.value.copy(state = State.CAN_SEARCH, informationText = "Error occurred")
            }
            _uiState.value = _uiState.value.copy(lastCheckedDate = formattedDate)
        }
    }

    fun startInstallation(context: Context) {
        viewModelScope.launch {
            try {
                if (downloadJob == null) {
                    downloadJob = withContext(Dispatchers.IO) {
                        downloadAndSaveFile(downloadPackage, uiState.value.update?.size!!.toLong(), context)
                    }

                    downloadJob?.invokeOnCompletion {
                        if (it != null) {
                            _uiState.value = _uiState.value.copy(
                                statusText = "",
                                state = State.READY_TO_INSTALL,
                                installationProgress = 0.00F
                            )
                        } else {
                            val filename = FileUtils.extractFileNameFromUrl(downloadPackage)
                            val downloadedFile =
                                File(Environment.getExternalStorageDirectory(), "ota/$filename")
                            val isSHA256Match = FileUtils.checkSHA256(downloadedFile, packageSHA256)

                            _uiState.value = uiState.value.copy(statusText = "Verifying SHA256")

                            if (isSHA256Match) {
                                _uiState.value = uiState.value.copy(
                                    statusText = "Installing",
                                    state = State.INSTALLING
                                )
                                prepareUpdate(downloadedFile, context)
                            } else {
                                _uiState.value = uiState.value.copy(
                                    informationText = "SHA256 verification failed, please download again.",
                                    state = State.CAN_SEARCH,
                                    installationProgress = 0.00F,
                                    update = null,
                                    statusText = ""
                                )
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(informationText = "Error occurred", state = State.READY_TO_INSTALL, installationProgress = 0.00F)
            }
        }
    }

    fun installLocalUpdate(localUpdate: Uri, context: Context) {
        viewModelScope.launch {
            val localFile = FileUtils.uriToFile(localUpdate, context)

            if (localFile != null) {
                _uiState.value = uiState.value.copy(
                    statusText = "Installing",
                    state = State.INSTALLING
                )
                packageSize = localFile.length()
                Log.i("Package size", packageSize.toString())
                prepareUpdate(localFile, context)
            } else {
                _uiState.value = uiState.value.copy(
                    statusText = "Error",
                    state = State.CAN_SEARCH
                )
            }
        }
    }

    private suspend fun downloadAndSaveFile(fileUrl: String, updateSize: Long, context: Context): Job {
        _uiState.value = _uiState.value.copy(statusText = context.getString(R.string.connecting), state = State.NONE)
        return viewModelScope.launch(Dispatchers.IO) {
            try {
                val fileName = FileUtils.extractFileNameFromUrl(fileUrl)
                val storageDir = withContext(Dispatchers.IO) {
                    val dir = File(Environment.getExternalStorageDirectory(), "ota")
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }
                    dir
                }

                val outputFile = File(storageDir, fileName)

                withContext(Dispatchers.IO) {
                    val url = URL(fileUrl)
                    val connection = url.openConnection() as HttpsURLConnection
                    connection.doInput = true

                    val responseCode = connection.responseCode
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        val size = connection.contentLengthLong
                        val input = url.openStream()
                        val output = FileOutputStream(outputFile)
                        val buffer = ByteArray(4096)
                        var bytesRead: Int

                        var totalBytesRead = 0L
                        val sizeType = FileUtils.getSizeType(updateSize)
                        val title = context.getString(R.string.downloading_file)

                        var lastProgress = -1.0F

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            if (!isActive) {
                                // Job has been canceled, exit the loop
                                return@withContext
                            }

                            output.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead.toLong()

                            val downloadedSize = FileUtils.getHumanSize(totalBytesRead, sizeType)
                            val totalSize: String

                            if (size != -1L) {
                                packageSize = size
                                totalSize = FileUtils.getHumanSize(size, sizeType)
                            } else {
                                packageSize = updateSize
                                totalSize = FileUtils.getHumanSize(updateSize, sizeType)
                            }

                            val progress = totalBytesRead.toFloat() / packageSize.toFloat()

                            if (progress - lastProgress > 0.01) {
                                // Batch updates: Update only when progress increases by a reasonable amount
                                _uiState.value = uiState.value.copy(
                                    state = State.DOWNLOADING,
                                    statusText = "$title $downloadedSize / $totalSize $sizeType",
                                    installationProgress = progress / 2
                                )
                                lastProgress = progress
                            }
                        }

                        if (isActive) {
                            output.flush()
                            output.close()
                            input.close()
                        }
                    }
                }
            } catch (e: Exception) {
                if (downloadJob?.isCancelled == false) {
                    // Handle errors and update the UI state accordingly
                    _uiState.value = uiState.value.copy(informationText = "Error occurred during download: ${e.message}")
                }
            }
        }
    }

    private fun prepareUpdate(file: File, context: Context) {
        val serviceIntent = Intent(context, UpdateService::class.java)
        serviceIntent.putExtra("updateFile", file)
        serviceIntent.putExtra("packageSize", packageSize)
        context.startService(serviceIntent)

        viewModelScope.launch {
            UpdateService.getInstance().updateStatusFlow.onEach {  }.collect { status ->
                Log.i("Status", status)
            }
        }
    }

    fun cancelDownload(context: Context) {
        viewModelScope.launch {
            try {
                if (downloadJob?.isActive == true) {
                    downloadJob?.cancel()
                    downloadJob = null
                    NotificationUtils.cancelNotification(context)
                    _uiState.value = _uiState.value.copy(state = State.READY_TO_INSTALL, installationProgress = 0.0F)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(informationText = "Error occurred")
            }
        }
    }

    fun openInternetSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        context.startActivity(intent)
    }

    fun showNotification(context: Context) {
        NotificationUtils.showNotification(context, uiState.value.update!!, uiState.value.state, (uiState.value.installationProgress * 100).toInt())
    }

    fun cancelNotification(context: Context) {
        NotificationUtils.cancelNotification(context)
    }

    private val cancelActionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "CANCEL_DOWNLOAD_REQUEST") {
                viewModelScope.launch {
                    if (context != null) {
                        cancelDownload(context)
                    }
                }
            }
        }
    }

    data class MainActivityUIState(
        val informationText: String = "",
        val state: State = State.NONE,
        val statusText: String = "",
        val installationProgress: Float = 0.0F,
        val deviceInformation: DeviceInformation? = null,
        val update: GetDeviceInformationResponse.Update? = null,
        val lastCheckedDate: String? = null,
        val isBetaEnabled : Boolean = false
    )
}