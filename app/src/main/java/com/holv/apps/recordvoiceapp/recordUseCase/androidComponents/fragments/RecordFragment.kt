package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.holv.apps.recordvoiceapp.databinding.RecordFragmentBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.Adapter
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.pojos.RecordAudio
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels.RecordViewModel
import com.holv.apps.recordvoiceapp.recordUseCase.businessLogic.RecordSettings


class RecordFragment : BaseFragment<RecordFragmentBinding>(),
    SettingsMp3,
    SavingFileMp3,
    DialogOnDelete {

    private val adapter = Adapter(::eventsAction)
    private val viewModel: RecordViewModel by viewModels {
        RecordViewModel.Factory(requireContext().applicationContext as Application)
    }
    private var isListenerReadyForUpdateTickClock = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        val isNewFragment = savedInstanceState == null
        viewModel.loadPage(isNewFragment)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = RecordFragmentBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recordRv.adapter = adapter
        viewModel.moduleItem.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items.toList())
            setListenersForHolder()
        }
        viewModel.wasItemAdd.observe(viewLifecycleOwner) { pair ->
            if (pair.first) {
                adapter.submitList(pair.second.toList())
            }
        }
        viewModel.shareLiveData.observe(viewLifecycleOwner) { intentData ->
            intentData?.let {
                startActivity(it)
            }
        }
    }

    override fun onSaveFile(fileName: String, listeners: DialogFragmentListeners) {
        viewModel.onSaveFile(fileName, listeners)
    }

    override fun onCancelSave() {
        viewModel.onCancelSave()
    }

    override fun saveSettings(recordSettings: RecordSettings) {
        viewModel.saveSettings(recordSettings)
    }

    override fun onDeleteRecording(adapterPos: Int, audio: RecordAudio) {
        viewModel.deleteRecording(audio, adapterPos)
    }

    private fun eventsAction(event: Events) {
        when (event) {

            Events.Pause -> {
                viewModel.animationOnOff(false)
                viewModel.pauseRecording()
            }

            Events.Play -> {
                viewModel.animationOnOff(true)
                viewModel.startPlayback()
            }

            Events.Record -> {
                viewModel.animationOnOff(true)
                viewModel.starRecording()
            }

            Events.Stop -> {
                viewModel.animationOnOff(false)
                viewModel.stopRecording()
                viewModel.stopPlayback()
                if (viewModel.startRecording.get()) {
                    SaveFileDialogFragment.newInstance()
                        .show(childFragmentManager, SaveFileDialogFragment.TAG)
                }
            }

            Events.PausePlayback -> {
                viewModel.animationOnOff(false)
                viewModel.pausePlayback()
            }

            is Events.PlayRecordedAudio -> {
                viewModel.animationOnOff(true)
                viewModel.stopPlayback()
                viewModel.startPlaybackFromRecordings(event.recordAudio)
            }

            is Events.SeekBarAudio -> viewModel.setSeekBarPos(event.pos)

            is Events.SeekBarReflectOnTimer -> {
                if (isListenerReadyForUpdateTickClock) {
                    viewModel.setSeekBarPosUpdateTimer(event.pos)
                }
            }

            Events.OpenSettings -> {
                SettingsDialogFragment.newInstance()
                    .show(childFragmentManager, SettingsDialogFragment.TAG)
            }

            is Events.ShareRecordedAudio -> viewModel.shareMp3File(event.recordAudio)

            is Events.DeleteRecordedAudio -> {
                DeleteMp3FileDialogFragment.newInstance(event.recordAudio, event.adapterPosition)
                    .show(childFragmentManager, DeleteMp3FileDialogFragment.TAG)
            }
        }
    }

    private fun setListenersForHolder() {
        Handler(Looper.myLooper()!!).postDelayed(({
            viewModel.setListenerForHolders(binding.recordRv)
            isListenerReadyForUpdateTickClock = true
        }), ONE_SEC)
    }

    companion object {
        const val TAG = "RecordFragment"
        private const val ONE_SEC = 1000L
        fun newInstance() = RecordFragment()
    }

}