package ru.hse.electivehw2.data

import android.content.Context
import android.graphics.drawable.Drawable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import kotlin.Result
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class ImageRepositoryImplTest {

    private lateinit var repository: ImageRepositoryImpl
    private lateinit var context: Context


    @Before
    fun setUp() {
        context = mock(Context::class.java)
        repository = ImageRepositoryImpl(context)
    }

    @Test
    fun `loadImage success returns drawable`() = runTest {
        val drawable = mock(Drawable::class.java)
        // Mocking the actual image loading logic
        repository = spy(repository)
        doReturn(Result.success(drawable)).`when`(repository).loadImage(anyString())

        val result = repository.loadImage("https://github.com/makar-with-tea/electiveHW2/blob/develop/exampledata/cat_and_frog.jpg?raw=true")

        assertEquals(Result.success(drawable), result)
    }

    @Test
    fun `loadImage failure returns exception`() = runTest {
        val exception = Exception("Failed to load image")
        // Mocking the actual image loading logic
        repository = spy(repository)
        doReturn(Result.failure<Drawable>(exception)).`when`(repository).loadImage(anyString())

        val result = repository.loadImage("fake_url")

        assertEquals(Result.failure<Drawable>(exception), result)
    }
}