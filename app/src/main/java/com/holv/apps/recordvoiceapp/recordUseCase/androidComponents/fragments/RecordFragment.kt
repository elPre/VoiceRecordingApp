package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.holv.apps.recordvoiceapp.databinding.RecordFragmentBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.Adapter
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels.RecordViewModel

class RecordFragment : BaseFragment<RecordFragmentBinding>() {

    private val adapter = Adapter(::eventsAction)
    private val viewModel : RecordViewModel by viewModels {
        RecordViewModel.Factory(requireContext().applicationContext as Application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadPage()
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = RecordFragmentBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recordRv.adapter = adapter

        viewModel.moduleItem.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }
    }

    private fun eventsAction(event: Events) {
        when (event) {
            Events.Pause -> Log.d(TAG,"Pause recording/playing and animation")
            Events.Play -> Log.d(TAG,"Start playing the recording and animation with seek bar")
            Events.Record -> Log.d(TAG,"Start recording and animation")
            Events.Stop -> Log.d(TAG,"Stop recording/playing and animation")
        }
    }

    companion object {

        const val TAG = "RecordFragment"

        fun newInstance() = RecordFragment()
    }

}