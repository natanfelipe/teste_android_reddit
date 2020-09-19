package com.fastnews.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.fastnews.common.NetworkState
import com.fastnews.repository.PostRepository
import com.fastnews.service.model.PostData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TimelineDataSource(private val scope: CoroutineScope): ItemKeyedDataSource<String, PostData>() {

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()
    val initialLoadNetworkState = MutableLiveData<NetworkState>()
    private val job = Job()

    private val initialLoadHandler = CoroutineExceptionHandler { _, _ ->
        initialLoadNetworkState.postValue(NetworkState.FAILED)
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        networkState.postValue(NetworkState.FAILED)
    }

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<PostData>
    ) {
        retry = {loadInitial(params, callback)}
        getPosts("", params.requestedLoadSize, callback)
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<PostData>) {
        retry = {loadAfter(params, callback)}
        getPosts(params.key, params.requestedLoadSize,  callback)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<PostData>) {}

    override fun getKey(item: PostData): String = item.name

    private fun getPosts(
        after: String,
        requestLoadSize: Int,
        callback: LoadCallback<PostData>
    ) {
        networkState.postValue(NetworkState.RUNNING)

        val exceptionHandler =
            if (after.isEmpty()) {
            initialLoadHandler
        }
        else {
            handler
        }

        scope.launch(exceptionHandler + job) {
            val response = PostRepository.getPosts(after, requestLoadSize)
            networkState.postValue(NetworkState.SUCCESS)
            callback.onResult(response)
        }
    }

    override fun invalidate() {
        super.invalidate()
        job.cancel()
    }

}