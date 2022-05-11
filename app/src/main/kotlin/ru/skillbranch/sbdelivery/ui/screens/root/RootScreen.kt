package ru.skillbranch.sbdelivery.ui.screens.root

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.collectLatest
import ru.skillbranch.common.Nav
import ru.skillbranch.common.extension.logd
import ru.skillbranch.sbdelivery.CAT_ROOT_UI
import ru.skillbranch.sbdelivery.ext.base
import ru.skillbranch.sbdelivery.ui.components.NavigationDrawer
import ru.skillbranch.sbdelivery.ui.screens.address.map.AddressMapFeature
import ru.skillbranch.sbdelivery.ui.screens.address.map.AddressMapScreen
import ru.skillbranch.sbdelivery.ui.screens.address.text.AddressTextFeature
import ru.skillbranch.sbdelivery.ui.screens.address.text.AddressTextScreen
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarScreen
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarViewModel
import ru.skillbranch.sbdelivery.ui.screens.cart.CartFeature
import ru.skillbranch.sbdelivery.ui.screens.cart.CartScreen
import ru.skillbranch.sbdelivery.ui.screens.dish.DishFeature
import ru.skillbranch.sbdelivery.ui.screens.dish.DishScreen
import ru.skillbranch.sbdelivery.ui.screens.dish.DishViewModel
import ru.skillbranch.sbdelivery.ui.screens.favorites.FavoritesFeature
import ru.skillbranch.sbdelivery.ui.screens.favorites.FavoritesScreen
import ru.skillbranch.sbdelivery.ui.screens.home.HomeFeature
import ru.skillbranch.sbdelivery.ui.screens.home.HomeScreen
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuScreen
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuViewModel
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListScreen
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderFeature
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderScreen
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderViewModel
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingFeature
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingScreen
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginFeature
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginScreen
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginViewModel
import ru.skillbranch.sbdelivery.ui.screens.profile.profile.ProfileFeature
import ru.skillbranch.sbdelivery.ui.screens.profile.profile.ProfileScreen
import ru.skillbranch.sbdelivery.ui.screens.profile.registration.RegistrationFeature
import ru.skillbranch.sbdelivery.ui.screens.profile.registration.RegistrationScreen
import ru.skillbranch.sbdelivery.ui.screens.recovery1.PassRecovery1Feature
import ru.skillbranch.sbdelivery.ui.screens.recovery1.PassRecovery1Screen
import ru.skillbranch.sbdelivery.ui.screens.recovery2.PassRecovery2Feature
import ru.skillbranch.sbdelivery.ui.screens.recovery2.PassRecovery2Screen
import ru.skillbranch.sbdelivery.ui.screens.recovery2.PassRecovery2ViewModel
import ru.skillbranch.sbdelivery.ui.screens.recovery3.PassRecovery3Feature
import ru.skillbranch.sbdelivery.ui.screens.recovery3.PassRecovery3Screen
import ru.skillbranch.sbdelivery.ui.screens.recovery3.PassRecovery3ViewModel
import ru.skillbranch.sbdelivery.ui.screens.search.SearchFeature
import ru.skillbranch.sbdelivery.ui.screens.search.SearchScreen

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
public fun RootScreen() {
    Log.d("RootScreen", "invoke")
    val vm: RootViewModel = hiltViewModel()
    val vmAppBar: AppBarViewModel = hiltViewModel()
    val nc = rememberNavController()
    val navBackStackEntry by nc.currentBackStackEntryAsState()
    val state = vm.state.collectAsState().value
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    var toggleDrawer by remember { mutableStateOf(false) }

    // Выдвижение Drawer
    fun onToggleDrawer() {
        toggleDrawer = !toggleDrawer
    }

    // Выдвижение Drawer
    LaunchedEffect(key1 = toggleDrawer) {
        scaffoldState.drawerState.run {
            if (toggleDrawer) open() else close()
        }
    }

    // Обработка сообщений навигации из других внешних мест
    LaunchedEffect(key1 = true) {
        vm.points.eventNav.i().collectLatest { it.navigate(nc) }
    }

    // Извещаем подписчиков () о текущем маршруте навигации
    LaunchedEffect(key1 = navBackStackEntry?.destination?.route) {
        navBackStackEntry?.destination?.route?.base()?.let {
            logd("Current position: $it")
            vm.points.stateNav.m().emit(Nav.Position(it))
        }
    }

    Scaffold(
        drawerGesturesEnabled = navBackStackEntry?.destination?.route?.base() ?: HomeFeature.target != AddressMapFeature.target,
        scaffoldState = scaffoldState,
        topBar = {
            AppBarScreen(
                vm = vmAppBar,
                onToggleDrawer = { onToggleDrawer() }
            )
        },
        drawerContent = {

            NavigationDrawer(
                menuItems = state.menuItems,
                currentRoute = navBackStackEntry?.destination?.route?.base() ?: HomeFeature.target,
                notificationCount = state.notificationCount,
                cartCount = state.cartCount,
                onLogout = {
                    vm.appSharedPreferences.apply {
                        userFirstName = ""
                        userLastName = ""
                        userEmail = ""
                        userId = ""
                    }
                    onToggleDrawer()
                },
                onSelect = {
                    onToggleDrawer()
                    vm.security.check(
                        nc,
                        it,
                        navBackStackEntry?.destination?.route?.base() ?: HomeFeature.target
                    )
                }
            )
        },
        content = {
            NavHost(
                navController = nc,
                startDestination = HomeFeature.target,
                modifier = Modifier
            ) {
                composable(HomeFeature.target) { HomeScreen(hiltViewModel()) }
                composable(FavoritesFeature.target) { FavoritesScreen(hiltViewModel()) }
                composable(CartFeature.target) { CartScreen(hiltViewModel()) }
                composable(SearchFeature.target) { SearchScreen(hiltViewModel()) }
                composable(RegistrationFeature.target) { RegistrationScreen(hiltViewModel()) }
                //composable(LoginFeature.target) { LoginScreen(hiltViewModel()) }
                composable(ProfileFeature.target) { ProfileScreen(hiltViewModel()) }
                composable(PassRecovery1Feature.target) { PassRecovery1Screen(hiltViewModel()) }
                composable(OrderProcessingFeature.target) { OrderProcessingScreen(hiltViewModel()) }
                composable(OrderListFeature.target) { OrderListScreen(hiltViewModel()) }
                composable(AddressTextFeature.target) { AddressTextScreen(hiltViewModel()) }
                composable(AddressMapFeature.target) { AddressMapScreen(hiltViewModel()) }

                composable(
                    route = LoginFeature.targetWithArgsTemplate,
                    arguments = listOf(
                        navArgument(LoginFeature.Args.prev) {
                            type = NavType.StringType
                            defaultValue = HomeFeature.target
                        },
                        navArgument(LoginFeature.Args.next) {
                            type = NavType.StringType
                            defaultValue = HomeFeature.target
                        }
                    )
                ) {
                    val vmLogin: LoginViewModel = hiltViewModel()
                    it.arguments?.getString(LoginFeature.Args.prev)?.let { prev ->
                        it.arguments?.getString(LoginFeature.Args.next)?.let { next ->
                            if (prev.isNotBlank() && next.isNotBlank()) {
                                vmLogin.mutate(LoginFeature.Msg.SetRoute(prev, next))
                            }
                        }
                    }
                    LoginScreen(vmLogin)
                }

                composable(
                    route = OrderFeature.targetWithArgsTemplate,
                    arguments = listOf(
                        navArgument(OrderFeature.Args.orderId) {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) {
                    val vmOrder: OrderViewModel = hiltViewModel()
                    it.arguments?.getString(OrderFeature.Args.orderId)?.let { orderId ->
                        if (orderId.isNotBlank()) {
                            vmOrder.mutate(OrderFeature.Msg.GetOrder(orderId))
                        }
                    }
                    OrderScreen(vmOrder)
                }

                composable(
                    route = PassRecovery2Feature.targetWithArgsTemplate,
                    arguments = listOf(
                        navArgument(PassRecovery2Feature.Args.email) {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) {
                    val vmPassRecovery2: PassRecovery2ViewModel = hiltViewModel()
                    it.arguments?.getString(PassRecovery2Feature.Args.email)?.let { email ->
                        if (email.isNotBlank()) {
                            vmPassRecovery2.mutate(PassRecovery2Feature.Msg.SetEmail(email))
                        }
                    }
                    PassRecovery2Screen(vmPassRecovery2)
                }

                composable(
                    route = PassRecovery3Feature.targetWithArgsTemplate,
                    arguments = listOf(
                        navArgument(PassRecovery3Feature.Args.email) {
                            type = NavType.StringType
                            defaultValue = ""
                        },
                        navArgument(PassRecovery3Feature.Args.code) {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) {
                    val vmPassRecovery3: PassRecovery3ViewModel = hiltViewModel()
                    it.arguments?.getString(PassRecovery3Feature.Args.email)?.let { email ->
                        if (email.isNotBlank()) {
                            vmPassRecovery3.mutate(PassRecovery3Feature.Msg.SetEmail(email))
                        }
                    }
                    it.arguments?.getString(PassRecovery3Feature.Args.code)?.let { code ->
                        if (code.isNotBlank()) {
                            vmPassRecovery3.mutate(PassRecovery3Feature.Msg.SetCode(code))
                        }
                    }
                    PassRecovery3Screen(vmPassRecovery3)
                }

                composable(
                    route = DishFeature.target + "/{dishId}",
                    arguments = listOf(
                        navArgument("dishId") {
                            type = NavType.StringType
                        }
                    )
                ) {
                    val vmDish: DishViewModel = hiltViewModel()
                    val dishId = it.arguments?.getString("dishId")
                    requireNotNull(dishId) { "dishId parameter wasn't found. Please make sure it's set!" }
                    vmDish.setDish(dishId)
                    DishScreen(vmDish)
                }

                composable(
                    route = MenuFeature.target + "?catId={catId}",
                    arguments = listOf(
                        navArgument("catId") {
                            type = NavType.StringType
                            defaultValue = CAT_ROOT_UI.id
                        }
                    )
                ) {
                    val vmMenu: MenuViewModel = hiltViewModel()
                    it.arguments?.getString("catId")?.let { catId ->
                        if (catId != CAT_ROOT_UI.id) {
                            vmMenu.mutate(MenuFeature.Msg.OpenCategory(catId))
                        }
                    }
                    MenuScreen(vmMenu)
                }
            }
        },
        drawerScrimColor = MaterialTheme.colors.primaryVariant.copy(alpha = DrawerDefaults.ScrimOpacity)
    )
}
