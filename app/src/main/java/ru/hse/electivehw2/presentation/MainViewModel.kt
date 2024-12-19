package ru.hse.electivehw2.presentation

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.hse.electivehw2.domain.usecase.LoadImageUseCase

class MainViewModel(private val loadImageUseCase: LoadImageUseCase) : ViewModel() {
    private val _imageDrawable = MutableLiveData<Drawable?>()
    val imageDrawable: LiveData<Drawable?> get() = _imageDrawable

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun loadImage(url: String, context: CoroutineScope) {
        context.launch {
            val result = loadImageUseCase.execute(url)
            if (result.isFailure) {
                Log.d("MainViewModel", "Error loading image", result.exceptionOrNull())
                _errorMessage.postValue(result.exceptionOrNull()?.message ?: "Неизвестная ошибка")
                return@launch
            }
            _imageDrawable.postValue(result.getOrNull())
            _errorMessage.postValue(null)
        }
    }
}
