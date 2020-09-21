package com.fastnews.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastnews.common.NetworkState
import com.fastnews.mechanism.VerifyNetworkInfo
import com.fastnews.repository.CommentRepository
import com.fastnews.service.model.CommentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentViewModel(private val currentNetworkInfo: VerifyNetworkInfo) : ViewModel() {

    private var mutableComments = MutableLiveData<List<CommentData>>()
    val comments: LiveData<List<CommentData>>
        get() = mutableComments
    var mutableNetworkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = mutableNetworkState

    fun getComments(postId: String) {
        if (currentNetworkInfo.isConnected()) {
            viewModelScope.launch(Dispatchers.IO) {
                mutableNetworkState.postValue(NetworkState.RUNNING)
                mutableComments.postValue(
                    CommentRepository.getComments(
                        postId
                    )
                )
                mutableNetworkState.postValue(NetworkState.SUCCESS)
            }
        } else {
            mutableNetworkState.postValue(NetworkState.FAILED)
        }
    }
}