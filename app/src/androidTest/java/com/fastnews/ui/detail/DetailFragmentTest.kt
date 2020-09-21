package com.fastnews.ui.detail

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.fastnews.R
import com.fastnews.common.waitUntilViewIsDisplayed
import com.fastnews.ui.SchemeActivity
import com.fastnews.ui.timeline.viewholders.TimelineItemViewHolder
import com.fastnews.util.EspressoIdlingResources
import org.junit.After
import org.junit.Before
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.SmallTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@SmallTest
class DetailFragmentTest {

    @get: Rule
    val activityRule = ActivityScenarioRule(SchemeActivity::class.java)
    private val LIST_ITEM_CLICKED = 1

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResources.countingIdlingResource)
    }

    @Test
    fun share_a_post_item() {
        onView(withId(R.id.state_progress_timeline))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        waitUntilViewIsDisplayed(withId(R.id.timeline_rv))
        onView(withId(R.id.timeline_rv))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.timeline_rv)).perform(
            RecyclerViewActions.actionOnItemAtPosition<TimelineItemViewHolder>(
                LIST_ITEM_CLICKED,
                click()
            )
        )

        onView(withId(R.id.bt_share)).perform(click())
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResources.countingIdlingResource)
    }
}