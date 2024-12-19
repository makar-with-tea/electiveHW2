package ru.hse.electivehw2.domain

import android.graphics.drawable.Drawable

interface ImageRepository {
    suspend fun loadImage(url: String): Result<Drawable>
}
