package com.fastnews.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fastnews.R
import com.fastnews.service.model.PostData
import kotlinx.android.synthetic.main.include_item_timeline_thumbnail.view.*

class TimelineAdapter(val onClickItem: (PostData, ImageView) -> Unit) : PagedListAdapter<PostData, TimelineItemViewHolder>(PostDiffUtilCallback) {

    private var items: List<PostData> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineItemViewHolder
            = TimelineItemViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_timeline,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: TimelineItemViewHolder, position: Int) {
        holder.data = getItem(position)
        holder.view.setOnClickListener { onClickItem(items[position], holder.view.item_timeline_thumbnail) }
    }

    companion object {
        val PostDiffUtilCallback = object : DiffUtil.ItemCallback<PostData>() {
            override fun areItemsTheSame(oldItem: PostData, newItem: PostData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PostData, newItem: PostData): Boolean {
                return oldItem == newItem
            }
        }
    }
}