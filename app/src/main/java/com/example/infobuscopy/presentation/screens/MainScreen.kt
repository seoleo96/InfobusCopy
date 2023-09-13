package com.example.infobuscopy.presentation.screens

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.infobuscopy.R
import com.example.infobuscopy.data.model.BusRouteModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainActivity"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel = koinViewModel()) {
    val mainState = mainViewModel.mainState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        mainViewModel.error.collect {
            if (it) {
                snackbarHostState.showSnackbar(
                    message = "Нет интернета или что то пошло не так",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        MainContent(mainState.routes, mainState.polyLines)
        it
    }
}


@Composable
fun MainContent(routes: List<BusRouteModel>, polyLines: List<LatLng>) {
    val cameraPositionLatLng = LatLng(routes.lastOrNull()?.lat ?: 54.90936, routes.lastOrNull()?.lon ?: 69.13374)
    val cameraPositionState = rememberCameraPositionState {
        CameraPositionState(position = CameraPosition.fromLatLngZoom(cameraPositionLatLng, 11.5f))
    }

    LaunchedEffect(key1 = true) {
        delay(1000)
        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition(cameraPositionLatLng, 11.5f, 0f, 0f)
            ),
            durationMs = 1000
        )
    }

    val icon = remember {
        mutableStateOf<BitmapDescriptor?>(null)
    }
    val context = LocalContext.current
    Box {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                icon.value = bitmapDescriptorFromVector(context, R.drawable.navigation_drop_blue)
            }
        ) {
            Polyline(
                points = polyLines, color = Color.Blue, visible = true, width = 6f, startCap = RoundCap()
            )
            routes.forEach {
                Marker(
                    state = MarkerState(position = LatLng(it.lat, it.lon)),
                    title = it.name,
                    rotation = it.direction.toFloat(),
                    icon = icon.value,
                )
            }
        }
        AnimatedVisibility(visible = routes.isEmpty()) {
            Box(
                modifier = Modifier
                    .background(color = Color.Gray.copy(alpha = 0.3f))
                    .fillMaxSize()
                    .pointerInput(Unit) {},
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .background(color = Color.Gray, shape = RoundedCornerShape(20.dp))
                        .clip(shape = RoundedCornerShape(30))
                        .padding(all = 16.dp)
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int,
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, 80, 80)
    val bm = Bitmap.createBitmap(
        80,
        80,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}