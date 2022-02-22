package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.activities

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.holv.apps.recordvoiceapp.R
import com.holv.apps.recordvoiceapp.databinding.ActivityMainBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments.RecordFragment


class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding = ActivityMainBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            openFragment(RecordFragment.newInstance(),RecordFragment.TAG)
        }
    }

    private fun openFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, fragment)
            .addToBackStack(tag).commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == ONE_FRAGMENT
            && supportFragmentManager.fragments[0] is RecordFragment)
            finish()
        else
            super.onBackPressed()
    }

    companion object {
        private const val ONE_FRAGMENT = 1
    }
}

