package ir.mahan.histore.data.model.profile


import com.google.gson.annotations.SerializedName

data class ResponseWallet(
    @SerializedName("wallet")
    val wallet: String? // 450000
)