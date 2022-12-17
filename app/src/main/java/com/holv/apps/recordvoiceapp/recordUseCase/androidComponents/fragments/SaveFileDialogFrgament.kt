package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.holv.apps.recordvoiceapp.databinding.AskSaveFileDialogFragmentBinding

class SaveFileDialogFragment : BaseBindingDialogFragment<AskSaveFileDialogFragmentBinding>(),
    DialogFragmentListeners {

    private var listenerSaveFile : SavingFileMp3? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listenerSaveFile = parentFragment as? SavingFileMp3
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        AskSaveFileDialogFragmentBinding.inflate(inflater, container,  false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnSave.setOnClickListener {
                btnSave.isEnabled = false
                if (etSaveFileName.text.toString().isNotBlank()) {
                    pbSaveMp3.isVisible = true
                    etSaveFileName.isEnabled = false
                    btnCancel.isEnabled = false
                    listenerSaveFile?.onSaveFile(binding.etSaveFileName.text.toString(), this@SaveFileDialogFragment)
                } else {
                    btnSave.isEnabled = true
                }
            }

            btnCancel.setOnClickListener {
                listenerSaveFile?.onCancelSave()
                dismiss()
            }
        }
    }


    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        //onCancel do no save file the same  as cancel  or keep the  file
        listenerSaveFile?.onCancelSave()
    }

    override fun onConversionSuccess() {
        dismiss()
    }

    override fun onConversionFailed() {
        //show alert or snack acknowledge the user that the apps does not work on  that phone
        dismiss()
    }

    override fun onSetProgressDone(progress: Int) {
        activity?.runOnUiThread {
            binding.pbSaveMp3.progress = progress
        }
    }

    companion object {
        const val TAG = "SaveFileDialogFragment"
        fun newInstance() = SaveFileDialogFragment()
    }

}