package ir.mahan.histore.data.repository

import ir.mahan.histore.data.api.APIEndpoints
import ir.mahan.histore.data.model.address.BodySubmitAddress
import javax.inject.Inject

class AddressRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun getProfileAddressesList() = api.getProfileAddressesList()
    suspend fun getProvinceList() = api.getProvinceList()
    suspend fun getCityList(id: Int) = api.getCityList(id)
    suspend fun postSubmitAddress(body: BodySubmitAddress) = api.postSubmitAddress(body)
    suspend fun deleteProfileAddress(id: Int) = api.deleteProfileAddress(id)
}