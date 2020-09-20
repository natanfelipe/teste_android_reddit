package com.fastnews.ui.detail

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.fastnews.R
import com.fastnews.common.KEY_POST
import com.fastnews.common.TEXT_TYPE
import com.fastnews.databinding.FragmentDetailPostBinding
import com.fastnews.service.model.CommentData
import com.fastnews.service.model.PostData
import com.fastnews.ui.web.CustomTabsWeb
import com.fastnews.viewmodel.CommentViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_detail_post.*
import kotlinx.android.synthetic.main.include_detail_post_thumbnail.*
import kotlinx.android.synthetic.main.include_header_detail_post_share.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment(R.layout.fragment_detail_post) {

    private var post: PostData? = null

    private val commentViewModel: CommentViewModel by viewModel()

    private lateinit var binding: FragmentDetailPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.arguments.let {
            post = it?.getParcelable(KEY_POST)
        }
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDetailPostBinding.inflate(inflater).apply {
        binding = this
        binding.networkStatus = networkStatus
        binding.lifecycleOwner = viewLifecycleOwner
        binding.postData = post
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildActionBar()
        populateUi()
        fetchComments()
        observeComments()
    }

    private fun buildActionBar() {
        val activity = activity as AppCompatActivity

        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun populateUi() {
        buildOnClickDetailThumbnail()
        populateAuthor()
        buildOnClickShareButton()
    }

    private fun populateAuthor() {
        post?.author.let {
            (activity as AppCompatActivity).supportActionBar?.title = it
        }
    }

    private fun fetchComments() {
        post?.let { postData ->
            commentViewModel.getComments(postData.id)
        }
    }

    private fun observeComments() {
        with(commentViewModel) {
            comments.observe(viewLifecycleOwner, Observer { comments ->
                populateComments(comments)
            })
            networkState.observe(viewLifecycleOwner, Observer { networkState ->
                binding.networkStatus = networkState
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

    private fun buildOnClickDetailThumbnail() {
        item_detail_post_thumbnail.setOnClickListener {
            if (!post?.url.isNullOrEmpty()) {
                requireContext().let {
                    val customTabsWeb = CustomTabsWeb(it, post?.url!!)
                    customTabsWeb.openUrlWithCustomTabs()
                }
            } else {
                Snackbar.make(
                    item_detail_post_thumbnail,
                    R.string.error_detail_post_url,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun buildOnClickShareButton() {
        bt_share.setOnClickListener{
            createShareIntent()
        }
    }

    private fun createShareIntent() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post?.url)
            type = TEXT_TYPE
        }

        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }
}