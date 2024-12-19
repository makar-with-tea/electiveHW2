package ru.hse.electivehw2.data

import android.content.Context
import android.graphics.drawable.Drawable
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import ru.hse.electivehw2.domain.ImageRepository
import java.io.IOException

class ImageRepositoryImpl(private val context: Context) : ImageRepository {
    override suspend fun loadImage(url: String): Result<Drawable> {
        return try {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()
            val res = imageLoader.execute(request)
            if (res is SuccessResult) {
                Result.success(res.drawable)
            } else {
                Result.failure((res as ErrorResult).throwable)
            }
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }
}
