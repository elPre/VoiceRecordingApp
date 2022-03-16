package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.holv.apps.recordvoiceapp.R

abstract class BaseBindingDialogFragment<VB : ViewBinding> : DialogFragment() {

    private var _binding: ViewBinding? = null
    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppDialogTheme)
    }

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflateBinding(inflater, container).let {
            _binding = it
            it.root
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.clearViews()
        _binding = null
    }

    protected open fun VB.clearViews() = Unit
}