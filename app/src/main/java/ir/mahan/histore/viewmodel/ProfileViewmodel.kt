package ir.mahan.histore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mahan.histore.data.model.login.BodyLogin
import ir.mahan.histore.data.model.login.ResponseLogin
import ir.mahan.histore.data.repository.LoginRepository
import ir.mahan.histore.data.model.login.ResponseVerify
import ir.mahan.histore.data.model.profile.ResponseProfile
import ir.mahan.histore.data.repository.ProfileRepository
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.network.ResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewmodel @Inject constructor(private val repository: ProfileRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            delay(200)
            fetchProfileData()
        }
    }


    //Verify
    private val _profileData = MutableLiveData<NetworkResult<ResponseProfile>>()
    val profileData: LiveData<NetworkResult<ResponseProfile>> = _profileData

    fun fetchProfileData() = viewModelScope.launch(Dispatchers.IO) {
        _profileData.postValue(NetworkResult.Loading())
        val response = repository.getProfileData()
        _profileData.postValue(ResponseHandler(response).generateNetworkResult())
    }
}