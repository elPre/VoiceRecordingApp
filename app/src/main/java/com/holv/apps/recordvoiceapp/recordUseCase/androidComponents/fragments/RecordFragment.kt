package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.holv.apps.recordvoiceapp.databinding.RecordFragmentBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.Adapter
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels.RecordViewModel

class RecordFragment : BaseFragment<RecordFragmentBinding>() {

    private val adapter = Adapter(::eventsAction)
    private val viewModel: RecordViewModel by viewModels {
        RecordViewModel.Factory(requireContext().applicationContext as Application)
    }

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
        ActivityCompat.requestPermissions(
            requireActivity(),
            permissions,
            REQUEST_RECORD_AUDIO_PERMISSION
        )
        binding.recordRv.adapter = adapter
        viewModel.moduleItem.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            setListenersForHolder()
        }
    }

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this)?.commit()
        }
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
            }
            Events.PausePlayback -> {
                viewModel.animationOnOff(false)
                viewModel.pausePlayback()
            }
            is Events.PlayRecordedAudio -> {
                viewModel.startPlaybackFromRecordings(event.recordAudio)
            }
        }
    }

    private fun setListenersForHolder() {
        Handler(Looper.myLooper()!!).postDelayed(({
            viewModel.setListenerForHolders(binding.recordRv)
        }), ONE_SEC)

    }

    companion object {
        const val TAG = "RecordFragment"
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private const val ONE_SEC = 1000L
        fun newInstance() = RecordFragment()
    }

}