package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.holv.apps.recordvoiceapp.R
import com.holv.apps.recordvoiceapp.databinding.AskDeleteFileDialogFragmentBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.pojos.RecordAudio

class DeleteMp3FileDialogFragment: BaseBindingDialogFragment<AskDeleteFileDialogFragmentBinding>() {

    private var listenerDeleteMp3File : DialogOnDelete? =  null
    private val adapterPosition: Int by lazy { arguments?.getInt(ARG_POSITION) ?: 0 }
    private val recordAudio: RecordAudio? by lazy { arguments?.getParcelable(ARG_RECORD_AUDIO)}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listenerDeleteMp3File = parentFragment as? DialogOnDelete
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        AskDeleteFileDialogFragmentBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            recordAudio?.let { audio ->
                val originalTextSize = resources.getString(R.string.save_file_name).length - ARG_FOR_STR_FORMAT
                val modifiedTextSize = String.format(resources.getString(R.string.save_file_name), audio.name).length
                tvFileName.text = String.format(resources.getString(R.string.save_file_name), audio.name)
                val spannable = SpannableString(tvFileName.text).apply {
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.orange)), originalTextSize, modifiedTextSize, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                }
                tvFileName.text = spannable
                btnSave.setOnClickListener {
                    listenerDeleteMp3File?.onDeleteRecording(adapterPosition, audio)
                    dismiss()
                }
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(recordAudio: RecordAudio, adapterPosition: Int) = DeleteMp3FileDialogFragment().apply {
            arguments = bundleOf(
                ARG_RECORD_AUDIO to recordAudio,
                ARG_POSITION to adapterPosition
            )
        }
        private const val ARG_RECORD_AUDIO = "arg-record-audio"
        private const val ARG_POSITION = "arg-adapter-pos"
        const val TAG = "DeleteMp3FileDialogFragment"
        private const val ARG_FOR_STR_FORMAT = 5
    }

}