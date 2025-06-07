package ir.mahan.histore.viewmodel

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mahan.histore.data.model.home.ResponseBanners
import ir.mahan.histore.data.model.home.ResponseDiscount
import ir.mahan.histore.data.model.home.ResponseProducts
import ir.mahan.histore.data.repository.HomeRepository
import ir.mahan.histore.util.ProductsCategories
import ir.mahan.histore.util.constants.GENERAL
import ir.mahan.histore.util.constants.NEW
import ir.mahan.histore.util.constants.SORT
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.network.ResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    // ScrollViewLastState
    var lastScrollState: Parcelable? = null

    init {
        viewModelScope.launch {
            delay(200)
            fetchBannersData()
            fetchDiscountItems()
        }

    }


    //Verify
    private val _bannersLiveData = MutableLiveData<NetworkResult<ResponseBanners>>()
    val bannersLiveData: LiveData<NetworkResult<ResponseBanners>> = _bannersLiveData

    private fun fetchBannersData() = viewModelScope.launch(Dispatchers.IO) {
        _bannersLiveData.postValue(NetworkResult.Loading())
        val response = repository.getBannersList(GENERAL)
        _bannersLiveData.postValue(ResponseHandler(response).generateNetworkResult())
    }

    // Discount Items
    private val _discountItemsLiveData = MutableLiveData<NetworkResult<ResponseDiscount>>()
    val discountItemsLiveData: LiveData<NetworkResult<ResponseDiscount>> = _discountItemsLiveData

    private fun fetchDiscountItems() = viewModelScope.launch(Dispatchers.IO) {
        _discountItemsLiveData.postValue(NetworkResult.Loading())
        val response = repository.getDiscountItems(GENERAL)
        _discountItemsLiveData.postValue(ResponseHandler(response).generateNetworkResult())
    }

    // products
    private fun productsQueries(): Map<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[SORT] = NEW
        return queries
    }

    /**
     * Call the API for only one type of category
     * @param categories
     */
    private fun fetchProductsBySingleCall(categories: ProductsCategories) = liveData {
        emit(NetworkResult.Loading())
        val response = repository.getProductsList(categories.item, productsQueries())
        emit(ResponseHandler(response).generateNetworkResult())
    }

    /**
     * associateWith: Returns a Map where keys are elements from the given array and values are produced by the valueSelector function applied to each element.
     * inline fun <K, V> Array<out K>.associateWith(valueSelector: (K) -> V): Map<K, V>
     */
    // map each category to its LiveData
    private val productsLiveDataMap: Map<ProductsCategories, LiveData<NetworkResult<ResponseProducts>>> =
        ProductsCategories.entries.associateWith {
            fetchProductsBySingleCall(it)
        }

    /**
     * Get products LiveData by providing key
     * @param categories
     */
    fun getProductsSpecifiedBy(categories: ProductsCategories) =
        productsLiveDataMap.getValue(categories)
}