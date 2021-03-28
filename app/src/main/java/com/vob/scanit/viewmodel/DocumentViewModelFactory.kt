package com.vob.scanit.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/*DocumentViewModelFactory is an utility class that provides ViewModels for a scope*/
class DocumentViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DocumentFragmentViewModel() as T
    }
}