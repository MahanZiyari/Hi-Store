package ir.mahan.histore.data.api


import ir.mahan.histore.data.model.home.ResponseBanners
import ir.mahan.histore.data.model.home.ResponseDiscount
import ir.mahan.histore.data.model.login.BodyLogin
import ir.mahan.histore.data.model.login.ResponseLogin
import ir.mahan.histore.data.model.login.ResponseVerify
import ir.mahan.histore.data.model.profile.ResponseProfile
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIEndpoints {

    @POST("auth/login")
    suspend fun postLogin(@Body body: BodyLogin): Response<ResponseLogin>

    @POST("auth/login/verify")
    suspend fun postVerify(@Body body: BodyLogin): Response<ResponseVerify>

    @GET("auth/detail")
    suspend fun getProfileData(): Response<ResponseProfile>

    @GET("ad/swiper/{slug}")
    suspend fun getBannersList(@Path("slug") slug: String): Response<ResponseBanners>

    @GET("offers/discount/{slug}")
    suspend fun getDiscountList(@Path("slug") slug: String): Response<ResponseDiscount>
}