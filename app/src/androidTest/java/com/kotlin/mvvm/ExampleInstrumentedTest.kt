package com.kotlin.mvvm

import androidx.test.espresso.Espresso.onView

import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches

import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.runner.AndroidJUnit4
//import androidx.test.espresso.contrib.RecyclerViewActions

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

    private val ITEM_BELOW_THE_FOLD = 40

    /**
     * Test that the list is long enough for this sample, the last item shouldn't appear.
     */
    @Test
    fun lastItem_NotDisplayed() {
        // Last item should not exist if the list wasn't scrolled down.
        onView(withText("Deliver food to Eric")).check(doesNotExist())
    }

    @Test
    fun scrollToItemBelowFold_checkItsText() {
        // First scroll to the position that needs to be matched and click on it.
       /* onView(ViewMatchers.withId(R.id.orderListView))
            .perform(RecyclerViewActions.actionOnItemAtPosition(ITEM_BELOW_THE_FOLD, click()))*/

        // Match the text in an item below the fold and check that it's displayed.
        val itemElementText = "Deliver food to Eric"/*getApplicationContext().getResources().getString(
            R.string.item_element_text*/
         // ITEM_BELOW_THE_FOLD.toString()
        onView(withText(itemElementText)).check(matches(isDisplayed()))
    }
}
