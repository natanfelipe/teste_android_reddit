package com.fastnews.service.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class PostResponse(val data: PostDataChild)

data class PostDataChild(val children: List<PostChildren>)

data class PostChildren(val data: PostData)

@Parcelize
data class PostData(
    val id: String,
    val author: String = "",
    val thumbnail: String = "",
    val name: String = "",
    val num_comments: Int,
    val score: Int,
    val title: String = "",
    val created_utc: Long,
    val url: String,
    val preview: Preview
) : Parcelable

@Parcelize
data class Preview(val images: List<PreviewImage>) : Parcelable

@Parcelize
data class PreviewImage(val id: String, val source: PreviewImageSource) : Parcelable

@Parcelize
data class PreviewImageSource(val url: String, val width: Int, val height: Int) : Parcelable
