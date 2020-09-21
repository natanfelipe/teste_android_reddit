package com.fastnews.viewmodel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastnews.common.NetworkState
import com.fastnews.common.PAGE_SIZE
import com.fastnews.datasource.TimelineDataSourceFactory
import com.fastnews.service.model.PostData
import kotlinx.coroutines.cancel

class PostViewModel : ViewModel() {

    private val ioScope = viewModelScope
    private val timelineDataSourceFactory by lazy {
        TimelineDataSourceFactory(ioScope)
    }
    private val pagedListConfig by lazy {
        PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE*2)
            .setEnablePlaceholders(false)
            .build()
    }

    var posts: LiveData<PagedList<PostData>>

    val initialLoadState: LiveData<NetworkState> = Transformations.switchMap(
        timelineDataSourceFactory.postLiveData
    ) {
        it.getInitialLoadNetworkState()
    }

    val networkState: LiveData<NetworkState> = Transformations.switchMap(
        timelineDataSourceFactory.postLiveData
    ) {
        it.getCurrentNetworkState()
    }

    init {
        posts = LivePagedListBuilder(timelineDataSourceFactory, pagedListConfig).build()
    }

    fun retry() {
        timelineDataSourceFactory.getPostLiveDataValue()?.doRetry()
    }

    fun reloadData() {
        timelineDataSourceFactory.getPostLiveDataValue()?.refreshAll()
    }

    override fun onCleared() {
        super.onCleared()
        ioScope.coroutineContext.cancel()
    }
}