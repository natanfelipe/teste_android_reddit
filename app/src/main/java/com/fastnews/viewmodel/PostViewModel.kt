package com.fastnews.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastnews.common.PAGE_SIZE
import com.fastnews.datasource.TimelineDataSourceFactory
import com.fastnews.mechanism.Coroutines
import com.fastnews.repository.PostRepository
import com.fastnews.service.model.PostData

class PostViewModel : ViewModel() {

    private val ioScope = viewModelScope
    private var mutablePostListVisibility = MutableLiveData<Int>().apply { View.GONE }
    val postListVisibility: LiveData<Int>
        get() = mutablePostListVisibility
    private var mutableProgressVisibility = MutableLiveData<Int>().apply { View.VISIBLE }
    val progressVisibility: LiveData<Int>
        get() = mutableProgressVisibility
    private var mutableErrorMessageVisibility = MutableLiveData<Int>().apply { View.GONE }
    val errorMessageVisibility: LiveData<Int>
        get() = mutableErrorMessageVisibility


    private val pagedListConfig by lazy {
        PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE*2)
            .setEnablePlaceholders(false)
            .build()
    }

    lateinit var posts: LiveData<PagedList<PostData>>

    private val timelineDataSourceFactory by lazy {
        TimelineDataSourceFactory(ioScope)
    }

    fun loadData(hasInternetConnection: Boolean) {
        mutableProgressVisibility.value = View.VISIBLE

        if (hasInternetConnection) {

            posts = LivePagedListBuilder(timelineDataSourceFactory, pagedListConfig).build()
            mutableProgressVisibility.value = View.GONE
            mutablePostListVisibility.value = View.VISIBLE
        } else {
            mutableProgressVisibility.value = View.GONE
            mutableErrorMessageVisibility.value = View.VISIBLE
        }
    }
}