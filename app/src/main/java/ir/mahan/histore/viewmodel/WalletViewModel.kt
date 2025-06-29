package ir.mahan.histore.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mahan.histore.data.model.profile.ResponseWallet
import ir.mahan.histore.data.repository.WalletRepository
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.network.ResponseHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(private val repository: WalletRepository) : ViewModel() {

    //Wallet
    private val _walletData = MutableLiveData<NetworkResult<ResponseWallet>>()
    val walletData: LiveData<NetworkResult<ResponseWallet>> = _walletData

    fun callWalletApi() = viewModelScope.launch {
        _walletData.value = NetworkResult.Loading()
        val response = repository.fetchWalletAmount()
        _walletData.value = ResponseHandler(response).generateNetworkResult()
    }


}