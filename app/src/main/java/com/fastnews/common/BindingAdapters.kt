package com.fastnews.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.fastnews.mechanism.TimeElapsed

@BindingAdapter("app:image")
fun loadImage(imageView: ImageView, url: String?) {
    url?.let {
        if (!TextUtils.isEmpty(it) && it.startsWith(PREFIX_HTTP)) {
            Glide.with(imageView.context)
                .load(it)
                .placeholder(ColorDrawable(Color.LTGRAY))
                .error(ColorDrawable(Color.DKGRAY))
                .into(imageView)
            imageView.visible()
        } else {
            imageView.gone()
        }
    }
}

@BindingAdapter("app:populateTime")
fun populateTime(textView: TextView, createdUtc: Long?) {
    createdUtc?.let {
        textView.text = TimeElapsed.getTimeElapsed(createdUtc)
    }
}