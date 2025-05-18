package ir.mahan.histore.data.repository


import ir.mahan.histore.data.api.APIEndpoints
import ir.mahan.histore.data.model.login.BodyLogin
import javax.inject.Inject

class LoginRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun postLogin(body: BodyLogin) = api.postLogin(body)
    suspend fun postVerify(body: BodyLogin) = api.postVerify(body)
}