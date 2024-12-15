package ru.hse.electivehw2.domain.usecase

import android.graphics.drawable.Drawable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import ru.hse.electivehw2.domain.ImageRepository
import kotlin.Result
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class LoadImageUseCaseTest {

    @Mock
    private lateinit var repository: ImageRepository

    private lateinit var useCase: LoadImageUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = LoadImageUseCase(repository)
    }

    @Test
    fun `execute success returns drawable`() = runTest {
        val drawable = mock(Drawable::class.java)
        `when`(repository.loadImage(anyString())).thenReturn(Result.success(drawable))

        val result = useCase.execute("https://github.com/makar-with-tea/electiveHW2/blob/develop/exampledata/cat_and_frog.jpg?raw=true")

        assertEquals(Result.success(drawable), result)
    }

    @Test
    fun `execute failure returns exception`() = runTest {
        val exception = Exception("Failed to load image")
        `when`(repository.loadImage(anyString())).thenReturn(Result.failure(exception))

        val result = useCase.execute("fake_url")

        assertEquals(Result.failure<Drawable>(exception), result)
    }
}