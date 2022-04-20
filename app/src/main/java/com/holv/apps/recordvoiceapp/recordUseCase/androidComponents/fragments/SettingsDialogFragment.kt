package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.holv.apps.recordvoiceapp.databinding.SettingsRecordingDialogFragmentBinding
import com.holv.apps.recordvoiceapp.recordUseCase.businessLogic.RecordSettings
import com.holv.apps.recordvoiceapp.recordUseCase.businessLogic.RecordType
import com.holv.apps.recordvoiceapp.recordUseCase.proto.ProtoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsDialogFragment : BaseBindingDialogFragment<SettingsRecordingDialogFragmentBinding>() {

    var listenerSettings : SettingsMp3? = null
    var quality : RecordSettings? = null
    //Protobuf for saving MP3 settings
    private var repo: ProtoRepository? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listenerSettings = parentFragment as? SettingsMp3
        repo = ProtoRepository(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkRadioBasedOnProtoBuffers()
        with(binding) {
            btnSave.setOnClickListener {
                quality?.run {
                    listenerSettings?.saveSettings(this)
                }
                dismiss()
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
            rgOptions.setOnCheckedChangeListener { _, checkedId ->
                quality = when(checkedId) {
                    rbQualityHigh.id -> RecordSettings(RecordType.MP3_HIGH)
                    rbQualityHighest.id -> RecordSettings(RecordType.MP3_HIGHEST)
                    rbQualityLow.id -> RecordSettings(RecordType.MP3_LOW)
                    rbQualityStandard.id -> RecordSettings(RecordType.MP3_MEDIUM)
                    rbQualityStandardHigh.id -> RecordSettings(RecordType.MP3_MEDIUM_HIGH)
                    rbQualitySuperLow.id -> RecordSettings(RecordType.MP3_SUPER_LOW)
                    else -> RecordSettings(RecordType.MP3_MEDIUM)
                }
            }

        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = SettingsRecordingDialogFragmentBinding.inflate(inflater, container, false)

    private fun checkRadioBasedOnProtoBuffers() {
        CoroutineScope(Dispatchers.IO).launch {
            val radioButtonIndex =  repo?.readProto?.first()?.filter?.number
            radioButtonIndex?.run {
                val radioIdCheck = when (RecordType.values()[radioButtonIndex]) {
                    RecordType.MP3_SUPER_LOW -> binding.rbQualitySuperLow.id
                    RecordType.MP3_LOW -> binding.rbQualityLow.id
                    RecordType.MP3_MEDIUM -> binding.rbQualityStandard.id
                    RecordType.MP3_MEDIUM_HIGH -> binding.rbQualityStandardHigh.id
                    RecordType.MP3_HIGH -> binding.rbQualityHigh.id
                    RecordType.MP3_HIGHEST -> binding.rbQualityHighest.id
                }
                binding.rgOptions.check(radioIdCheck)
            }
        }
    }



    companion object {
        const val TAG = "SettingsDialogFragment"
        fun newInstance() = SettingsDialogFragment()
    }
}