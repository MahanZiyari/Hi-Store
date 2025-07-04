package ir.mahan.histore.data.repository

import ir.mahan.histore.data.api.APIEndpoints
import javax.inject.Inject

class FavoritesRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun fetchUserFavorites() = api.fetchUserFavorites()
    suspend fun deleteUserFavorite(id: Int) = api.deleteUserFavorite(id)
}