package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.ads.MobileAds
import com.holv.apps.recordvoiceapp.R
import com.holv.apps.recordvoiceapp.databinding.ActivityMainBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments.RecordFragment


class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding = ActivityMainBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            permissions,
            REQUEST_RECORD_AUDIO_PERMISSION
        )
        Log.d(TAG,"getting the intent ${intent.action}")
        if (savedInstanceState == null && permissionToRecordAccepted) {
            Log.d(TAG,"brand new fragment")
            Log.d(TAG,"here ask if the fragment exist put that  one otherwise  create a new  one")
            openFragment(RecordFragment.newInstance(),RecordFragment.TAG)
        }
    }

    override fun onStart() {
        super.onStart()
        // Initialize the Mobile Ads SDK with an AdMob App ID.
        MobileAds.initialize(this) {}
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) {
            Log.d("MainActivity", "Show a snackbar of a dialog fragment ")
            finish()
        } else {
            openFragment(RecordFragment.newInstance(),RecordFragment.TAG)
        }
    }

    companion object {
        const val TAG = "MainActivity"
        private const val ONE_FRAGMENT = 1
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }
}

