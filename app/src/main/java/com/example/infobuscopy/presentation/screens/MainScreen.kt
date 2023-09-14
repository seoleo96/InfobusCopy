package com.example.infobuscopy.presentation.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.infobuscopy.data.model.BusRouteModel
import com.example.infobuscopy.util.LruCacheImpl
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
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainActivity"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel = koinViewModel(), lruCacheImpl: LruCacheImpl = get()) {
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
        MainContent(lruCacheImpl, mainState.routes14, mainState.polyLines14)
        it
    }
}

@Composable
fun MainContent(lruCacheImpl: LruCacheImpl, routes: List<BusRouteModel>, polyLines: List<LatLng>) {
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

        while (true){
            lruCacheImpl.getBitmapDescriptor().let {
                Log.e("LruCache", "MainContent: $it")
            }
            delay(2000)
        }
    }

    val icon = remember {
        mutableStateOf<BitmapDescriptor?>(null)
    }
    Box {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                icon.value = lruCacheImpl.getBitmapDescriptor()?.let {
                    BitmapDescriptorFactory.fromBitmap(it)
                }
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
                    visible = icon.value != null
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