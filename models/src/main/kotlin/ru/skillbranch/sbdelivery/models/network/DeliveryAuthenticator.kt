package ru.skillbranch.sbdelivery.models.network

import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.skillbranch.common.ISharedPreferences
import ru.skillbranch.common.extension.manage
import ru.skillbranch.common.network.retrofit2.RetrofitService
import ru.skillbranch.common.network.retrofit2.RetrofitService.Authenticator
import ru.skillbranch.sbdelivery.models.network.domains.ReqRecoveryToken

public class DeliveryAuthenticator<T : RetrofitService.DefaultErrors>(pref: ISharedPreferences) :
    Authenticator<DeliveryConnector, DeliveryAPI, T> {
    private var refreshToken: String by pref.store.manage("")
    private var accessToken: String by pref.store.manage("")
    override var ownerService: RetrofitService<DeliveryConnector, DeliveryAPI, T>? =
        null

    override fun authenticate(route: Route?, response: Response): Request? {
        // This is a synchronous call
        if (response.code == 401 && ownerService != null) {
            val res = ownerService!!.api
                .refreshSynchronous(ReqRecoveryToken(refreshToken))
                .execute()
            if (res.isSuccessful) {
                accessToken = res.body()!!.accessToken
                return prepareRequest(response)
            }
        }
        return null
    }

    private fun prepareRequest(response: Response): Request =
        response.request.newBuilder().header("Authorization", "Bearer $accessToken").build()
}
