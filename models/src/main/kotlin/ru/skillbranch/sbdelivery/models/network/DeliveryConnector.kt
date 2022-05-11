package ru.skillbranch.sbdelivery.models.network

import ru.skillbranch.common.network.retrofit2.RetrofitService
import ru.skillbranch.sbdelivery.models.BuildConfig
import kotlin.reflect.KClass

public object DeliveryConnector : RetrofitService.Connector<DeliveryAPI> {
    override val baseUrl: String = BuildConfig.SERVER

    // override val baseUrl: String = "http://127.0.0.1:1080/"
    override val api: KClass<DeliveryAPI> = DeliveryAPI::class
}
