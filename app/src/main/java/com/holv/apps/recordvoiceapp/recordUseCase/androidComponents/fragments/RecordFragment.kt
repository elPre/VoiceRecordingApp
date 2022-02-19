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
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders.FireAnimation
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels.RecordViewModel

class RecordFragment : BaseFragment<RecordFragmentBinding>() {

    private val adapter = Adapter(::eventsAction)
    private val viewModel : RecordViewModel by viewModels {
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
        binding.recordRv.adapter = adapter
        viewModel.moduleItem.observe(::getLifecycle) { items ->
            adapter.submitList(items)
        }
    }

    private fun eventsAction(event: Events) {
        when (event) {
            Events.Pause -> Log.d(TAG,"Pause recording/playing and animation")
            Events.Play -> turnAnimation(true)
            Events.Record -> turnAnimation(true)
            Events.Stop -> turnAnimation(false)
        }
    }

    private fun turnAnimation(turn: Boolean) {
        val recyclerView = binding.recordRv
        val itemCount = recyclerView.adapter?.itemCount
        if (itemCount != null && itemCount > 0) {
            val holder = recyclerView.findViewHolderForAdapterPosition(1)
            if(holder is FireAnimation) {
                holder.onFireAnimation(turn)
            }
        }
    }

    companion object {
        const val TAG = "RecordFragment"
        fun newInstance() = RecordFragment()
    }

}