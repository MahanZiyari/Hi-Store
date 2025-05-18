package ir.mahan.histore.util.network

sealed class NetworkResult<T>(val data: T? = null, val error: String? = null) {
    class Loading<T> : NetworkResult<T>()
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String) : NetworkResult<T>(error = message)
}