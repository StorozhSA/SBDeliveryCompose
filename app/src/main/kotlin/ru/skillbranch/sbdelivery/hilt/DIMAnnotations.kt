package ru.skillbranch.sbdelivery.hilt

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIAppScopeHolder

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIAppScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIAppPoints

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIAppErrors

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIAppDefaultExceptionHandler

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIAppSharedPreferences

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIDeliveryServiceNetwork

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIDeliveryServiceDataBase

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIDeliveryAuthenticator

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIDefaultRetrofitCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIDefaultNetworkMonitor

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIDefaultJSONFactoryJackson

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIDefaultJSONFactoryMoshi

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DIDefaultJSONFactoryKotlin
