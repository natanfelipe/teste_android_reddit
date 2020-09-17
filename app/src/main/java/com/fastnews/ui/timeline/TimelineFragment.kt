package com.fastnews.ui.timeline

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.fastnews.R
import com.fastnews.common.gone
import com.fastnews.common.hasInternetConnection
import com.fastnews.common.visible
import com.fastnews.service.model.PostData
import com.fastnews.ui.detail.DetailFragment.Companion.KEY_POST
import com.fastnews.viewmodel.PostViewModel
import kotlinx.android.synthetic.main.fragment_timeline.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class TimelineFragment : Fragment(R.layout.fragment_timeline) {

    private val postViewModel: PostViewModel by viewModel()
    private lateinit var adapter: TimelineAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buildActionBar()
        buildTimeline()
        verifyConnectionState()
    }

    private fun verifyConnectionState() {
        if (requireContext().hasInternetConnection()) {
            hideNoConnectionState()
            showProgress()
            fetchTimeline()
        } else {
            hideProgress()
            showNoConnectionState()

            state_without_conn_timeline.setOnClickListener {
                verifyConnectionState()
            }
        }
    }

    private fun buildActionBar() {
        (activity as AppCompatActivity).supportActionBar?.apply {
            setHomeButtonEnabled(false)
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowHomeEnabled(false)
            title = resources.getString(R.string.app_name)
        }
    }

    private fun buildTimeline() {
        adapter = TimelineAdapter { it, imageView ->
            onClickItem(it, imageView)
        }

        timeline_rv.itemAnimator = DefaultItemAnimator()
        timeline_rv.adapter = adapter
    }

    private fun fetchTimeline() {
        postViewModel.getPosts("", 50).observe(viewLifecycleOwner, Observer { posts ->
            posts.let {
                adapter.setData(posts)
                hideProgress()
                showPosts()
            }
        })
    }

    private fun showPosts() {
        timeline_rv.visible()
    }

    private fun showProgress() {
        state_progress_timeline.visible()
    }

    private fun hideProgress() {
        state_progress_timeline.gone()
    }

    private fun showNoConnectionState() {
        state_without_conn_timeline.visible()
    }

    private fun hideNoConnectionState() {
        state_without_conn_timeline.gone()
    }

    private fun onClickItem(postData: PostData, imageView: ImageView) {
        val extras = FragmentNavigatorExtras(
            imageView to "thumbnail"
        )
        val bundle = Bundle()
        bundle.putParcelable(KEY_POST, postData)
        findNavController().navigate(R.id.action_timeline_to_detail, bundle, null, extras)
    }
}