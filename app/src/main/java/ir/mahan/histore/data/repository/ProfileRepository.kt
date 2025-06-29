package ir.mahan.histore.data.repository


import ir.mahan.histore.data.api.APIEndpoints
import ir.mahan.histore.data.model.login.BodyLogin
import okhttp3.RequestBody
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun getProfileData() = api.getProfileData()
    suspend fun postAvtar(body: RequestBody) = api.postAvatar(body)
}