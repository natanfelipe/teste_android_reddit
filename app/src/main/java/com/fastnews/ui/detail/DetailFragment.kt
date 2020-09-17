package com.fastnews.ui.detail

import android.os.Bundle
import android.text.TextUtils
import android.transition.TransitionInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fastnews.R
import com.fastnews.common.gone
import com.fastnews.common.visible
import com.fastnews.mechanism.TimeElapsed
import com.fastnews.mechanism.VerifyNetworkInfo
import com.fastnews.service.model.CommentData
import com.fastnews.service.model.PostData
import com.fastnews.ui.web.CustomTabsWeb
import com.fastnews.viewmodel.CommentViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_detail_post.*
import kotlinx.android.synthetic.main.include_detail_post_thumbnail.*
import kotlinx.android.synthetic.main.include_detail_post_title.*
import kotlinx.android.synthetic.main.include_item_timeline_ic_score.*
import kotlinx.android.synthetic.main.include_item_timeline_timeleft.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment(R.layout.fragment_detail_post) {

    companion object {
        val KEY_POST = "KEY_POST"
    }

    private var post: PostData? = null

    private val commentViewModel: CommentViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.arguments.let {
            post = it?.getParcelable(KEY_POST)
        }
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildActionBar()
        populateUi()
    }

    private fun buildActionBar() {
        val activity = activity as AppCompatActivity

        //activity.setSupportActionBar(toolbar)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun populateUi() {
        populateAuthor()
        populateTimeLeftValue()
        populateTitle()
        populateThumbnail()
        buildOnClickDetailThumbnail()
        populateScore()
        verifyConnectionState()
    }

    private fun verifyConnectionState() {
        context.let {
            if (VerifyNetworkInfo.isConnected(it!!)) {
                hideNoConnectionState()
                showStateProgress()
                fetchComments()
            } else {
                hideStateProgress()
                showNoConnectionState()

                state_without_conn_detail_post.setOnClickListener {
                    verifyConnectionState()
                }
            }
        }
    }

    private fun fetchComments() {
            post.let {
                commentViewModel.getComments(postId = post!!.id).observe(viewLifecycleOwner, Observer { comments ->
                    comments.let {
                        populateComments(comments)
                        hideStateProgress()
                        showComments()
                    }
                })
            }
    }

    private fun populateComments(comments: List<CommentData>) {
        if (isAdded) {
            activity?.runOnUiThread {
                detail_post_comments.removeAllViews()

                for (comment in comments) {
                    val itemReview = CommentItem.newInstance(requireActivity(), comment)
                    detail_post_comments.addView(itemReview)
                }
            }
        }
    }

    private fun showComments() {
        detail_post_comments.visible()
    }

    private fun hideStateProgress() {
        state_progress_detail_post_comments.gone()
    }

    private fun showStateProgress() {
        state_progress_detail_post_comments.visible()
    }

    private fun showNoConnectionState() {
        state_without_conn_detail_post.visible()
    }

    private fun hideNoConnectionState() {
        state_without_conn_detail_post.gone()
    }

    private fun populateAuthor() {
        post?.author.let {
            item_timeline_author.text = it

            (activity as AppCompatActivity).supportActionBar?.title = it
        }
    }

    private fun populateTimeLeftValue() {
        post?.created_utc.let {
            val elapsed = TimeElapsed.getTimeElapsed(it!!.toLong())
            item_timeline_timeleft.text = elapsed
        }
    }

    private fun populateTitle() {
        post?.title.let {
            item_detail_post_title.text = it
        }
    }

    private fun populateThumbnail() {
        val PREFIX_HTTP = "http"
        var thumbnailUrl = ""

        // TODO Fix high quality images
        /*if(post?.preview != null) {
            post?.preview?.images?.map {
                if (!TextUtils.isEmpty(it.source.url)) {
                    thumbnailUrl = it.source.url
                }
            }
        }*/

        if (!TextUtils.isEmpty(post?.thumbnail) && post?.thumbnail!!.startsWith(PREFIX_HTTP)) {
            thumbnailUrl = post!!.thumbnail
        }

        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Glide.with(item_detail_post_thumbnail.context)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(item_detail_post_thumbnail)
            item_detail_post_thumbnail.visibility = View.VISIBLE
        }
    }

    private fun buildOnClickDetailThumbnail() {
        item_detail_post_thumbnail.setOnClickListener {
            if(!post?.url.isNullOrEmpty()) {
                requireContext().let {
                    val customTabsWeb = CustomTabsWeb(it, post?.url!!)
                    customTabsWeb.openUrlWithCustomTabs()
                }
            } else {
                Snackbar.make(item_detail_post_thumbnail, R.string.error_detail_post_url, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateScore() {
        post?.score.let {
            item_timeline_bt_score_text.text = it.toString()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }
}