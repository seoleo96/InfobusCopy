package com.example.infobuscopy.presentation.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.infobuscopy.R

@Composable
fun SecondScreen() {
    var bikeState by remember { mutableStateOf(BikePosition.Start) }

    val offsetAnimation : IntOffset by animateIntOffsetAsState(
        targetValue = if (bikeState == BikePosition.Start) IntOffset.Zero else IntOffset(20, 300),
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "",
    )
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.navigation_drop_blue),
            contentDescription = null,
            modifier = Modifier
                .height(90.dp)
                .absoluteOffset{
                    offsetAnimation
                }
        )
        Button(
            onClick = {
                bikeState = when (bikeState) {
                    BikePosition.Start -> BikePosition.Finish
                    BikePosition.Finish -> BikePosition.Start
                }
            }, modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(align = Alignment.Center)
        ) {
            Text(text = "Ride")
        }
    }
}

enum class BikePosition {
    Start, Finish
}