package com.fastnews.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fastnews.mechanism.Coroutines
import com.fastnews.repository.PostRepository
import com.fastnews.service.model.PostData

class PostViewModel : ViewModel() {

    private var mutablePostListVisibility = MutableLiveData<Int>().apply { View.GONE }
    val postListVisibility: LiveData<Int>
        get() = mutablePostListVisibility
    private var mutableProgressVisibility = MutableLiveData<Int>().apply { View.VISIBLE }
    val progressVisibility: LiveData<Int>
        get() = mutableProgressVisibility
    private var mutableErrorMessageVisibility = MutableLiveData<Int>().apply { View.GONE }
    val errorMessageVisibility: LiveData<Int>
        get() = mutableErrorMessageVisibility

    private lateinit var posts: MutableLiveData<List<PostData>>

    fun init(hasInternetConnection: Boolean): Boolean {
        mutableProgressVisibility.postValue(View.VISIBLE)

        return if (hasInternetConnection) {
            mutableProgressVisibility.postValue(View.GONE)
            mutablePostListVisibility.postValue(View.VISIBLE)
            true
        } else {
            mutableProgressVisibility.postValue(View.GONE)
            mutableErrorMessageVisibility.postValue(View.VISIBLE)
            false
        }
    }

    fun getPosts(after: String, limit: Int): LiveData<List<PostData>> {
        if (!::posts.isInitialized) {
            posts = MutableLiveData()

            Coroutines.ioThenMain({
                PostRepository.getPosts(after, limit)
            }) {
                posts.postValue(it)
            }
        }
        return posts
    }

}