package com.kotlin.mvvm

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
  /*  @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.example.mycode_lm_ng", appContext.packageName)
    }*/

    /**
     * Test that the list is long enough for this sample, the last item shouldn't appear.
     */
    @Test
    fun lastItem_NotDisplayed() {
        // Last item should not exist if the list wasn't scrolled down.
        onView(withText("Deliver food to Eric")).check(doesNotExist())
    }
}
