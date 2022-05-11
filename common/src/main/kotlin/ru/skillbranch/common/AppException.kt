package ru.skillbranch.common

public class AppException(
    public val appCode: String,
    message: String? = appCode,
    cause: Throwable? = null,
    enableSuppression: Boolean = false,
    writableStackTrace: Boolean = false
) : Throwable(message, cause, enableSuppression, writableStackTrace)
