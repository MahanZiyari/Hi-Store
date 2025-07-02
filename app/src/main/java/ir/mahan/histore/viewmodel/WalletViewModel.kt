package ir.mahan.histore.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mahan.histore.data.model.profile.ResponseWallet
import ir.mahan.histore.data.model.wallet.BodyIncreaseWallet
import ir.mahan.histore.data.model.wallet.ResponseIncreaseWallet
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

    //Wallet
    private val _increaseWalletData = MutableLiveData<NetworkResult<ResponseIncreaseWallet>>()
    val increaseWalletData: LiveData<NetworkResult<ResponseIncreaseWallet>> = _increaseWalletData

    fun requestIncreaseWallet(body: BodyIncreaseWallet) = viewModelScope.launch {
        _increaseWalletData.value = NetworkResult.Loading()
        val response = repository.postIncreaseWallet(body)
        _increaseWalletData.value = ResponseHandler(response).generateNetworkResult()
    }


}