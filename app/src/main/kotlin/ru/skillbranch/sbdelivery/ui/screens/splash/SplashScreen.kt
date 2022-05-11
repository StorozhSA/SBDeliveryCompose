package ru.skillbranch.sbdelivery.ui.screens.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import ru.skillbranch.sbdelivery.AppTheme
import ru.skillbranch.sbdelivery.R

@Composable
public fun SplashScreen(
    vm: SplashViewModel = viewModel(),
    nextScreen: () -> Unit
) {
    val state = vm.state.collectAsState().value
    val scale = remember { Animatable(0f) }

    // Animation
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(
                durationMillis = state.animationDuration,
                easing = { OvershootInterpolator(5f).getInterpolation(it) }
            )
        )
        // Customize the delay time
        delay(state.afterAnimationDelay.toLong())
        nextScreen.invoke()
    }

    // Image
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_bg),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize()
        )
        Image(
            painter = painterResource(id = R.drawable.ic_splash_text),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
    }
}

@Preview
@Composable
public fun SplashScreenPreview() {
    AppTheme {
        SplashScreen {}
    }
}
