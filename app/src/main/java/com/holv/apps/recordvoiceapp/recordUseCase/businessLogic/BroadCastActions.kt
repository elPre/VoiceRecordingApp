package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class BroadCastActions : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { cxt ->
            intent?.let {
                Intent(FILTER_INTENT).apply {
                    putExtra(ACTION_NAME_INTENT, intent.action)
                    putExtra(EXTRA_NOTIFICATION_ACTION, intent.getIntExtra(EXTRA_NOTIFICATION_ACTION, 0))
                    IntentFilter(FILTER_INTENT)
                }
            }?.also {
                Log.d(TAG,"sending local broadcast $it")
                LocalBroadcastManager.getInstance(cxt).sendBroadcast(it)
            }
        }
    }

    companion object {
        const val TAG = "BroadCastActions"
        const val FILTER_INTENT = "AUDIO-ACTION-RECORDER-NOTIFICATION-FILTER"
        const val ACTION_NAME_INTENT  = "AUDIO-ACTION-NOTIFICATION"
        const val EXTRA_NOTIFICATION_ACTION = "extra-notification-action"
    }
}