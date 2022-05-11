package ru.skillbranch.common.network.retrofit2

import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

public object TestAuthenticator :
    RetrofitService.Authenticator<TestConnector, TestAPI, TestErrors> {
    private var accessToken: String = "NEW_ACCESS_TOKEN"
    override var ownerService: RetrofitService<TestConnector, TestAPI, TestErrors>? = null

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            val res = ownerService!!.api.refreshSynchronous("REFRESH_TOKEN").execute()
            if (res.isSuccessful) {
                accessToken = res.body().toString()
                return prepareRequest(response)
            }
        }
        return null
    }

    private fun prepareRequest(response: Response): Request =
        response.request.newBuilder().header("Authorization", "Bearer $accessToken").build()
}
