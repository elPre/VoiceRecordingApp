package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.util

import androidx.annotation.StringRes
import com.holv.apps.recordvoiceapp.R

enum class PushNotificationChannel(
    @StringRes val channelId: Int,
    @StringRes val channelNameId: Int,
    @StringRes val channelDescId: Int,
    val hasSound: Boolean,
) {
    ALIVE(R.string.alive_channel_id, R.string.alive_channel_name, R.string.notification_description, true)
}