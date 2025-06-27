package ir.mahan.histore.util.network

import com.google.gson.Gson
import retrofit2.Response

// TODO: Write Kdoc Comment for this Class
open class ResponseHandler<T>(private val response: Response<T>) {

    open fun generateNetworkResult(): NetworkResult<T> {
        return when {
            response.code() == 401 -> NetworkResult.Error("You are not authorized")
            /*response.code() == 422 -> {
                var errorMessage = ""
                if (response.errorBody() != null) {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                    //val message = errorResponse.message
                    val errors = errorResponse.errors
                    errors?.forEach { (_, fieldError) ->
                        errorMessage = fieldError.joinToString()
                    }
                }
                NetworkResult.Error(errorMessage)
            }*/

            response.code() == 500 -> NetworkResult.Error("Try again")
            response.isSuccessful -> NetworkResult.Success(response.body()!!)
            else -> NetworkResult.Error(response.message())
        }
    }
}