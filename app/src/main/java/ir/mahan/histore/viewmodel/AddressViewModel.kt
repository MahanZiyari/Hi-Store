package ir.mahan.histore.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mahan.histore.data.model.address.BodySubmitAddress
import ir.mahan.histore.data.model.address.ResponseProfileAddresses
import ir.mahan.histore.data.model.address.ResponseProvinceList
import ir.mahan.histore.data.model.address.ResponseSubmitAddress
import ir.mahan.histore.data.model.profile.userComment.ResponseDeleteComment
import ir.mahan.histore.data.repository.AddressRepository
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.network.ResponseHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(private val repository: AddressRepository) : ViewModel() {

    // Addresses
    private val _userAddressesLiveData = MutableLiveData<NetworkResult<ResponseProfileAddresses>>()
    val userAddressesLiveData: LiveData<NetworkResult<ResponseProfileAddresses>> = _userAddressesLiveData

    fun getUserAddresses() = viewModelScope.launch {
        _userAddressesLiveData.value = NetworkResult.Loading()
        val response = repository.getProfileAddressesList()
        _userAddressesLiveData.value = ResponseHandler(response).generateNetworkResult()
    }


    // province List
    private val _provinceListLiveData = MutableLiveData<NetworkResult<ResponseProvinceList>>()
    val provinceListLiveData: LiveData<NetworkResult<ResponseProvinceList>> = _provinceListLiveData

    fun getProvinceList() = viewModelScope.launch {
        _provinceListLiveData.value = NetworkResult.Loading()
        val response = repository.getProvinceList()
        _provinceListLiveData.value = ResponseHandler(response).generateNetworkResult()
    }

    // city List
    private val _cityListLiveData = MutableLiveData<NetworkResult<ResponseProvinceList>>()
    val cityListLiveData: LiveData<NetworkResult<ResponseProvinceList>> = _cityListLiveData

    fun getCitiesList(provinceId: Int) = viewModelScope.launch {
        _cityListLiveData.value = NetworkResult.Loading()
        val response = repository.getCityList(provinceId)
        _cityListLiveData.value = ResponseHandler(response).generateNetworkResult()
    }

    // Submit Address
    private val _submitAddressData = MutableLiveData<NetworkResult<ResponseSubmitAddress>>()
    val submitAddressData: LiveData<NetworkResult<ResponseSubmitAddress>> = _submitAddressData

    fun submitAddress(body: BodySubmitAddress) = viewModelScope.launch {
        _submitAddressData.value = NetworkResult.Loading()
        val response = repository.postSubmitAddress(body)
        _submitAddressData.value = ResponseHandler(response).generateNetworkResult()
    }

    // Delete Address
    private val _deleteAddressResult = MutableLiveData<NetworkResult<ResponseDeleteComment>>()
    val deleteAddressResult: LiveData<NetworkResult<ResponseDeleteComment>> = _deleteAddressResult

    fun deleteAddress(addressId: Int) = viewModelScope.launch {
        _deleteAddressResult.value = NetworkResult.Loading()
        val response = repository.deleteProfileAddress(addressId)
        _deleteAddressResult.value = ResponseHandler(response).generateNetworkResult()
    }

}