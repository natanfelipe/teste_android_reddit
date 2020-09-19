package com.fastnews.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.fastnews.service.model.PostData
import kotlinx.coroutines.CoroutineScope

class TimelineDataSourceFactory(private val scope: CoroutineScope): DataSource.Factory<String, PostData>() {

    private val postLiveData = MutableLiveData<TimelineDataSource>()
    private lateinit var timelineDataSource: TimelineDataSource

    override fun create(): DataSource<String, PostData> {
        timelineDataSource = TimelineDataSource(scope)
        postLiveData.postValue(timelineDataSource)
        return timelineDataSource
    }
}