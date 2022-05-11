package ru.skillbranch.common.network.retrofit2

import kotlin.reflect.KClass

public object TestConnector : RetrofitService.Connector<TestAPI> {
    override val baseUrl: String = "http://127.0.0.1:1080/"
    override val api: KClass<TestAPI> = TestAPI::class
}
