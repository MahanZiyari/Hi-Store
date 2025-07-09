package ir.mahan.histore.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mahan.histore.data.model.profile.favorites.ResponseProfileFavorites
import ir.mahan.histore.data.model.profile.order.ResponseProfileOrdersList
import ir.mahan.histore.data.model.profile.userComment.ResponseDeleteComment
import ir.mahan.histore.data.repository.FavoritesRepository
import ir.mahan.histore.data.repository.ProfileOrdersRepository
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.network.ResponseHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileOrdersViewModel @Inject constructor(private val repository: ProfileOrdersRepository) : ViewModel() {

    //Wallet
    private val _userOrdersLiveData = MutableLiveData<NetworkResult<ResponseProfileOrdersList>>()
    val userOrdersLiveData: LiveData<NetworkResult<ResponseProfileOrdersList>> = _userOrdersLiveData

    fun getUserOrders(status: String) = viewModelScope.launch {
        _userOrdersLiveData.value = NetworkResult.Loading()
        val response = repository.getProfileOrdersList(status)
        _userOrdersLiveData.value = ResponseHandler(response).generateNetworkResult()
    }


}