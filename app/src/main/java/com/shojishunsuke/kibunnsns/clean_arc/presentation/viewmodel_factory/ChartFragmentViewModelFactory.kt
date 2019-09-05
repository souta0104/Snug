package com.shojishunsuke.kibunnsns.clean_arc.presentation.viewmodel_factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shojishunsuke.kibunnsns.clean_arc.presentation.ChartFragmentViewModel

class ChartFragmentViewModelFactory(private val context: Context):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChartFragmentViewModel(context) as T
    }
}