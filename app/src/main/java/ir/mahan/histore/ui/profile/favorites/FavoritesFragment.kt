package ir.mahan.histore.ui.profile.favorites

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ir.mahan.histore.R
import ir.mahan.histore.data.model.profile.favorites.ResponseProfileFavorites
import ir.mahan.histore.databinding.FragmentFavoritesBinding
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.extensions.setupRecyclerview
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.FavoritesViewModel
import javax.inject.Inject


class FavoritesFragment : BaseFragment() {

    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////
    //Binding
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    // View Model
    private val viewModel by viewModels<FavoritesViewModel>()
    // Other
    @Inject
    lateinit var favoritesAdapter: FavoritesAdapter
    private var recyclerviewState: Parcelable? = null
    ///////////////////////////////////////////////////////////////////////////
    // user Functions
    ///////////////////////////////////////////////////////////////////////////

    private fun requestForFavorites() {
        if (isNetworkAvailable)
            viewModel.getUserFavorites()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() = binding.apply {
        initToolbar()
    }

    private fun initToolbar() = binding.toolbar.apply {
        toolbarTitleTxt.text = getString(R.string.yourFavorites)
        toolbarOptionImg.isVisible = false
        toolbarBackImg.setOnClickListener { findNavController().popBackStack() }
    }



    private fun loadScreenData(){
        observeUserFavorites()
        observeDeleteFavoriteResult()
    }

    private fun observeUserFavorites() = binding.apply {
        viewModel.userFavoritesLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    commentsList.showShimmer()
                }

                is NetworkResult.Success -> {
                    commentsList.hideShimmer()
                    result.data?.let {
                        if (it.data.isNotEmpty()) {
                            initRecycler(it.data)
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

    private fun observeDeleteFavoriteResult() = binding.apply {
        viewModel.deleteFavoriteResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {}

                is NetworkResult.Success -> {
                    result.data?.let {
                        requestForFavorites()
                    }
                }

                is NetworkResult.Error -> {
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun initRecycler(list: List<ResponseProfileFavorites.Data>) {
        binding.apply {
            favoritesAdapter.setData(list)
            commentsList.setupRecyclerview(LinearLayoutManager(requireContext()), favoritesAdapter)
            //Auto scroll
            commentsList.layoutManager?.onRestoreInstanceState(recyclerviewState)
            //Click
            favoritesAdapter.setOnItemClickListener {
                //Save state
                recyclerviewState = commentsList.layoutManager?.onSaveInstanceState()
                //Call delete api
                if (isNetworkAvailable)
                    viewModel.deleteUserFavorite(it)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestForFavorites()
        setupUI()
        loadScreenData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}