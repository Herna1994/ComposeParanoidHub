package co.aospa.hub.utils

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibrateUtils {

    private var vibrationService: Vibrator? = null

    fun initialize(context: Context) {
        if (vibrationService == null) {
            vibrationService = launchVibrationService(context)
        }
    }

    private fun launchVibrationService(context: Context): Vibrator {
        val vibrationManager = context.applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        return vibrationManager.defaultVibrator
    }

    fun softVibration() {
        vibrationService?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
    }

    fun successVibration() {
        vibrationService?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
    }

    fun errorVibration() {
        vibrationService?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
    }

    fun cancelVibrationService() {
        vibrationService?.cancel()
    }
}