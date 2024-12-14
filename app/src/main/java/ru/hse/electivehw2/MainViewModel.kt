package ru.hse.electivehw2

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _imageDrawable = MutableLiveData<Drawable?>()
    val imageDrawable: LiveData<Drawable?> get() = _imageDrawable

    fun setImageDrawable(drawable: Drawable?) {
        _imageDrawable.value = drawable
    }
}