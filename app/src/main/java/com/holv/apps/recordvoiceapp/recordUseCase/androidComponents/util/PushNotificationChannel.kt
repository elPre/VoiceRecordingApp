package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.util

import androidx.annotation.StringRes
import com.holv.apps.recordvoiceapp.R

enum class PushNotificationChannel(
    @StringRes val channelId: Int,
    @StringRes val channelNameId: Int,
    @StringRes val channelDescId: Int,
    val hasSound: Boolean,
) {
    RECORD(R.string.record_channel_id, R.string.record_channel_name, R.string.record_notification_description, false),
    PLAYBACK(R.string.playback_channel_id, R.string.playback_channel_name, R.string.playback_notification_description, false)
}