package ru.skillbranch.sbdelivery

import ru.skillbranch.common.network.retrofit2.RetrofitService

@Suppress("FunctionName")
public interface IAppErrors : RetrofitService.DefaultErrors {
    public fun E_LOGIN_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_LOGIN_FAILED")
    }

    public fun E_LOGIN_ERROR(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_LOGIN_ERROR")
    }

    public fun E_NOT_FOUND(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_NOT_FOUND")
    }

    public fun E_REG_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_REG_FAILED")
    }

    public fun E_REG_EXISTS(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_REG_EXISTS")
    }

    public fun E_RECOVERY_EMAIL_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_RECOVERY_EMAIL_FAILED")
    }

    public fun E_RECOVERY_EMAIL_LESS_TIME(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_RECOVERY_EMAIL_LESS_TIME")
    }

    public fun E_RECOVERY_CODE_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_RECOVERY_CODE_FAILED")
    }

    public fun E_RECOVERY_CODE_WRONG(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_RECOVERY_CODE_WRONG")
    }

    public fun E_RECOVERY_PASSWORD_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_RECOVERY_PASSWORD_FAILED")
    }

    public fun E_RECOVERY_PASSWORD_EXPIRED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_RECOVERY_PASSWORD_EXPIRED")
    }

    public fun E_REFRESH_TOKEN_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_REFRESH_TOKEN_FAILED")
    }

    public fun E_REFRESH_TOKEN_EXPIRED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_REFRESH_TOKEN_EXPIRED")
    }

    public fun E_PROFILE_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_PROFILE_FAILED")
    }

    public fun E_PASSWORD_CHANGE_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_PASSWORD_CHANGE_FAILED")
    }

    public fun E_FAVORITE_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_FAVORITE_FAILED")
    }

    public fun E_GET_RECOMMENDED_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_GET_RECOMMENDED_FAILED")
    }

    public fun E_GET_CATEGORIES_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_GET_CATEGORIES_FAILED")
    }

    public fun E_GET_DISHES_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_GET_DISHES_FAILED")
    }

    public fun E_GET_REVIEWS_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_GET_REVIEWS_FAILED")
    }

    public fun E_ADD_REVIEWS_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_ADD_REVIEWS_FAILED")
    }

    public fun E_GET_OR_UPDATE_CART_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_GET_OR_UPDATE_CART_FAILED")
    }

    public fun E_CHECK_ADDRESS_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_CHECK_ADDRESS_FAILED")
    }

    public fun E_ORDER_NEW_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_ORDER_NEW_FAILED")
    }

    public fun E_GET_ORDERS_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_GET_ORDERS_FAILED")
    }

    public fun E_GET_ORDERS_STATUSES_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_GET_ORDERS_STATUSES_FAILED")
    }

    public fun E_ORDER_CANCEL_FAILED(): RetrofitService.ErrorItem {
        return RetrofitService.ErrorItem("E_ORDER_CANCEL_FAILED")
    }
}

public class AppErrors : IAppErrors
