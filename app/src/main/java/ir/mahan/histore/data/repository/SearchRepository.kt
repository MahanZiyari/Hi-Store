package ir.mahan.histore.data.repository


import ir.mahan.histore.data.api.APIEndpoints
import ir.mahan.histore.data.model.login.BodyLogin
import javax.inject.Inject

class SearchRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun searchProducts(queries: Map<String, String>) = api.searchProducts(queries)
}