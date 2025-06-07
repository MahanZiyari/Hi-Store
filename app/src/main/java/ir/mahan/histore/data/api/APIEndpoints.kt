package ir.mahan.histore.data.api


import ir.mahan.histore.data.model.home.ResponseBanners
import ir.mahan.histore.data.model.home.ResponseDiscount
import ir.mahan.histore.data.model.home.ResponseProducts
import ir.mahan.histore.data.model.login.BodyLogin
import ir.mahan.histore.data.model.login.ResponseLogin
import ir.mahan.histore.data.model.login.ResponseVerify
import ir.mahan.histore.data.model.profile.ResponseProfile
import ir.mahan.histore.util.constants.PATH_SLUG
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface APIEndpoints {

    @POST("auth/login")
    suspend fun postLogin(@Body body: BodyLogin): Response<ResponseLogin>

    @POST("auth/login/verify")
    suspend fun postVerify(@Body body: BodyLogin): Response<ResponseVerify>

    @GET("auth/detail")
    suspend fun getProfileData(): Response<ResponseProfile>

    @GET("ad/swiper/{slug}")
    suspend fun getBannersList(@Path(PATH_SLUG) slug: String): Response<ResponseBanners>

    @GET("offers/discount/{slug}")
    suspend fun getDiscountList(@Path(PATH_SLUG) slug: String): Response<ResponseDiscount>

    @GET("category/pro/{slug}")
    suspend fun getProductsList(@Path(PATH_SLUG) slug: String, @QueryMap queries: Map<String, String>): Response<ResponseProducts>
}