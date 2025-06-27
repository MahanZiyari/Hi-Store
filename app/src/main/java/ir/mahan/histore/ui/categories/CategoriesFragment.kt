package ir.mahan.histore.ui.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.model.categories.ResponseCategories.ResponseCategoriesItem
import ir.mahan.histore.databinding.FragmentCategoriesBinding
import ir.mahan.histore.ui.categories.adapters.CategoryAdapter
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.extensions.setupRecyclerview
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.CategoriesViewmodel
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesFragment : BaseFragment() {

    //Binding
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    // View Model
    private val viewModel: CategoriesViewmodel by activityViewModels()
    // Adapters
    @Inject
    lateinit var categoriesAdapter: CategoryAdapter

    // Called inside onViewCreated()
    private fun setupUI() = binding.apply {
        setupToolBar()
    }

    private fun loadScreenData() {
        loadCategoriesData()
    }

    private fun loadCategoriesData() = binding.apply {
        viewModel.categoriesLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    categoriesList.showShimmer()
                }

                is NetworkResult.Success -> {
                    categoriesList.hideShimmer()
                    result.data?.let { responseCategories ->
                        initCategoriesRecycler(responseCategories)
                    }
                }

                is NetworkResult.Error -> {
                    categoriesList.hideShimmer()
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun initCategoriesRecycler(data: List<ResponseCategoriesItem>) {
        categoriesAdapter.setData(data.dropLast(1))
        binding.categoriesList.setupRecyclerview(LinearLayoutManager(requireContext()), categoriesAdapter)
        //Click
        categoriesAdapter.setOnItemClickListener {
            val action = CategoriesFragmentDirections.actionFromCategoriesToRelatedProducts(it)
            findNavController().navigate(action)
        }
    }

    // Initialize tool bar
    private fun setupToolBar() = binding.toolbar.apply {
        //Title
        toolbarTitleTxt.text = getString(R.string.categories)
        //Option
        toolbarOptionImg.isVisible = false
        toolbarBackImg.isVisible = false
    }

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle Methods
    ///////////////////////////////////////////////////////////////////////////
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCategoriesBinding.inflate(layoutInflater)
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