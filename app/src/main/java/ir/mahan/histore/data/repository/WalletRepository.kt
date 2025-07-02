package ir.mahan.histore.data.repository

import ir.mahan.histore.data.api.APIEndpoints
import ir.mahan.histore.data.model.wallet.BodyIncreaseWallet
import javax.inject.Inject

class WalletRepository @Inject constructor(private val api: APIEndpoints) {
    suspend fun fetchWalletAmount() = api.fetchWalletAmount()
    suspend fun postIncreaseWallet(body: BodyIncreaseWallet) = api.postIncreaseWallet(body)
}