package com.tvstreaming.app.core.api.exceptions

import java.io.IOException

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause)

class NetworkException(message: String, cause: Throwable? = null) : AppException(message, cause)

class ApiException(message: String, val code: Int) : AppException(message)

class UnauthorizedException(message: String) : AppException(message)

class NoNetworkException : IOException("No network connection available")