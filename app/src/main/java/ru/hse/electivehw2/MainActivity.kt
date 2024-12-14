package ru.hse.electivehw2

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.material.imageview.ShapeableImageView

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView: ShapeableImageView = findViewById(R.id.imageFilterView)
        val errorTextView: TextView = findViewById(R.id.errorTextView)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        viewModel.imageDrawable.observe(this) { drawable ->
            if (drawable != null) {
                imageView.setImageDrawable(drawable)
                progressBar.visibility = ProgressBar.GONE
                errorTextView.visibility = TextView.GONE
            } else {
                loadImage(imageView, errorTextView, progressBar)
            }
        }
    }

    private fun loadImage(imageView: ShapeableImageView, errorTextView: TextView, progressBar: ProgressBar) {
        val imageUrl = getString(R.string.lavender_image_url)
        val imageLoader = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(imageUrl)
            .target(
                onStart = {
                    progressBar.visibility = ProgressBar.VISIBLE
                    errorTextView.visibility = TextView.GONE
                },
                onSuccess = { drawable ->
                    imageView.setImageDrawable(drawable)
                    progressBar.visibility = ProgressBar.GONE
                    errorTextView.visibility = TextView.GONE
                    viewModel.setImageDrawable(drawable)
                },
                onError = { errorDrawable ->
                    imageView.setImageDrawable(errorDrawable)
                    progressBar.visibility = ProgressBar.GONE
                    errorTextView.visibility = TextView.VISIBLE
                }
            )
            .listener(
                onError = { _, throwable ->
                    val errorMessage = throwable.throwable.localizedMessage ?: "Неизвестная ошибка"
                    Log.e("ImageRequestError", errorMessage)
                    errorTextView.text = errorMessage
                }
            )
            .build()
        imageLoader.enqueue(request)
    }
}