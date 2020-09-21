package com.fastnews.ui.timeline.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fastnews.R
import com.fastnews.common.KEY_POST
import com.fastnews.common.NetworkState
import com.fastnews.databinding.ItemTimelineBinding
import com.fastnews.databinding.ItemTimelineNetworkStateBinding
import com.fastnews.interfaces.OnClickItemList
import com.fastnews.service.model.PostData
import com.fastnews.ui.timeline.viewholders.NetworkStateViewHolder
import com.fastnews.ui.timeline.viewholders.TimelineItemViewHolder
import kotlinx.android.synthetic.main.include_item_timeline_thumbnail.view.*
import java.lang.IllegalArgumentException

class TimelineAdapter(private val retryCallback: () -> Unit) :
    PagedListAdapter<PostData, RecyclerView.ViewHolder>(PostDiffUtilCallback), OnClickItemList {

    private var currentState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_timeline -> {
                val view = ItemTimelineBinding.inflate(inflater, parent, false)
                TimelineItemViewHolder(view)
            }
            R.layout.item_timeline_network_state -> {
                val view = ItemTimelineNetworkStateBinding.inflate(inflater, parent, false)
                NetworkStateViewHolder(view, retryCallback)
            }
            else -> {
                throw IllegalArgumentException("")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_timeline -> {
                getItem(position)?.let {
                    (holder as TimelineItemViewHolder).view.apply {
                        holder.bind(it)
                        holder.view.click = this@TimelineAdapter
                    }
                }
            }
            R.layout.item_timeline_network_state -> currentState?.let {
                (holder as NetworkStateViewHolder).bind(it)
            }
        }
    }

    private fun hasMorePostsToLoad() = currentState != NetworkState.SUCCESS

    override fun getItemViewType(position: Int) =
        if (hasMorePostsToLoad() && position == itemCount - 1) {
            R.layout.item_timeline_network_state
        } else {
            R.layout.item_timeline
        }

    override fun getItemCount() = super.getItemCount() + if (hasMorePostsToLoad()) 1 else 0

    fun updateNetworkState(state: NetworkState) {
        currentList?.let {
            if (it.size != 0) {
                val previousState = currentState
                val hadToLoadMore = hasMorePostsToLoad()
                currentState = state
                val hasToLoadMore = hasMorePostsToLoad()
                if (hadToLoadMore != hasToLoadMore) {
                    if (hadToLoadMore) {
                        notifyItemRemoved(super.getItemCount())
                    } else {
                        notifyItemInserted(super.getItemCount())
                    }
                }
                if (hadToLoadMore && previousState !== currentState) {
                    notifyItemChanged(itemCount - 1)
                }
            }
        }
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

    override fun onClick(view: View) {
        currentList?.let { posts ->
            for (post in posts) {
                if (view.tag == post.url) {
                    val extras = FragmentNavigatorExtras(
                        view.item_timeline_thumbnail to "thumbnail"
                    )
                    val bundle = Bundle()
                    bundle.putParcelable(KEY_POST, post)
                    Navigation.findNavController(view)
                        .navigate(R.id.action_timeline_to_detail, bundle, null, extras)
                }
            }
        }
    }
}
