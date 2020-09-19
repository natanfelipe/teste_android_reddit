package com.fastnews.ui.timeline.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.fastnews.R
import com.fastnews.common.NetworkState
import com.fastnews.databinding.ItemTimelineBinding
import com.fastnews.interfaces.OnClickItemList
import com.fastnews.service.model.PostData
import com.fastnews.ui.detail.DetailFragment
import com.fastnews.ui.timeline.viewholders.TimelineItemViewHolder
import kotlinx.android.synthetic.main.include_item_timeline_thumbnail.view.*

class TimelineAdapter : PagedListAdapter<PostData, TimelineItemViewHolder>(
    PostDiffUtilCallback
), OnClickItemList {

    private var currentState = NetworkState.RUNNING

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = ItemTimelineBinding.inflate(inflater, parent, false)

        return TimelineItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimelineItemViewHolder, position: Int) {
        getItem(position)?.let { postData ->
            holder.apply {
                bind(postData)
                view.click = this@TimelineAdapter
            }
        }
    }


    override fun getItemCount() = super.getItemCount() + if (showingFooter()) 1 else 0

    private fun showingFooter(): Boolean =
        super.getItemCount() != 0 && (currentState == NetworkState.RUNNING || currentState == NetworkState.FAILED)

    fun updateNetworkState(state: NetworkState) {
        this.currentState = state
        notifyItemChanged(super.getItemCount())
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
                    bundle.putParcelable(DetailFragment.KEY_POST, post)
                    Navigation.findNavController(view)
                        .navigate(R.id.action_timeline_to_detail, bundle, null, extras)
                }
            }
        }
    }
}