package ru.hse.electivehw2

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Matcher
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import ru.hse.electivehw2.presentation.MainActivity
import java.util.*
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
@MediumTest
class BarcodeInstrumentedTest {
    private var activityScenario: ActivityScenario<MainActivity>? = null
    private var handler: BarcodeFailureHandler? = null
    private var uiDevice: UiDevice? = null

    private val desiredUrl = "https://github.com/android/architecture-components-samples/"
    private var linksFound = 0

    private lateinit var appContext: Context
    private lateinit var mInstrumentation: Instrumentation
    private lateinit var barcodeScanner: BarcodeScannerOptions

    @Before
    fun setUp() {
        mInstrumentation = InstrumentationRegistry.getInstrumentation()
        handler = BarcodeFailureHandler(mInstrumentation)
        Espresso.setFailureHandler(handler)

        uiDevice = UiDevice.getInstance(mInstrumentation)
        uiDevice?.pressHome()

        val nonLocalizedContext = mInstrumentation.targetContext
        val configuration = nonLocalizedContext.resources.configuration
        configuration.setLocale(Locale.UK)
        appContext = nonLocalizedContext.createConfigurationContext(configuration)

        barcodeScanner = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            )
            .build()

        val intent = Intent(appContext, MainActivity::class.java)
        activityScenario = ActivityScenario.launch(intent)
    }

    @Test(timeout = MAX_TIMEOUT)
    fun checkQRIsShown() {
        checkImageStep()
    }

    private fun checkImageStep() = runBlocking {
        val imageFilterViewBitmap = onView(
            instanceOf(ImageFilterView::class.java)
        ).captureToBitmap()

        val imageFromView = InputImage.fromBitmap(imageFilterViewBitmap, 0)
        val scanner = BarcodeScanning.getClient()
        val resultFromView = scanner.process(imageFromView)
        delay(THREAD_DELAY)
        resultFromView.result.forEach { barcode ->
            handler?.appendExtraMessage("Barcode of type ${barcode.valueType}")
            when (barcode.valueType) {
                Barcode.TYPE_URL -> {
                    barcode.url?.url?.let { url ->
                        Log.d(TAG, url)
                        validateRecognizedUrl(url)
                    }
                }
            }
        }
        assertEquals("Total number of links: $linksFound.", 1, linksFound)
    }

    private fun validateRecognizedUrl(url: String) {
        assertEquals(desiredUrl, url)
        linksFound++
    }

    companion object {
        private const val APP_NAME = "Image QR"
        private const val THREAD_DELAY: Long = 8_900
        private const val MAX_TIMEOUT: Long = 13_000
        private const val TAG = "BarcodeImageTest"

        @BeforeClass
        @JvmStatic
        fun enableAccessibilityChecks() {
            AccessibilityChecks.enable()
            IdlingPolicies.setMasterPolicyTimeout(10, TimeUnit.SECONDS)
            IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.SECONDS)
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            val mInstrumentation = InstrumentationRegistry.getInstrumentation()
            val uiDevice = UiDevice.getInstance(mInstrumentation)
            uiDevice.pressHome()
        }
    }
}

class BarcodeFailureHandler(instrumentation: Instrumentation) : FailureHandler {
    private var extraMessage = StringBuilder("")
    private var delegate: DefaultFailureHandler = DefaultFailureHandler(instrumentation.targetContext)

    override fun handle(error: Throwable?, viewMatcher: Matcher<View>?) {
        if (error != null) {
            val newError = Throwable(
                "$extraMessage ${error.message}",
                error.cause,
            )
            delegate.handle(newError, viewMatcher)
        }
    }

    fun appendExtraMessage(text: String) {
        extraMessage = extraMessage.append(text)
    }
}
