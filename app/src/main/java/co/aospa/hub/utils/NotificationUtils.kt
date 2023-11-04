package co.aospa.hub.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import co.aospa.hub.R
import co.aospa.hub.data.api.methods.getdeviceinformation.GetDeviceInformationResponse
import co.aospa.hub.data.api.model.State

object NotificationUtils {

    private val notificationManager: NotificationManager? = null

    fun showNotification(context: Context, update: GetDeviceInformationResponse.Update, state: State = State.NONE, installationProgress: Int = 0) {
        val isStable = UpdateUtils.isStable(update.build_type)

        val notification = NotificationCompat.Builder(context, "ParanoidHub Updates")
            .setSmallIcon(R.drawable.logo_dark)
            .setContentTitle(
                if (state == State.DOWNLOADING) {
                    if (isStable) {
                        context.getString(R.string.downloading_update_notification_title, update.version, update.version_code)
                    } else {
                        context.getString(R.string.downloading_update_notification_title, update.version, update.build_type)
                    }
                } else {
                    if (isStable) {
                        context.getString(R.string.available_to_download_stable, update.version, update.version_code)
                    } else {
                        context.getString(R.string.available_to_download, update.version, update.build_type)
                    }
                }
            )
            .setContentText(
                if (state == State.DOWNLOADING) {
                    ""
                } else {
                    context.getString(R.string.downloading_update_notification_summary)
                })
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)

        if (state == State.READY_TO_INSTALL) {
            val installIntent = Intent("DOWNLOAD")
            val installPendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                installIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            notification.addAction(0, context.getString(R.string.install_action), installPendingIntent)
        }

        if (state == State.DOWNLOADING) {
            val cancelIntent = Intent("CANCEL_DOWNLOAD")
            val cancelPendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            notification.addAction(0, context.getString(R.string.cancel_action), cancelPendingIntent)
        }

        if (state == State.DOWNLOADING || state == State.INSTALLING) {
            notification.setProgress(100, installationProgress * 2, false)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        val notificationId = 1
        val channel = NotificationChannel(
            "ParanoidHub Update",
            "Update Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        // Notify with a unique ID to update or replace the existing notification
        notificationManager.notify(notificationId, notification.build())
    }

    fun cancelNotification(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(1)
    }
}