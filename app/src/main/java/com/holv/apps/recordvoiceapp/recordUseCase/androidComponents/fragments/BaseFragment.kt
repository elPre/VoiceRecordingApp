package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.holv.apps.recordvoiceapp.R

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: ViewBinding? = null
    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    protected fun showSnackbar(msg: String, canDismiss: Boolean = true) {
        view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_LONG) }
            ?.apply {
                setTextColor(requireContext().getColor(R.color.black))
                view.apply {
                    background =
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.snackbar_rounded_rectangle
                        )
                    (view.findViewById<View?>(R.id.snackbar_text) as? TextView?)?.isSingleLine = false
                    behavior = object : BaseTransientBottomBar.Behavior() {
                        override fun canSwipeDismissView(child: View): Boolean = canDismiss
                    }
                }
                show()
            }
    }

    protected open fun VB.clearViews() = Unit
}