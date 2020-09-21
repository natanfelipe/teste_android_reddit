package com.fastnews.ui.timeline.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.fastnews.common.NetworkState
import com.fastnews.databinding.ItemTimelineNetworkStateBinding

class NetworkStateViewHolder(val view: ItemTimelineNetworkStateBinding, private val retryCallback: () -> Unit) : RecyclerView.ViewHolder(view.root) {

    init {
        view.errorMessage.setOnClickListener { retryCallback() }
    }

    fun bind(networkState: NetworkState) {
        view.networkState = networkState
    }

}