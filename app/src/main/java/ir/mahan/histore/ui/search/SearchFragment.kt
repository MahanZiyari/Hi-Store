package ir.mahan.histore.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.databinding.FragmentSearchBinding
import ir.mahan.histore.ui.search.adapters.SearchAdapter
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.constants.NEW
import ir.mahan.histore.util.extensions.setupRecyclerview
import ir.mahan.histore.util.extensions.showKeyboard
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.SearchViewmodel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment() {

    //Binding
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    // View Model
    private val viewModel: SearchViewmodel by activityViewModels()
    // Adapters
    @Inject
    lateinit var searchAdapter: SearchAdapter

    // Called inside onViewCreated()
    private fun setupUI() = binding.apply {
        setupToolBar()
        popUpKeyboard()
        initSearchItemsRecycler()
        //Search
        handleSearchInput()
        observeActiveFilter()
        filterImg.setOnClickListener { findNavController().navigate(R.id.actionSearchToFilter) }
    }

    private fun loadScreenData() {
        loadSearchData()
    }

    private fun initSearchItemsRecycler() {
        binding.searchList.setupRecyclerview(LinearLayoutManager(requireContext()), searchAdapter)
        //Click
        searchAdapter.setOnItemClickListener {
            // TODO: Navigate to details
        }
    }

    private fun loadSearchData() = binding.apply {
        viewModel.searchResultLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    searchList.showShimmer()
                }

                is NetworkResult.Success -> {
                    searchList.hideShimmer()
                    result.data?.let { responseSearch ->
                        responseSearch.products?.let { products ->
                            if (products.data?.isNotEmpty()!!) {
                                emptyLay.isVisible = false
                                searchList.isVisible = true
                                //Init recycler
                                searchAdapter.setData(products.data)
                            } else {
                                emptyLay.isVisible = true
                                searchList.isVisible = false
                            }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    searchList.hideShimmer()
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun observeActiveFilter() = binding.apply {
        viewModel.activeFilter.observe(viewLifecycleOwner) {
            val searchText = searchEdt.text.toString()
            if (searchText.length >= 3)
                viewModel.searchForProducts(
                    queries = viewModel.searchQueries(searchText, it)
                )
        }
    }

    // Initialize tool bar
    private fun setupToolBar() = binding.toolbar.apply {
        //Back
        toolbarBackImg.setOnClickListener { findNavController().popBackStack() }
        //Title
        toolbarTitleTxt.text = getString(R.string.searchInProducts)
        //Option
        toolbarOptionImg.isVisible = false
    }

    private fun popUpKeyboard() {
        //Auto open keyboard
        lifecycleScope.launch {
            delay(300)
            binding.searchEdt.showKeyboard(requireActivity())
        }
    }
    
    private fun handleSearchInput() = binding.apply {
        searchEdt.addTextChangedListener {
            if (it.toString().length > 3) {
                if (isNetworkAvailable) {
                    viewModel.searchForProducts(viewModel.searchQueries(it.toString(), NEW))
                }
            }
            //Empty
            if (it.toString().isEmpty()) {
                emptyLay.isVisible = true
                searchList.isVisible = false
            }
        }
    }

    // Lifecycle  Methods
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadScreenData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}