package ir.mahan.histore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mahan.histore.data.model.home.ResponseBanners
import ir.mahan.histore.data.repository.HomeRepository
import ir.mahan.histore.util.constants.GENERAL
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.network.ResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            delay(200)
            fetchBannersData()
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
}