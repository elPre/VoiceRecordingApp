package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.holv.apps.recordvoiceapp.databinding.AskSaveFileDialogFragmentBinding

class SaveFileDialogFragment : BaseBindingDialogFragment<AskSaveFileDialogFragmentBinding>() {

    private var listenerSaveFile : SavingFileMp3? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            listenerSaveFile = it.getSerializable(ARG_LISTENER) as SavingFileMp3?
        }
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        AskSaveFileDialogFragmentBinding.inflate(inflater, container,  false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSave.setOnClickListener {
            if (binding.etSaveFileName.text.toString().isNotBlank()) {
                listenerSaveFile?.onSaveFile(binding.etSaveFileName.text.toString())
                dismiss()
            }
        }

        binding.btnCancel.setOnClickListener {
            listenerSaveFile?.onCancelSave()
            dismiss()
        }
    }


    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Log.d(TAG,"onCancel do no save file the same  as cancel  or keep the  file")
        listenerSaveFile?.onCancelSave()
    }

    companion object {
        const val TAG = "SaveFileDialogFragment"
        private const val ARG_LISTENER =  "arg-listener"
        fun newInstance(listener: SavingFileMp3) = SaveFileDialogFragment().apply {
            arguments = bundleOf(ARG_LISTENER to listener)
        }
    }

}