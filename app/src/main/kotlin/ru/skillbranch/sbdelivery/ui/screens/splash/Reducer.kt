package ru.skillbranch.sbdelivery.ui.screens.splash

import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature.State

public fun State.selfReduce(msg: Msg): Pair<State, Set<Eff>> =
    when (msg) {
        is Msg.Msg1 -> {
            copy(x1 = "X1", x2 = "X2") to setOf(Eff.ActionX1("xxxx1"))
        }
        is Msg.Msg2 -> {
            copy(x1 = "X2", x2 = "X1") to setOf(Eff.ActionX2("xxxx2"))
        }
    }
