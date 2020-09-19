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

    private val pagedListConfig by lazy {
        PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE*2)
            .setEnablePlaceholders(false)
            .build()
    }

    var posts: LiveData<PagedList<PostData>>

    private val timelineDataSourceFactory by lazy {
        TimelineDataSourceFactory(ioScope)
    }

    val initialLoadState: LiveData<NetworkState> = Transformations.switchMap(
        timelineDataSourceFactory.postLiveData
    ) {
        it.initialLoadNetworkState
    }

    val networkState: LiveData<NetworkState> = Transformations.switchMap(
        timelineDataSourceFactory.postLiveData
    ) {
        it.networkState
    }

    init {
        posts = LivePagedListBuilder(timelineDataSourceFactory, pagedListConfig).build()
    }

    fun retry() {
        timelineDataSourceFactory.getPostLiveDataValue()?.retry?.invoke()
    }

    fun reloadData() {
        timelineDataSourceFactory.getPostLiveDataValue()?.invalidate()
    }

    override fun onCleared() {
        super.onCleared()
        ioScope.coroutineContext.cancel()
    }
}