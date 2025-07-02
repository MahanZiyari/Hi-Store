package ir.mahan.histore.ui.profile.comments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.model.profile.BodyUpdateProfile
import ir.mahan.histore.data.model.profile.userComment.ResponseProfileComments
import ir.mahan.histore.databinding.FragmentEditProfileBinding
import ir.mahan.histore.databinding.FragmentUserCommentsBinding
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.event.Event
import ir.mahan.histore.util.event.EventBus
import ir.mahan.histore.util.extensions.enableLoading
import ir.mahan.histore.util.extensions.setupRecyclerview
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.ProfileViewmodel
import ir.mahan.histore.viewmodel.UserCommentsViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserCommentsFragment : BaseFragment() {

    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////
    //Binding
    private var _binding: FragmentUserCommentsBinding? = null
    private val binding get() = _binding!!
    // View Model
    private val viewModel by viewModels<UserCommentsViewModel>()
    // Other
    @Inject
    lateinit var commentsAdapter: CommentsAdapter
    private var recyclerviewState: Parcelable? = null
    ///////////////////////////////////////////////////////////////////////////
    // user Functions
    ///////////////////////////////////////////////////////////////////////////

    private fun requestForComments() {
        if (isNetworkAvailable)
            viewModel.getUserComments()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() = binding.apply {
        initToolbar()
    }

    private fun initToolbar() = binding.toolbar.apply {
        toolbarTitleTxt.text = getString(R.string.yourComments)
        toolbarOptionImg.isVisible = false
        toolbarBackImg.setOnClickListener { findNavController().popBackStack() }
    }



    private fun loadScreenData(){
        observeUserComments()
        observeDeleteCommentResult()
    }

    private fun observeUserComments() = binding.apply {
        viewModel.userCommentsLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    commentsList.showShimmer()
                }

                is NetworkResult.Success -> {
                    commentsList.hideShimmer()
                    result.data?.let {
                        if (it.data.isNotEmpty()) {
                            initCommentsRecycler(it.data)
                        } else {
                            emptyLay.isVisible = true
                            commentsList.isVisible = false
                        }
                    }
                }

                is NetworkResult.Error -> {
                    commentsList.hideShimmer()
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun observeDeleteCommentResult() = binding.apply {
        viewModel.deleteCommentResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {}

                is NetworkResult.Success -> {
                    result.data?.let {
                        requestForComments()
                    }
                }

                is NetworkResult.Error -> {
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun initCommentsRecycler(list: List<ResponseProfileComments.Data>) {
        binding.apply {
            commentsAdapter.setData(list)
            commentsList.setupRecyclerview(LinearLayoutManager(requireContext()), commentsAdapter)
            //Auto scroll
            commentsList.layoutManager?.onRestoreInstanceState(recyclerviewState)
            //Click
            commentsAdapter.setOnItemClickListener {
                //Save state
                recyclerviewState = commentsList.layoutManager?.onSaveInstanceState()
                //Call delete api
                if (isNetworkAvailable)
                    viewModel.deleteUserComment(it)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserCommentsBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestForComments()
        setupUI()
        loadScreenData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}