package ir.mahan.histore.data.model.profile.userComment


import com.google.gson.annotations.SerializedName

data class ResponseDeleteComment(
    @SerializedName("message")
    val message: String? // success
)