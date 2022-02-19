package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.*
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.pojos.RecordAudio
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.pojos.RecordFileAction
import kotlinx.coroutines.launch

class RecordViewModel(val app: Application) : ViewModel() {

    val moduleItem = MutableLiveData<List<RecordItem>>()

    fun loadPage(isNewFragment: Boolean) = viewModelScope.launch {
        if (isNewFragment) {
            load()
        }
    }

    private fun load() {
        val list = mutableListOf<RecordItem>()
        list.add(TopBanner)
        list.add(LogoAnimation)
        list.add(UserControls)
        list.add(LegendRecordings)
        val listRecordings = mutableListOf<Records>()
        for (i in 0 until 10) {
            listRecordings.add(
                Records(
                    RecordAudio(
                        name = "Amazing time at Bungalow $i let addd more text to see what happens",
                        time = "Fri Feb 18 2022",
                        duration = "$i : $i mins",
                        RecordFileAction.NO_SELECTION
                    )
                )
            )
        }
        list.addAll(listRecordings)
        moduleItem.postValue(list.toList())
    }


    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RecordViewModel(app) as T
        }
    }
}