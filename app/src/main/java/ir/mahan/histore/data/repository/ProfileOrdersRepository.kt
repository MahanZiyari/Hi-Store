package ir.mahan.histore.data.repository

import ir.mahan.histore.data.api.APIEndpoints
import javax.inject.Inject

class ProfileOrdersRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun getProfileOrdersList(status: String) = api.getProfileOrdersList(status)
}