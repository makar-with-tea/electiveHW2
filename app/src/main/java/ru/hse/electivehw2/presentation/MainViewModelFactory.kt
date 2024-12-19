package ru.hse.electivehw2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.hse.electivehw2.domain.usecase.LoadImageUseCase

class MainViewModelFactory(private val loadImageUseCase: LoadImageUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(loadImageUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
