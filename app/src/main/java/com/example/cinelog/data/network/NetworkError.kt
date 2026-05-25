package com.example.cinelog.data.network

sealed class NetworkError {
    object NoConnection : NetworkError()
    object Timeout : NetworkError()
    object ServerError : NetworkError()
    object Unknown : NetworkError()
}

fun classifyError(e: Exception): NetworkError = when (e) {
    is java.net.UnknownHostException, is java.net.ConnectException -> NetworkError.NoConnection
    is java.net.SocketTimeoutException -> NetworkError.Timeout
    is retrofit2.HttpException -> NetworkError.ServerError
    else -> NetworkError.Unknown
}
