package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationUtils {

    private const val NOTIFICATION_GROUP = "SHOW_MEDICINES_NOTIFICATION"
    private const val SUMMARY_ID = 0
    const val EXTRA_OPEN_NOTIFICATION = "OPEN_NOTIFICATION_ACTION"
    const val OPEN_NOTIFICATION_EXTRA_VALUE = 1

    fun buildNotificationManager(
        context: Context,
        channel: PushNotificationChannel
    ): NotificationManager? {
        // Since android Oreo notification channel is needed.
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService(NotificationManager::class.java)?.apply {
                val name = context.getString(channel.channelNameId)
                val shouldCreate = getNotificationChannel(name) == null

                if (shouldCreate) {
                    createNotificationChannel(
                        NotificationChannel(
                            context.getString(channel.channelId),
                            name,
                            NotificationManager.IMPORTANCE_HIGH
                        ).apply {
                            description = context.getString(channel.channelDescId)
                            setShowBadge(true)
                            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                            if (!channel.hasSound)
                                setSound(null, null)
                        })
                }
            }
        } else {
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        }
    }

    fun showRecordingNotification(context: Context, title: String, messageBody: String, clazz: Class<*>){

    }

    fun sendNotification(context: Context, title: String, messageBody: String, clazz: Class<*>) {

        val intent = Intent(context, clazz).apply {
            putExtra(EXTRA_OPEN_NOTIFICATION, OPEN_NOTIFICATION_EXTRA_VALUE)
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, context.getString(PushNotificationChannel.ALIVE.channelId))
//            .setSmallIcon(R.drawable.ic_round_notifications_24)
//            .setColor(context.getColor(R.color.nutriGreen))
//            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(messageBody)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody)
            )
            .setAutoCancel(true)
            .setGroup(NOTIFICATION_GROUP)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX) //Important for heads-up notification
            .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE) //Important for heads-up notification

        val summaryNotification = NotificationCompat.Builder(context, context.getString(PushNotificationChannel.ALIVE.channelId))
            //set content text to support devices running API level < 24
//            .setSmallIcon(R.drawable.ic_round_notifications_24)
//            .setColor(context.getColor(R.color.nutriGreen))
            //build summary info into InboxStyle template
            .setStyle(NotificationCompat.InboxStyle())
            //specify which group this notification belongs to
            .setGroup(NOTIFICATION_GROUP)
            //set this notification as the summary for the group
            .setGroupSummary(true)
            .setAutoCancel(true)
            .setSilent(true)
            .build()

        NotificationManagerCompat.from(context).apply {
            val notificationId = System.currentTimeMillis().toInt()
            notify(notificationId + 1, builder.build())
            notify(SUMMARY_ID, summaryNotification)
        }

    }


    fun clearNotifications(ctx: Context) {
        NotificationManagerCompat.from(ctx).cancelAll()
    }

    fun notificationsEnabled(ctx: Context): Boolean {
        val mgr = NotificationManagerCompat.from(ctx)
        if (!mgr.areNotificationsEnabled()) {
            return false
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PushNotificationChannel.values().all { c ->
                mgr.getNotificationChannel(ctx.getString(c.channelId))
                    ?.importance != NotificationManager.IMPORTANCE_NONE
            } // All currently-created channels need to be enabled
        } else {
            true
        }
    }
}