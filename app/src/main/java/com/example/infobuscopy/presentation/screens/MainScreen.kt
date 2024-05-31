package com.example.infobuscopy.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.infobuscopy.R
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

    val context = LocalContext.current
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

    if (mainState.routes14.isNotEmpty()) {
        mainState.routes14.forEach { busRouteModel: BusRouteModel ->
            val busNumber = "14"
            lruCacheImpl.saveBitmapDescriptor(
                context,
                R.drawable.navigation_drop_blue,
                busNumber,
                busRouteModel.invalidAdapted,
                busRouteModel.id.toString(),
            )
        }
    }
    if (mainState.routes101.isNotEmpty()) {
        mainState.routes101.forEach { busRouteModel: BusRouteModel ->
            val busNumber = "101"
            lruCacheImpl.saveBitmapDescriptor(
                context,
                R.drawable.navigation_drop_red,
                busNumber,
                busRouteModel.invalidAdapted,
                busRouteModel.id.toString(),
            )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    mainViewModel.visible101Buss()
                }) {
                Column(
                    modifier = Modifier.padding(all = 0.dp)
                ) {
                    Text(text = "101", textAlign = TextAlign.Center)
                    Checkbox(
                        modifier = Modifier.padding(all = 0.dp),
                        checked = mainState.visible101Buss,
                        onCheckedChange = null
                    )
                }
            }
        }
    ) {
        MainContent(
            lruCacheImpl = lruCacheImpl,
            routes14 = mainState.routes14,
            polyLines14 = mainState.polyLines14,
            routes101 = mainState.routes101,
            polyLines101 = mainState.polyLines101,
            visible101Buss = mainState.visible101Buss,
        )
        it
    }
}

@Composable
fun MainContent(
    lruCacheImpl: LruCacheImpl,
    routes14: List<BusRouteModel>,
    polyLines14: List<LatLng>,
    routes101: List<BusRouteModel>,
    polyLines101: List<LatLng>,
    visible101Buss: Boolean,
) {
    val cameraPositionLatLng = LatLng(routes14.lastOrNull()?.lat ?: 54.90936, routes14.lastOrNull()?.lon ?: 69.13374)
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

    val icon14 = remember {
        mutableStateOf<BitmapDescriptor?>(null)
    }
    val icon101 = remember {
        mutableStateOf<BitmapDescriptor?>(null)
    }
    Box {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                icon14.value = lruCacheImpl.getBitmapDescriptor("14")?.let {
                    BitmapDescriptorFactory.fromBitmap(it)
                }
                icon101.value = lruCacheImpl.getBitmapDescriptor("101")?.let {
                    BitmapDescriptorFactory.fromBitmap(it)
                }
            }
        ) {
            Polyline(
                points = polyLines14, color = Color.Blue, visible = true, width = 6f, startCap = RoundCap()
            )
            routes14.forEach { busRouteModel ->
                val latLong = LatLng(busRouteModel.lat, busRouteModel.lon)
                Marker(
                    state = MarkerState(position = latLong),
                    title = busRouteModel.name,
                    rotation = busRouteModel.direction.toFloat(),
                    icon = lruCacheImpl.getBitmapDescriptor(busRouteModel.id.toString())?.let { bitmap ->
                        BitmapDescriptorFactory.fromBitmap(bitmap)
                    },
                    visible = true
                )
            }

            if (visible101Buss) {
                Polyline(
                    points = polyLines101, color = Color.Green, visible = true, width = 6f, startCap = RoundCap()
                )
                routes101.forEach { busRouteModel ->
                    val latLong = LatLng(busRouteModel.lat, busRouteModel.lon)
                    Marker(
                        state = MarkerState(position = latLong),
                        title = busRouteModel.name,
                        rotation = busRouteModel.direction.toFloat(),
                        icon = lruCacheImpl.getBitmapDescriptor(busRouteModel.id.toString())?.let { bitmap ->
                            BitmapDescriptorFactory.fromBitmap(bitmap)
                        },
                        visible = true
                    )
                }
            }
        }

        AnimatedVisibility(visible = routes14.isEmpty()) {
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