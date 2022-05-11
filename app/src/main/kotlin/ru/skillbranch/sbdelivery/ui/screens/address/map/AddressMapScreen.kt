package ru.skillbranch.sbdelivery.ui.screens.address.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.edit
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.models.network.domains.ReqCoordinate
import ru.skillbranch.sbdelivery.ui.components.ButtonV1
import ru.skillbranch.sbdelivery.ui.components.Field
import ru.skillbranch.sbdelivery.ui.components.FormsValidators

private const val TAG = "AddressMapScreen"
private val MOSCOW = LatLng(55.7550054166326, 37.61793415993452)


@Composable
public fun AddressMapScreen(vm: AddressMapViewModel) {
    val state = vm.state.collectAsState().value

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    AddressMapForm(state, vm::mutate)
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalComposeUiApi::class)

@Composable
public fun AddressMapForm(
    state: AddressMapFeature.State,
    mutate: (AddressMapFeature.Msg) -> Unit
) {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    //
    val rawAddress = state.resAddress.firstOrNull()?.value ?: ""
    val rawAddressError = rememberSaveable { mutableStateOf("") }
    val buttonSaveEnabled = rememberSaveable { mutableStateOf(false) }
    //
    val isPermissionsChecked = remember { mutableStateOf(false) }
    val isLocationGranted = remember { mutableStateOf(false) }
    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(MOSCOW, 11f)
    }
    var tapPosition by remember { mutableStateOf(cameraPositionState.position.target) }

    val mapProperties = remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isBuildingEnabled = true,
                isMyLocationEnabled = false
            )
        )
    }
    val mapUiSettings = remember {
        mutableStateOf(
            MapUiSettings(
                mapToolbarEnabled = true,
                compassEnabled = false,
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        )
    }

    fun buttonEnabledCheck() {
        buttonSaveEnabled.value = rawAddress.isNotBlank()
    }

    CheckPermissions(mapProperties, mapUiSettings, isPermissionsChecked, isLocationGranted)

    buttonEnabledCheck()

    if (isPermissionsChecked.value) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (map, address, saveBtn) = createRefs()

            // region Google Map
            Box(
                modifier = Modifier.constrainAs(map) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            ) {

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties.value,
                    uiSettings = mapUiSettings.value,
                    onMapLoaded = { isMapLoaded = true },
                    googleMapOptionsFactory = {
                        GoogleMapOptions().camera(
                            CameraPosition.fromLatLngZoom(cameraPositionState.position.target, 11f)
                        )
                    },
                    onMapClick = {
                        Log.d(TAG, "Map clicked: $it")
                        tapPosition = it
                        mutate(
                            AddressMapFeature.Msg.SetReqAddress(
                                ReqCoordinate(
                                    it.latitude,
                                    it.longitude
                                )
                            )
                        )
                    },
                    onPOIClick = {
                        Log.d(TAG, "POI clicked: ${it.name}")
                    }
                ) {
                    Marker(position = tapPosition)
                }

                if (!isMapLoaded) {
                    AnimatedVisibility(
                        modifier = Modifier.fillMaxSize(),
                        visible = !isMapLoaded,
                        enter = EnterTransition.None,
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .background(MaterialTheme.colors.background)
                                .wrapContentSize()
                        )
                    }
                }
            }
            // endregion

            Row(
                modifier = Modifier
                    .constrainAs(address) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(
                        top = 4.dp,
                        start = 8.dp,
                        end = (if (isLocationGranted.value) 62.dp else 8.dp)
                    )
            ) {
                // region raw Address
                Field(
                    value = rawAddress,
                    error = rawAddressError,
                    enabled = !state.isLoading(),
                    readOnly = true,
                    validator = FormsValidators.fieldAddress,
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.fillMaxWidth()
                ) {}
                // endregion
            }

            Row(
                modifier = Modifier
                    .constrainAs(saveBtn) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(8.dp)
            ) {
                ButtonV1(
                    text = stringResource(R.string.labelSave),
                    enabled = buttonSaveEnabled.value && !state.isLoading(),
                ) {
                    sharedPref.edit {
                        putString("address", rawAddress)
                        commit()
                    }
                    dispatcher.onBackPressed()
                }
            }
        }
    }
}

@Composable
private fun CheckPermissions(
    mapProperties: MutableState<MapProperties>,
    mapUiSettings: MutableState<MapUiSettings>,
    isPermissionsChecked: MutableState<Boolean>,
    isLocationGranted: MutableState<Boolean>,
) {
    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                // Precise location access granted.
                mapProperties.value = mapProperties.value.copy(
                    isMyLocationEnabled = true
                )
                mapUiSettings.value = mapUiSettings.value.copy(
                    myLocationButtonEnabled = true
                )
                isLocationGranted.value = true
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                // Only approximate location access granted.
                mapProperties.value = mapProperties.value.copy(
                    isMyLocationEnabled = true
                )
                mapUiSettings.value = mapUiSettings.value.copy(
                    myLocationButtonEnabled = true,
                )
                isLocationGranted.value = true
            }
            else -> {
                // No location access granted.
                mapProperties.value = mapProperties.value.copy(
                    isMyLocationEnabled = false
                )
                mapUiSettings.value = mapUiSettings.value.copy(
                    myLocationButtonEnabled = false
                )
                isLocationGranted.value = false
            }
        }
        isPermissionsChecked.value = true
    }

    SideEffect {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    // endregion
}

/*@Composable
private fun DebugView(cameraPositionState: CameraPositionState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        val moving = if (cameraPositionState.isMoving) "moving" else "not moving"
        Text(text = "Camera is $moving")
        Text(text = "Camera position is ${cameraPositionState.position}")
    }
}*/
