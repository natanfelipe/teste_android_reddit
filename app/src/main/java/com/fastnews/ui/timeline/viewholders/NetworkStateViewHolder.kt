package com.fastnews.ui.timeline.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.fastnews.databinding.ItemTimelineBinding
import com.fastnews.service.model.PostData

class NetworkStateViewHolder(val view: ItemTimelineBinding) : RecyclerView.ViewHolder(view.root) {
    fun bind(postData: PostData) {
        view.postData = postData
    }
}