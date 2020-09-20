package com.fastnews.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.fastnews.R
import com.fastnews.databinding.FragmentTimelineBinding
import com.fastnews.ui.timeline.adapters.TimelineAdapter
import com.fastnews.viewmodel.PostViewModel
import kotlinx.android.synthetic.main.fragment_timeline.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class TimelineFragment : Fragment() {

    private val postViewModel: PostViewModel by viewModel()
    private val adapter = TimelineAdapter()
    private lateinit var binding: FragmentTimelineBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentTimelineBinding.inflate(inflater).apply {
        binding = this
        binding.lifecycleOwner = viewLifecycleOwner
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buildActionBar()
        buildTimeline()
        loadData()
        reloadList()
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
        timeline_rv.itemAnimator = DefaultItemAnimator()
        timeline_rv.adapter = adapter
    }

    private fun loadData() {
        with(postViewModel) {
            initialLoadState.observe(viewLifecycleOwner, Observer {
                binding.networkStatus = it
            })
            networkState.observe(viewLifecycleOwner, Observer {
                adapter.updateNetworkState(it)
            })
            posts.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }
    }

    private fun reloadList() {
        state_without_conn_timeline.setOnClickListener {
            postViewModel.reloadData()
        }
    }
}