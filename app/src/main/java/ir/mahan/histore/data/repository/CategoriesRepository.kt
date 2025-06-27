package ir.mahan.histore.data.repository

import ir.mahan.histore.data.api.APIEndpoints
import javax.inject.Inject

class CategoriesRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun getCategoriesList() = api.fetchCategoriesList()
}