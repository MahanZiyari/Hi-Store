package ir.mahan.histore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mahan.histore.data.model.search.ResponseSearch
import ir.mahan.histore.data.model.search.filter.FilterModel
import ir.mahan.histore.data.repository.SearchFilterRepository
import ir.mahan.histore.data.repository.SearchRepository
import ir.mahan.histore.util.constants.Q
import ir.mahan.histore.util.constants.SORT
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.network.ResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewmodel @Inject constructor(
    private val repository: SearchRepository,
    private val filterRepository: SearchFilterRepository
) : ViewModel() {

    // Search Items
    private val _searchResultLiveData = MutableLiveData<NetworkResult<ResponseSearch>>()
    val searchResultLiveData: LiveData<NetworkResult<ResponseSearch>> = _searchResultLiveData

    fun searchQueries(search: String, sort: String): Map<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[Q] = search
        queries[SORT] = sort
        return queries
    }

    fun searchForProducts(queries: Map<String, String>) = viewModelScope.launch(Dispatchers.IO) {
        _searchResultLiveData.postValue(NetworkResult.Loading())
        val response = repository.searchProducts(queries)
        _searchResultLiveData.postValue(ResponseHandler(response).generateNetworkResult())
    }

    //Filter
    private val _filterData = MutableLiveData<List<FilterModel>>()
    val filterData: LiveData<List<FilterModel>> = _filterData

    fun getFilterData() = viewModelScope.launch {
        _filterData.value = filterRepository.fillFilterData()
    }

    //Filter selected
    private val _activeFilter = MutableLiveData<String>()
    val activeFilter: LiveData<String> = _activeFilter

    fun setActiveFilterTo(item: String) {
        _activeFilter.value = item
    }
}