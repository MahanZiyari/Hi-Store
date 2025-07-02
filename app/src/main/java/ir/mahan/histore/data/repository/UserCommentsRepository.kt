package ir.mahan.histore.data.repository

import ir.mahan.histore.data.api.APIEndpoints
import javax.inject.Inject

class UserCommentsRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun fetchUserComments() = api.fetchUserComments()
    suspend fun deleteUserComment(id: Int) = api.deleteUserComment(id)
}