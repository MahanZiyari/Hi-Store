package ir.mahan.histore.data.model.address

import com.google.gson.annotations.SerializedName

class ResponseProvinceList : ArrayList<ResponseProvinceList.ResponseProvinceListItem>(){
    data class ResponseProvinceListItem(
        @SerializedName("id")
        val id: Int?, // 1
        @SerializedName("title")
        val title: String? // آذربایجان شرقی
    )
}