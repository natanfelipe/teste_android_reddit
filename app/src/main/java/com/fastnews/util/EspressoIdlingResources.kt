package com.fastnews.util

import android.util.Log
import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResources {

    const val RESOURCE = "GLOBAL"
    @JvmField val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if(!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}