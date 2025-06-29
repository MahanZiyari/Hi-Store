package ir.mahan.histore.data.repository

import ir.mahan.histore.data.api.APIEndpoints
import javax.inject.Inject

class WalletRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun fetchWalletAmount() = api.fetchWalletAmount()
}