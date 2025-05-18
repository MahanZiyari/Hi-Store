package ir.mahan.histore.data.api


import ir.mahan.histore.data.model.login.BodyLogin
import ir.mahan.histore.data.model.login.ResponseLogin
import ir.mahan.histore.data.model.login.ResponseVerify
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIEndpoints {

    @POST("auth/login")
    suspend fun postLogin(@Body body: BodyLogin): Response<ResponseLogin>

    @POST("auth/login/verify")
    suspend fun postVerify(@Body body: BodyLogin): Response<ResponseVerify>
}