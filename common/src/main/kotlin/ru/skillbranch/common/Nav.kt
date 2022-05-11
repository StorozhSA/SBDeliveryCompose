package ru.skillbranch.common

import android.os.Bundle
import androidx.navigation.NavDirections
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

public sealed class Nav {
    public abstract fun navigate(nc: NavHostController)

    public sealed class To : Nav() {
        public abstract val args: Bundle?
        public abstract val options: NavOptions?
        public abstract val extras: Navigator.Extras?

        public data class ResId(
            val destination: Int,
            override val args: Bundle? = null,
            override val options: NavOptions? = null,
            override val extras: Navigator.Extras? = null
        ) : To() {
            override fun navigate(nc: NavHostController) {
                nc.navigate(
                    resId = destination,
                    navOptions = options,
                    navigatorExtras = extras,
                    args = args
                )
            }
        }

        public data class Route(
            val destination: String,
            override val args: Bundle? = null,
            override val options: NavOptions? = null,
            override val extras: Navigator.Extras? = null
        ) : To() {
            override fun navigate(nc: NavHostController) {
                nc.navigate(
                    route = destination,
                    navOptions = options,
                    navigatorExtras = extras
                )
            }
        }

        public data class Direction(
            val destination: NavDirections
        ) : To() {
            override val args: Bundle? = null
            override val options: NavOptions? = null
            override val extras: Navigator.Extras? = null

            override fun navigate(nc: NavHostController) {
                nc.navigate(directions = destination)
            }
        }
    }

    public sealed class PopBackStack : Nav() {
        public abstract val inclusive: Boolean

        public data class ResId(
            val destination: Int = 0,
            override val inclusive: Boolean = false
        ) : PopBackStack() {
            override fun navigate(nc: NavHostController) {
                if (destination == 0) {
                    nc.popBackStack()
                } else {
                    nc.popBackStack(
                        destinationId = destination,
                        inclusive = inclusive
                    )
                }
            }
        }

        public data class Route(
            val destination: String = "",
            override val inclusive: Boolean = false
        ) : PopBackStack() {
            override fun navigate(nc: NavHostController) {
                if (destination.isBlank()) {
                    nc.popBackStack()
                } else {
                    nc.popBackStack(
                        route = destination,
                        inclusive = inclusive
                    )
                }
            }
        }

    }

    public data class Position(
        val destination: String,
        val args: Bundle? = null,
        val options: NavOptions? = null,
        val extras: Navigator.Extras? = null
    ) : Nav() {
        override fun navigate(nc: NavHostController) {
            /*nc.navigate(
                route = destination,
                navOptions = options,
                navigatorExtras = extras
            )*/
        }
    }
}
