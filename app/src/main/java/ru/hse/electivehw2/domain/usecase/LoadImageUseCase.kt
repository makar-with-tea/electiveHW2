package ru.hse.electivehw2.domain.usecase

import android.graphics.drawable.Drawable
import ru.hse.electivehw2.domain.ImageRepository

class LoadImageUseCase(private val repository: ImageRepository) {
    suspend fun execute(url: String): Result<Drawable> {
        return repository.loadImage(url)
    }
}