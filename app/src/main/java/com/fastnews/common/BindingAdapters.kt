package com.fastnews.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.fastnews.mechanism.TimeElapsed
import com.fastnews.service.model.PreviewImage

@BindingAdapter("app:image")
fun loadImage(imageView: ImageView, images: List<PreviewImage>?) {
    var url = ""

    images?.let { list ->
        list.map { previewImage ->
            if (!TextUtils.isEmpty(previewImage.source.url)) {
                url = HtmlCompat.fromHtml(previewImage.source.url, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        .toString()
            }
        }
    }

    if (!TextUtils.isEmpty(url) && url.startsWith(PREFIX_HTTP)) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(ColorDrawable(Color.LTGRAY))
            .error(ColorDrawable(Color.DKGRAY))
            .into(imageView)
        imageView.visible()
    } else {
        imageView.gone()
    }
}

@BindingAdapter("app:populateTime")
fun populateTime(textView: TextView, createdUtc: Long?) {
    createdUtc?.let {
        textView.text = TimeElapsed.getTimeElapsed(createdUtc)
    }
}