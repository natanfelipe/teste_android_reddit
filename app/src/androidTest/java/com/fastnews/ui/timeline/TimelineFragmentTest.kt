package com.fastnews.ui.timeline

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.fastnews.R
import com.fastnews.ui.SchemeActivity
import com.fastnews.ui.timeline.viewholders.TimelineItemViewHolder
import com.fastnews.util.EspressoIdlingResources
import com.fastnews.common.waitUntilViewIsDisplayed
import org.hamcrest.Matchers.not
import androidx.test.espresso.action.ViewActions.click
import androidx.test.filters.MediumTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@MediumTest
class TimelineFragmentTest {

    @get: Rule
    val activityRule = ActivityScenarioRule(SchemeActivity::class.java)
    private val LIST_ITEM_CLICKED = 1
    private val MIN_ITEM_COUNT = 1
    private val IS_EMPTY = ""

    @Before
    fun setUp(){
        IdlingRegistry.getInstance().register(EspressoIdlingResources.countingIdlingResource)
    }

    @Test
    fun is_timeline_list_with_at_least_one_articles() {
        onView(withId(R.id.state_progress_timeline)).check(matches(isDisplayed()))
        waitUntilViewIsDisplayed(withId(R.id.timeline_rv))
        onView(withId(R.id.timeline_rv)).check(matches(isDisplayed()))
        onView(withId(R.id.timeline_rv)).check(matches(hasMinimumChildCount(MIN_ITEM_COUNT)))
    }

    @Test
    fun select_timeline_list_item_displays_article_details_with_title_not_blank() {
        onView(withId(R.id.state_progress_timeline)).check(matches(isDisplayed()))
        waitUntilViewIsDisplayed(withId(R.id.timeline_rv))
        onView(withId(R.id.timeline_rv)).check(matches(isDisplayed()))
        onView(withId(R.id.timeline_rv)).perform(
            actionOnItemAtPosition<TimelineItemViewHolder>(
                LIST_ITEM_CLICKED,
                click()
            )
        )

        onView(withId(R.id.item_detail_post_title)).check(matches(not(withText(IS_EMPTY))))
    }


    @After
    fun tearDown(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResources.countingIdlingResource)
    }
}