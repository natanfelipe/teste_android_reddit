package com.fastnews.ui.web

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.fastnews.R

class CustomTabsWeb(private val context: Context, private val url: String) {

    fun openUrlWithCustomTabs() {
        val intentBuilder = CustomTabsIntent.Builder()

        intentBuilder.apply {
            setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
            setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            setStartAnimations(context, R.anim.pull_in_right, R.anim.push_out_left)
            setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        val customTabsIntent = intentBuilder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

}
