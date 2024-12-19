package ru.hse.electivehw2.presentation

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ru.hse.electivehw2.R
import ru.hse.electivehw2.data.ImageRepositoryImpl
import ru.hse.electivehw2.domain.usecase.LoadImageUseCase

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageUrl = getString(R.string.example_image_url)
        val imageView: ImageFilterView = findViewById(R.id.imageFilterView)
        val errorTextView: TextView = findViewById(R.id.errorTextView)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        val repository = ImageRepositoryImpl(this)
        val loadImageUseCase = LoadImageUseCase(repository)
        viewModel = ViewModelProvider(this, MainViewModelFactory(loadImageUseCase)).get(MainViewModel::class.java)

        viewModel.imageDrawable.observe(this) { drawable ->
            if (drawable != null) {
                Log.d("MainActivity", "Image loaded")
                imageView.visibility = ImageFilterView.VISIBLE
                imageView.setImageDrawable(drawable)
                progressBar.visibility = ProgressBar.GONE
                errorTextView.visibility = TextView.GONE
            } else if (viewModel.errorMessage.value == null) {
                Log.d("MainActivity", "Loading image")
                progressBar.visibility = ProgressBar.VISIBLE
                viewModel.loadImage(imageUrl, CoroutineScope(Dispatchers.IO))
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Log.d("MainActivity", "Error: $errorMessage")
                errorTextView.text = errorMessage
                errorTextView.visibility = TextView.VISIBLE
                progressBar.visibility = ProgressBar.GONE
                imageView.visibility = ImageFilterView.GONE
            } else {
                Log.d("MainActivity", "No error")
                errorTextView.visibility = TextView.GONE
                imageView.visibility = ImageFilterView.VISIBLE
            }
        }

        if (savedInstanceState == null) {
            viewModel.loadImage(imageUrl, CoroutineScope(Dispatchers.IO))
        }
    }
}
