package ir.mahan.histore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mahan.histore.data.model.categories.ResponseCategories
import ir.mahan.histore.data.model.categories.ResponseCategories.ResponseCategoriesItem
import ir.mahan.histore.data.model.search.ResponseSearch
import ir.mahan.histore.data.repository.CategoriesRepository
import ir.mahan.histore.data.repository.SearchFilterRepository
import ir.mahan.histore.util.constants.Q
import ir.mahan.histore.util.constants.SORT
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.network.ResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewmodel @Inject constructor(
    private val repository: CategoriesRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            delay(300)
            getCategories()
        }
    }

    // Search Items
    private val _categoriesLiveData = MutableLiveData<NetworkResult<ResponseCategories>>()
    val categoriesLiveData: LiveData<NetworkResult<ResponseCategories>> = _categoriesLiveData

    fun getCategories() = viewModelScope.launch(Dispatchers.IO) {
        _categoriesLiveData.postValue(NetworkResult.Loading())
        val response = repository.getCategoriesList()
        _categoriesLiveData.postValue(ResponseHandler(response).generateNetworkResult())
    }


}