package com.fastnews.ui.timeline.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.fastnews.common.NetworkState
import com.fastnews.databinding.ItemTimelineNetworkStateBinding

class NetworkStateViewHolder(val view: ItemTimelineNetworkStateBinding) : RecyclerView.ViewHolder(view.root) {
    fun bind(networkState: NetworkState) {
        view.networkState = networkState
    }
}