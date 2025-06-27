package ir.mahan.histore.data.repository


import ir.mahan.histore.data.api.APIEndpoints
import javax.inject.Inject

class CatProductRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun getProductsList(slug: String, productsQueries: Map<String, String>) = api.getProductsList(slug, productsQueries)
}