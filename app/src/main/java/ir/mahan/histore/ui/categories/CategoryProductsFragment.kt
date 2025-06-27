package ir.mahan.histore.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.model.home.ResponseProducts
import ir.mahan.histore.databinding.FragmentCategoryProductsBinding
import ir.mahan.histore.ui.categories.adapters.CategoryAdapter
import ir.mahan.histore.ui.categories.adapters.ProductsAdapter
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.extensions.setupRecyclerview
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.CatProductsViewModel
import javax.inject.Inject

@AndroidEntryPoint
class CategoryProductsFragment : BaseFragment() {

    //Binding
    private var _binding: FragmentCategoryProductsBinding? = null
    private val binding get() = _binding!!

    // View Model
    private val viewModel: CatProductsViewModel by activityViewModels()
    private val args: CategoryProductsFragmentArgs by navArgs()

    // Adapters
    @Inject
    lateinit var productsAdapter: ProductsAdapter

    // Other Props
    private var slug = ""

    // Called inside onViewCreated()
    private fun setupUI() = binding.apply {
        setupToolBar()
    }

    private fun loadSpecifiedProductsByCategory() = binding.apply {
        viewModel.productsLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    productsList.showShimmer()
                }

                is NetworkResult.Success -> {
                    productsList.hideShimmer()
                    result.data?.let { responseCategories ->
                        binding.toolbar.toolbarTitleTxt.text = responseCategories.subCategory?.title
                        responseCategories.subCategory?.products?.let { products ->
                            if (products.data!!.isNotEmpty()) {
                                emptyLay.isVisible = false
                                productsList.isVisible = true
                                //Init recycler
                                initProductsRecycler(products.data)
                            } else {
                                emptyLay.isVisible = true
                                productsList.isVisible = false
                            }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    productsList.hideShimmer()
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun observeCategoryFilter() {
        viewModel.filterCategoryData.observe(viewLifecycleOwner) {
            if (isNetworkAvailable)
                viewModel.fetchProductsFromApi(
                    slug,
                    viewModel.createProductsQuery(
                        sort = it.sort, search = it.search, minPrice = it.minPrice, maxPrice = it.maxPrice,
                        available = it.available
                    )
                )
        }
    }

    private fun initProductsRecycler(data: List<ResponseProducts.SubCategory.Products.Data>) {
        productsAdapter.setData(data)
        binding.productsList.setupRecyclerview(LinearLayoutManager(requireContext()), productsAdapter)
        //Click
        productsAdapter.setOnItemClickListener {
            // TODO: Navigate to Detail Screen
        }
    }

    private fun loadScreenData() {
        loadSpecifiedProductsByCategory()
        observeCategoryFilter()
    }

    // Initialize tool bar
    private fun setupToolBar() = binding.toolbar.apply {
        toolbarBackImg.setOnClickListener {
            findNavController().popBackStack()
        }
        //Option
        toolbarOptionImg.setOnClickListener {
            findNavController().navigate(R.id.actionOpenCatFilters)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle Methods
    ///////////////////////////////////////////////////////////////////////////
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryProductsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args?.let {
            slug = it.slug
        }
        viewModel.fetchProductsFromApi(slug, viewModel.createProductsQuery())
        viewModel.getFilterData()
        setupUI()
        loadScreenData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}