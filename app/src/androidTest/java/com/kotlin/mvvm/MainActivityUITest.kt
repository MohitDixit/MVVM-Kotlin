package com.kotlin.mvvm

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.kotlin.mvvm.ui.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.rule.ActivityTestRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.action.ViewActions.swipeDown
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.mvvm.model.OrderData2
import com.kotlin.mvvm.ui.adapter.OrderAdapter
import java.io.IOException


@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityUITest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun recyclerViewListClick_success() {
        onView(withId(R.id.orderListView))
            .perform(click())

    }

    @Test
    fun recyclerViewListScroll_success() {

        val recyclerView = activityRule.activity.findViewById<RecyclerView>(R.id.orderListView)
        val itemCount = recyclerView.adapter?.itemCount

        onView(withId(R.id.orderListView))
            .perform(RecyclerViewActions.scrollToPosition<OrderAdapter.ItemViewHolder>(20 - 1))
    }

    @Test
    fun recyclerViewListClick_DataCorrect_success() {

        onView(withId(R.id.orderListView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<OrderAdapter.ItemViewHolder>(0, click()))

        val listType = object : TypeToken<List<OrderData2>>() {
        }.type

        val orderList: List<OrderData2> = Gson().fromJson(loadJSONFromAssets(), listType)

        onView(withId(R.id.order_description)).check(matches(ViewMatchers.withText(orderList[0].description + " at "/*BuildConfig.at_str*/ + orderList[0].location?.address)))
    }

    @Test
    fun pullToRefresh_success(){
        onView(withId(R.id.swipe_container)).perform(swipeDown())
    }

    fun loadJSONFromAssets(): String? {
        var json: String? = null
        try {
            val classLoader = this.javaClass.classLoader
            val inputStream = classLoader?.getResourceAsStream("mockrepojson.json")
            val size = inputStream?.available()
            val buffer = size?.let { ByteArray(it) }
            inputStream?.read(buffer)
            inputStream?.close()

            json = buffer?.let { String(it, Charsets.UTF_8) }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return json
    }
}