package com.kotlin.mvvm

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.kotlin.mvvm.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.rule.ActivityTestRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.action.ViewActions.swipeDown
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.mvvm.api.model.OrderData
import com.kotlin.mvvm.ui.OrderAdapter
import com.kotlin.mvvm.util.Utils



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
        val itemCount = recyclerView.adapter!!.itemCount

        onView(withId(R.id.orderListView))
            .perform(RecyclerViewActions.scrollToPosition<OrderAdapter.ItemViewHolder>(itemCount - 1))
    }

    @Test
    fun recyclerViewListClick_DataCorrect_success() {

        onView(withId(R.id.orderListView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<OrderAdapter.ItemViewHolder>(0, click()))

        val listType = object : TypeToken<List<OrderData>>() {
        }.type

        val orderList: List<OrderData> = Gson().fromJson(Utils.loadJSONFromAssets(), listType)

        onView(withId(R.id.order_description)).check(matches(ViewMatchers.withText(orderList[0].description + BuildConfig.at_str + orderList[0].location?.address)))
    }

    @Test
    fun pullToRefresh_success(){
        onView(withId(R.id.swipe_container)).perform(swipeDown())
    }
}