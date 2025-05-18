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
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.network.ResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewmodel @Inject constructor(private val repository: LoginRepository) : ViewModel() {
    private var _loginData = MutableLiveData<NetworkResult<ResponseLogin>>()
    val loginData: LiveData<NetworkResult<ResponseLogin>> = _loginData

    fun requestToLoginUser(body: BodyLogin) = viewModelScope.launch(Dispatchers.IO) {
        _loginData.postValue(NetworkResult.Loading())
        val response = repository.postLogin(body)
        _loginData.postValue(ResponseHandler(response).generateNetworkResult())
    }

    //Verify
    private val _verifyData = MutableLiveData<NetworkResult<ResponseVerify>>()
    val verifyData: LiveData<NetworkResult<ResponseVerify>> = _verifyData

    fun verifyPhoneNumber(body: BodyLogin) = viewModelScope.launch(Dispatchers.IO) {
        _verifyData.postValue(NetworkResult.Loading())
        val response = repository.postVerify(body)
        _verifyData.postValue(ResponseHandler(response).generateNetworkResult())
    }
}