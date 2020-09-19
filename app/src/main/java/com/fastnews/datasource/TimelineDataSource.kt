package com.fastnews.datasource

import androidx.paging.ItemKeyedDataSource
import com.fastnews.repository.PostRepository
import com.fastnews.service.model.PostData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TimelineDataSource(private val scope: CoroutineScope): ItemKeyedDataSource<String, PostData>() {

    //var loadState: MutableLiveData<NetworkState> = MutableLiveData()

    private val job = Job()


    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<PostData>
    ) {
        getPosts("", params.requestedLoadSize, callback)
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<PostData>) {
        getPosts(params.key, params.requestedLoadSize,  callback)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<PostData>) {}

    override fun getKey(item: PostData): String = item.name

    private fun getPosts(
        after: String,
        requestLoadSize: Int,
        callback: LoadCallback<PostData>
    ) {
        scope.launch(job) {
            val response = PostRepository.getPosts(after, requestLoadSize)
            callback.onResult(response)
        }
    }

    override fun invalidate() {
        super.invalidate()
        job.cancel()
    }

}