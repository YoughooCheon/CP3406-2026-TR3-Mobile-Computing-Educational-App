package com.example.cp3406_assessment3_younghoocheon.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.AppContainer
import com.example.cp3406_assessment3_younghoocheon.appContainer

/**
 * Creates (or finds) the ViewModel of the current screen. [create] receives the [AppContainer],
 * so a screen only says which dependencies its ViewModel needs and the factory code is written once.
 */
@Composable
inline fun <reified VM : ViewModel> appViewModel(crossinline create: (AppContainer) -> VM): VM {
    val container = appContainer()
    return viewModel(factory = viewModelFactory { initializer { create(container) } })
}
