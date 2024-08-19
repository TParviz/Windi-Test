package ru.winditest.core

import android.util.Log
import retrofit2.HttpException
import ru.winditest.R
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ResponseHandler {
    suspend operator fun <T> invoke(block: suspend () -> T) = try {
        Resource.Success(block())
    } catch (e: Exception) {
        Log.e(javaClass.name, e.toString())
        val errorCode = when (e) {
            is HttpException -> e.code()
            is SocketTimeoutException -> ErrorCodes.SocketTimeOut.code
            is UnknownHostException,
            is ConnectException -> ErrorCodes.UnknownHost.code

            else -> Int.MAX_VALUE
        }
        Resource.Error(getErrorMessage(errorCode))
    }

    private fun getErrorMessage(code: Int) = when (code) {
        ErrorCodes.UnknownHost.code -> UiText.StringResource(R.string.no_connection)
        ErrorCodes.SocketTimeOut.code -> UiText.StringResource(R.string.timeout)
        401 -> UiText.StringResource(R.string.unauthorized)
        404 -> UiText.StringResource(R.string.not_found)
        in 400..499 -> UiText.StringResource(R.string.invalid_data)
        in 500..599 -> UiText.StringResource(R.string.server_error)
        else -> UiText.StringResource(R.string.unknown_error)
    }


    private enum class ErrorCodes(val code: Int) {
        SocketTimeOut(-1),
        UnknownHost(-2)
    }
}