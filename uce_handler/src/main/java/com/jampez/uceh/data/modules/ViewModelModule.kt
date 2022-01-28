package com.jampez.uceh.data.modules

import com.jampez.uceh.features.uce.UCEViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { UCEViewModel(get()) }
}