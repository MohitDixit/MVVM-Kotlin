package com.kotlin.mvvm.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class MainActivityViewModelFactory @Inject constructor(
    private val mainActivityViewModel: MainActivityViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return mainActivityViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}