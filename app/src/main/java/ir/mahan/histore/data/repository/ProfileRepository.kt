package ir.mahan.histore.data.repository


import ir.mahan.histore.data.api.APIEndpoints
import ir.mahan.histore.data.model.login.BodyLogin
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun getProfileData() = api.getProfileData()
}