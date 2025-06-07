package ir.mahan.histore.data.repository


import ir.mahan.histore.data.api.APIEndpoints
import javax.inject.Inject

class HomeRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun getBannersList(slug: String) = api.getBannersList(slug)
    suspend fun getDiscountItems(slug: String) = api.getDiscountList(slug)
    suspend fun getProductsList(slug: String, productsQueries: Map<String, String>) = api.getProductsList(slug, productsQueries)
}