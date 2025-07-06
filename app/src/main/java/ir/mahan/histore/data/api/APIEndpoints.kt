package ir.mahan.histore.data.api


import ir.mahan.histore.data.model.address.BodySubmitAddress
import ir.mahan.histore.data.model.address.ResponseProfileAddresses
import ir.mahan.histore.data.model.address.ResponseProvinceList
import ir.mahan.histore.data.model.address.ResponseSubmitAddress
import ir.mahan.histore.data.model.categories.ResponseCategories
import ir.mahan.histore.data.model.home.ResponseBanners
import ir.mahan.histore.data.model.home.ResponseDiscount
import ir.mahan.histore.data.model.home.ResponseProducts
import ir.mahan.histore.data.model.login.BodyLogin
import ir.mahan.histore.data.model.login.ResponseLogin
import ir.mahan.histore.data.model.login.ResponseVerify
import ir.mahan.histore.data.model.profile.BodyUpdateProfile
import ir.mahan.histore.data.model.profile.ResponseProfile
import ir.mahan.histore.data.model.profile.ResponseWallet
import ir.mahan.histore.data.model.profile.favorites.ResponseProfileFavorites
import ir.mahan.histore.data.model.profile.userComment.ResponseDeleteComment
import ir.mahan.histore.data.model.profile.userComment.ResponseProfileComments
import ir.mahan.histore.data.model.search.ResponseSearch
import ir.mahan.histore.data.model.wallet.BodyIncreaseWallet
import ir.mahan.histore.data.model.wallet.ResponseIncreaseWallet
import ir.mahan.histore.util.constants.PATH_SLUG
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
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

    @GET("search")
    suspend fun searchProducts(@QueryMap queries: Map<String, String>): Response<ResponseSearch>

    @GET("menu")
    suspend fun fetchCategoriesList(): Response<ResponseCategories>

    @GET("auth/wallet")
    suspend fun fetchWalletAmount(): Response<ResponseWallet>


    /**
     * Post avatar to server
     * It Uses RequestBody for creating dynamic  body
     *
     * @param body:RequestBody
     * @return
     */
    @POST("auth/avatar")
    suspend fun postAvatar(@Body body: RequestBody): Response<Unit>

    @PUT("auth/update")
    suspend fun postUploadProfile(@Body body: BodyUpdateProfile): Response<ResponseProfile>

    @POST("auth/wallet")
    suspend fun postIncreaseWallet(@Body body: BodyIncreaseWallet): Response<ResponseIncreaseWallet>

    @GET("auth/comments")
    suspend fun fetchUserComments(): Response<ResponseProfileComments>

    @DELETE("auth/comment/{id}")
    suspend fun deleteUserComment(@Path("id") id: Int): Response<ResponseDeleteComment>

    @GET("auth/favorites")
    suspend fun fetchUserFavorites(): Response<ResponseProfileFavorites>

    @DELETE("auth/favorite/{id}")
    suspend fun deleteUserFavorite(@Path("id") id: Int): Response<ResponseDeleteComment>

    @GET("auth/addresses")
    suspend fun getProfileAddressesList(): Response<ResponseProfileAddresses>

    @GET("auth/address/provinces")
    suspend fun getProvinceList(): Response<ResponseProvinceList>

    @GET("auth/address/provinces")
    suspend fun getCityList(@Query("provinceId") id: Int): Response<ResponseProvinceList>

    @POST("auth/address")
    suspend fun postSubmitAddress(@Body body: BodySubmitAddress): Response<ResponseSubmitAddress>

    @DELETE("auth/address/remove/{id}")
    suspend fun deleteProfileAddress(@Path("id") id: Int): Response<ResponseDeleteComment>
}