package ru.skillbranch.sbdelivery.hilt

import androidx.navigation.NavHostController
import ru.skillbranch.sbdelivery.IAppSharedPreferences
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginFeature
import ru.skillbranch.sbdelivery.ui.screens.profile.profile.ProfileFeature
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class SecurityProxy @Inject constructor(
    @DIAppSharedPreferences private val appSharedPreferences: IAppSharedPreferences
) {
    private val securityIfNotLogged: List<String> = listOf(
        OrderListFeature.target,
        ProfileFeature.target,
    )

    public fun check(nc: NavHostController, originalRoute: String, currentRoute: String) {
        if (appSharedPreferences.userId.isBlank() && securityIfNotLogged.contains(originalRoute)) {
            nc.navigate(LoginFeature.targetWithArgs(currentRoute, originalRoute)) {
                launchSingleTop = true
            }
        } else {
            nc.navigate(originalRoute) { launchSingleTop = true }
        }
    }
}
