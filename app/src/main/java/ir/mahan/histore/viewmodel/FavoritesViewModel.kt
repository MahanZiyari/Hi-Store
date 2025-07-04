package ir.mahan.histore.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mahan.histore.data.model.profile.favorites.ResponseProfileFavorites
import ir.mahan.histore.data.model.profile.userComment.ResponseDeleteComment
import ir.mahan.histore.data.repository.FavoritesRepository
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.network.ResponseHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val repository: FavoritesRepository) : ViewModel() {

    //Wallet
    private val _userFavoritesLiveData = MutableLiveData<NetworkResult<ResponseProfileFavorites>>()
    val userFavoritesLiveData: LiveData<NetworkResult<ResponseProfileFavorites>> = _userFavoritesLiveData

    fun getUserFavorites() = viewModelScope.launch {
        _userFavoritesLiveData.value = NetworkResult.Loading()
        val response = repository.fetchUserFavorites()
        _userFavoritesLiveData.value = ResponseHandler(response).generateNetworkResult()
    }


    //Wallet
    private val _deleteFavoriteResult = MutableLiveData<NetworkResult<ResponseDeleteComment>>()
    val deleteFavoriteResult: LiveData<NetworkResult<ResponseDeleteComment>> = _deleteFavoriteResult

    fun deleteUserFavorite(id: Int) = viewModelScope.launch {
        _deleteFavoriteResult.value = NetworkResult.Loading()
        val response = repository.deleteUserFavorite(id)
        _deleteFavoriteResult.value = ResponseHandler(response).generateNetworkResult()
    }

}