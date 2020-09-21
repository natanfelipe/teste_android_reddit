package com.fastnews.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.fastnews.common.NetworkState
import com.fastnews.repository.PostRepository
import com.fastnews.service.model.PostData
import kotlinx.coroutines.*

class TimelineDataSource(private val scope: CoroutineScope) :
    ItemKeyedDataSource<String, PostData>() {

    private val currentNetworkState = MutableLiveData<NetworkState>()
    private val initialLoadNetworkState = MutableLiveData<NetworkState>()

    private val job = SupervisorJob()

    private val initialLoadHandler = CoroutineExceptionHandler { _, _ ->
        initialLoadNetworkState.postValue(NetworkState.FAILED)
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        currentNetworkState.postValue(NetworkState.FAILED)
    }

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<PostData>
    ) {
        retry = { loadInitial(params, callback) }
        getPosts("", params.requestedLoadSize, callback, initialLoadNetworkState)
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<PostData>) {
        retry = { loadAfter(params, callback) }
        getPosts(params.key, params.requestedLoadSize, callback, currentNetworkState)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<PostData>) {}

    override fun getKey(item: PostData): String = item.name

    private fun getPosts(
        after: String,
        requestLoadSize: Int,
        callback: LoadCallback<PostData>,
        networkState: MutableLiveData<NetworkState>
    ) {
        val exceptionHandler =
            if (after.isEmpty()) {
                initialLoadHandler
            } else {
                handler
            }

        networkState.postValue(NetworkState.RUNNING)

        scope.launch(exceptionHandler + job) {
            val response = PostRepository.getPosts(after, requestLoadSize)
            networkState.postValue(NetworkState.SUCCESS)
            callback.onResult(response)
        }
    }

    fun getInitialLoadNetworkState(): LiveData<NetworkState> = initialLoadNetworkState

    fun getCurrentNetworkState(): LiveData<NetworkState> = currentNetworkState

    fun refreshAll() = this.invalidate()

    fun doRetry() { retry?.invoke() }

    override fun invalidate() {
        super.invalidate()
        job.cancel()
    }

}