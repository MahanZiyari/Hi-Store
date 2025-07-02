package ir.mahan.histore.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mahan.histore.data.model.profile.ResponseWallet
import ir.mahan.histore.data.model.profile.userComment.ResponseDeleteComment
import ir.mahan.histore.data.model.profile.userComment.ResponseProfileComments
import ir.mahan.histore.data.model.wallet.BodyIncreaseWallet
import ir.mahan.histore.data.model.wallet.ResponseIncreaseWallet
import ir.mahan.histore.data.repository.UserCommentsRepository
import ir.mahan.histore.data.repository.WalletRepository
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.network.ResponseHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserCommentsViewModel @Inject constructor(private val repository: UserCommentsRepository) : ViewModel() {

    //Wallet
    private val _userCommentsLiveData = MutableLiveData<NetworkResult<ResponseProfileComments>>()
    val userCommentsLiveData: LiveData<NetworkResult<ResponseProfileComments>> = _userCommentsLiveData

    fun getUserComments() = viewModelScope.launch {
        _userCommentsLiveData.value = NetworkResult.Loading()
        val response = repository.fetchUserComments()
        _userCommentsLiveData.value = ResponseHandler(response).generateNetworkResult()
    }


    //Wallet
    private val _deleteCommentResult = MutableLiveData<NetworkResult<ResponseDeleteComment>>()
    val deleteCommentResult: LiveData<NetworkResult<ResponseDeleteComment>> = _deleteCommentResult

    fun deleteUserComment(id: Int) = viewModelScope.launch {
        _deleteCommentResult.value = NetworkResult.Loading()
        val response = repository.deleteUserComment(id)
        _deleteCommentResult.value = ResponseHandler(response).generateNetworkResult()
    }

}