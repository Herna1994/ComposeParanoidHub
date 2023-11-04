package co.aospa.hub.utils

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import co.aospa.hub.MainActivityViewModel
import java.util.*

class Scheduler : Service() {

    private lateinit var mAlarmManager: AlarmManager
    private lateinit var mAlarmIntent: PendingIntent

    override fun onCreate() {
        mAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mAlarmIntent = getPendingIntent()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setRepeatingAlarm()
        stopSelf()
        return START_NOT_STICKY
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.action = "UPDATE"
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun setRepeatingAlarm() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 4)
        calendar.set(Calendar.MINUTE, 58)
        calendar.set(Calendar.SECOND, 0)

        // Schedule the alarm to repeat every 24 hours
        mAlarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            mAlarmIntent
        )
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver", "Received alarm broadcast")
        if (context != null && intent?.action == "UPDATE") {
            Log.d("AlarmReceiver", "Received UPDATE action")

            val viewModel = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application).create(
                MainActivityViewModel::class.java)
            viewModel.searchForUpdate(context)
        }
    }
}
