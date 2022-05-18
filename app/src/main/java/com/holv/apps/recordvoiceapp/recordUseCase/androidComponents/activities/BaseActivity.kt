package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.activities

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.util.NotificationUtils
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.util.PushNotificationChannel

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(binding.root)
        NotificationUtils.buildNotificationManager(applicationContext, PushNotificationChannel.RECORD)
        NotificationUtils.buildNotificationManager(applicationContext, PushNotificationChannel.PLAYBACK)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.clearViews()
        _binding = null
    }

    protected open fun VB.clearViews() = Unit
}
