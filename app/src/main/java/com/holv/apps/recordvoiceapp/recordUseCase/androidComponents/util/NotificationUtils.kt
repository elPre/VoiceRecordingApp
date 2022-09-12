package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.holv.apps.recordvoiceapp.R
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.activities.MainActivity
import com.holv.apps.recordvoiceapp.recordUseCase.businessLogic.BroadCastActions


object NotificationUtils {

    private const val NOTIFICATION_PLAYBACK_ID = 1
    private const val NOTIFICATION_RECORD_ID = 2


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
                            NotificationManager.IMPORTANCE_LOW
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

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val openAppIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        val builder = NotificationCompat.Builder(context, context.getString(PushNotificationChannel.PLAYBACK.channelId))
            .setSmallIcon(R.drawable.ic_baseline_fiber_manual_record_24)
            .setColor(context.getColor(R.color.primaryBlue))
            .setContentTitle(context.getString(R.string.record_notification_title))
            .setContentText(messageBody)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody)
            )
            .setAutoCancel(false)
            .setContentIntent(openAppIntent)
            .setOnlyAlertOnce(true)//shows notification for only first time
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).apply {
            notify(NOTIFICATION_RECORD_ID, builder.build())
        }
    }

    fun showPlaybackNotification(context: Context, title: String, messageBody: String, isPlayback: Boolean) {

        val mediaSession  = MediaSessionCompat(context,"tag")

        //Playback intent
        val playbackIntent = Intent(context, BroadCastActions::class.java).apply {
            action = AudioNotificationActions.PLAY.name
            putExtra(BroadCastActions.EXTRA_NOTIFICATION_ACTION, AudioNotificationActions.PLAY.ordinal)
        }
        val playBackPendingIntent = PendingIntent.getBroadcast(context, 0, playbackIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        //Pause intent
        val pausePlaybackIntent = Intent(context, BroadCastActions::class.java).apply {
            action = AudioNotificationActions.PAUSE.name
            putExtra(BroadCastActions.EXTRA_NOTIFICATION_ACTION, AudioNotificationActions.PAUSE.ordinal)
        }
        val pausePlaybackPendingIntent = PendingIntent.getBroadcast(context, 0, pausePlaybackIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val buttonDrawable = if (isPlayback) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24
        val buttonIntent = if (isPlayback) pausePlaybackPendingIntent else playBackPendingIntent
        val stringDescription = if (isPlayback) context.getString(R.string.pause) else context.getString(R.string.play)

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val openAppIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, context.getString(PushNotificationChannel.PLAYBACK.channelId))
            .setSmallIcon(R.drawable.ic_baseline_audio_file_24)
            .setColor(context.getColor(R.color.primaryBlue))
            .setContentTitle(String.format(context.getString(R.string.playback_notification_title), title))
            .setContentText(messageBody)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0)
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setContentIntent(openAppIntent)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)//shows notification for only first time
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(buttonDrawable, stringDescription, buttonIntent)


        NotificationManagerCompat.from(context).apply {
            notify(NOTIFICATION_PLAYBACK_ID, builder.build())
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

    enum class AudioNotificationActions {
        PLAY,
        PAUSE,
        STOP,
        RECORD
    }

}