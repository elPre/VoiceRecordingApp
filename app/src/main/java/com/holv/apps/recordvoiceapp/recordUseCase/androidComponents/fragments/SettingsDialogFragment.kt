package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.holv.apps.recordvoiceapp.databinding.SettingsRecordingDialogFragmentBinding
import com.holv.apps.recordvoiceapp.recordUseCase.businessLogic.RecordSettings
import com.holv.apps.recordvoiceapp.recordUseCase.businessLogic.RecordType

class SettingsDialogFragment : BaseBindingDialogFragment<SettingsRecordingDialogFragmentBinding>() {

    var listenerSettings : SettingsMp3? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            listenerSettings = it.getSerializable(ARG_LISTENER) as SettingsMp3?
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSave.setOnClickListener {
            listenerSettings?.saveSettings(RecordSettings(RecordType.MP3_HIGHEST))
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = SettingsRecordingDialogFragmentBinding.inflate(inflater, container, false)



    companion object {
        const val TAG = "SettingsDialogFragment"
        private const val ARG_LISTENER =  "arg-listener-settings"
        fun newInstance(listener: SettingsMp3) = SettingsDialogFragment().apply {
            arguments = bundleOf(
                ARG_LISTENER to listener
            )
        }
    }
}