package ru.skillbranch.sbdelivery.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.IAppSharedPreferences
import ru.skillbranch.sbdelivery.hilt.DIAppSharedPreferences
import ru.skillbranch.sbdelivery.ui.screens.root.RootFeature
import ru.skillbranch.sbdelivery.ui.screens.root.RootScreen
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashScreen
import javax.inject.Inject


@AndroidEntryPoint
public class MainActivity : ComponentActivity() {

    @Inject
    @DIAppSharedPreferences
    public lateinit var appSharedPreferences: IAppSharedPreferences

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val nc = rememberNavController()

                NavHost(nc, SplashFeature.target) {

                    // Splash Screen
                    composable(SplashFeature.target) {
                        SplashScreen(
                            vm = hiltViewModel(),
                            nextScreen = {
                                nc.navigate(RootFeature.target) {
                                    popUpTo(SplashFeature.target) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    // Root Screen (next after Splash screen)
                    composable(RootFeature.target) {
                        RootScreen()
                    }
                }
            }
        }
    }
}




