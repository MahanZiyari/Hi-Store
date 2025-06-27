package ir.mahan.histore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mahan.histore.data.model.categories.FilterCategoryModel
import ir.mahan.histore.data.model.home.ResponseProducts
import ir.mahan.histore.data.model.search.filter.FilterModel
import ir.mahan.histore.data.repository.CatProductRepository
import ir.mahan.histore.data.repository.SearchFilterRepository
import ir.mahan.histore.util.constants.MAX_PRICE
import ir.mahan.histore.util.constants.MIN_PRICE
import ir.mahan.histore.util.constants.ONLY_AVAILABLE
import ir.mahan.histore.util.constants.SEARCH
import ir.mahan.histore.util.constants.SELECTED_BRANDS
import ir.mahan.histore.util.constants.SORT
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.network.ResponseHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatProductsViewModel @Inject constructor(
    private val repository: CatProductRepository,
    private val filterRepository: SearchFilterRepository
) : ViewModel() {

    fun createProductsQuery(
        sort: String? = null,
        search: String? = null,
        minPrice: String? = null,
        maxPrice: String? = null,
        brands: String? = null,
        available: Boolean? = null
    ): Map<String, String> {
        val queries: HashMap<String, String> = HashMap()
        if (sort != null)
            queries[SORT] = sort
        if (search != null)
            queries[SEARCH] = search
        if (minPrice != null)
            queries[MIN_PRICE] = minPrice
        if (maxPrice != null)
            queries[MAX_PRICE] = maxPrice
        if (brands != null)
            queries[SELECTED_BRANDS] = brands
        if (available != null)
            queries[ONLY_AVAILABLE] = available.toString()
        return queries
    }

    private val _productsLiveData = MutableLiveData<NetworkResult<ResponseProducts>>()
    val  productsLiveData : LiveData<NetworkResult<ResponseProducts>> = _productsLiveData

    fun fetchProductsFromApi(slug: String, queries: Map<String, String>) = viewModelScope.launch {
        _productsLiveData.value = NetworkResult.Loading()
        val response = repository.getProductsList(slug, queries)
        _productsLiveData.value = ResponseHandler(response).generateNetworkResult()
    }

    //Filter
    private val _filterData = MutableLiveData<List<FilterModel>>()
    val filterData: LiveData<List<FilterModel>> = _filterData

    fun getFilterData() = viewModelScope.launch {
        _filterData.value = filterRepository.fillFilterData()
    }

    //Filter Data
    private val _filterCategoryData = MutableLiveData<FilterCategoryModel>()
    val filterCategoryData: LiveData<FilterCategoryModel> = _filterCategoryData

    fun setSelectedFilers(
        sort: String? = null, search: String? = null, minPrice: String? = null, maxPrice: String? = null,
        available: Boolean? = null
    ) {
        _filterCategoryData.value = FilterCategoryModel(sort, search, minPrice, maxPrice, available)
    }
}