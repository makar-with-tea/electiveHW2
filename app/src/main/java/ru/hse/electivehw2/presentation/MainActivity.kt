package ru.hse.electivehw2.presentation

import android.os.Bundle
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
                imageView.setImageDrawable(drawable)
                progressBar.visibility = ProgressBar.GONE
            } else if (viewModel.errorMessage.value == null) {
                progressBar.visibility = ProgressBar.VISIBLE
                viewModel.loadImage(imageUrl, CoroutineScope(Dispatchers.IO))
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                errorTextView.text = errorMessage
                progressBar.visibility = ProgressBar.GONE
            }
        }

        if (savedInstanceState == null) {
            progressBar.visibility = ProgressBar.VISIBLE
            viewModel.loadImage(imageUrl, CoroutineScope(Dispatchers.IO))
        }
    }
}
