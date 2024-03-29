package com.yapp.buddycon.gifticon.detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.LabelManager
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.yapp.buddycon.designsystem.R
import com.yapp.buddycon.designsystem.component.appbar.TopAppBarWithBackAndEdit
import com.yapp.buddycon.designsystem.component.button.BuddyConButton
import com.yapp.buddycon.designsystem.component.custom.FullGifticonImage
import com.yapp.buddycon.designsystem.component.dialog.DefaultDialog
import com.yapp.buddycon.designsystem.component.snackbar.BuddyConSnackbar
import com.yapp.buddycon.designsystem.component.snackbar.showBuddyConSnackBar
import com.yapp.buddycon.designsystem.component.tag.DDayTag
import com.yapp.buddycon.designsystem.component.utils.DividerHorizontal
import com.yapp.buddycon.designsystem.theme.Black
import com.yapp.buddycon.designsystem.theme.BuddyConTheme
import com.yapp.buddycon.designsystem.theme.Grey70
import com.yapp.buddycon.designsystem.theme.Paddings
import com.yapp.buddycon.designsystem.theme.Pink50
import com.yapp.buddycon.domain.model.kakao.SearchPlaceModel
import timber.log.Timber
import java.text.SimpleDateFormat

@Composable
fun GifticonDetailScreen(
    gifticonId: Int?,
    fromRegister: Boolean?,
    onBack: () -> Unit
) {
    checkNotNull(gifticonId)
    checkNotNull(fromRegister)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showedSnackbar by remember { mutableStateOf(false) }

    if (fromRegister && showedSnackbar.not()) {
        showBuddyConSnackBar(
            message = context.getString(R.string.gifticon_register_complete_snackbar),
            scope = coroutineScope,
            snackbarHostState = snackbarHostState
        )
        showedSnackbar = true
    }

    Scaffold(
        topBar = {
            TopAppBarWithBackAndEdit(
                title = stringResource(R.string.gifticon),
                onBack = onBack
            )
        },
        floatingActionButton = {
            BuddyConButton(
                modifier = Modifier
                    .padding(horizontal = Paddings.xlarge)
                    .fillMaxWidth(),
                text = stringResource(R.string.gifticon_used_complete)
            ) {
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = { BuddyConSnackbar(snackbarHostState = snackbarHostState) }
    ) { paddingValues ->
        GifticonDetailContent(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            gifticonId = gifticonId
        )
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
private fun GifticonDetailContent(
    modifier: Modifier = Modifier,
    gifticonDetailViewModel: GifticonDetailViewModel = hiltViewModel(),
    gifticonId: Int
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isImageExpanded by remember { mutableStateOf(false) }
    val gifticonDetailModel by gifticonDetailViewModel.gifticonDetailModel.collectAsStateWithLifecycle()
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    val searchPlaceModels by gifticonDetailViewModel.searchPlacesModel.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        gifticonDetailViewModel.requestGifticonDetail(gifticonId)
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            gifticonDetailViewModel.searchPlacesByKeyword(
                query = gifticonDetailModel.gifticonStore.value,
                x = location.longitude.toString(),
                y = location.latitude.toString()
            )
        }
    }

    FullGifticonImage(
        imageUri = gifticonDetailModel.imageUrl,
        isExpanded = isImageExpanded,
        onExpandChanged = { isImageExpanded = it }
    )

    Column(modifier.verticalScroll(scrollState)) {
        Box(
            modifier = Modifier
                .padding(top = Paddings.medium)
                .padding(horizontal = Paddings.xlarge)
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
        ) {
            AsyncImage(
                model = gifticonDetailModel.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (gifticonDetailModel.expireDate.isNotEmpty()) {
                DDayTag(
                    modifier = Modifier.padding(top = 12.dp, start = 12.dp),
                    dateMillis = SimpleDateFormat("yyyy-MM-dd").parse(gifticonDetailModel.expireDate).time
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 12.dp, end = 12.dp)
                    .size(40.dp)
                    .background(Black.copy(0.4f), CircleShape)
                    .clickable { isImageExpanded = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }
        }
        Text(
            text = gifticonDetailModel.name,
            modifier = Modifier.padding(top = 30.dp, start = 16.dp),
            style = BuddyConTheme.typography.title02
        )
        GifticonDetailInfoRow(
            modifier = Modifier
                .padding(top = 22.dp)
                .fillMaxWidth(),
            info = stringResource(R.string.gifticon_expiration_date),
            value = gifticonDetailModel.expireDate
        )
        GifticonDetailInfoRow(
            modifier = Modifier.fillMaxWidth(),
            info = stringResource(R.string.gifticon_usage),
            value = gifticonDetailModel.gifticonStore.value
        )
        GifticonDetailInfoRow(
            modifier = Modifier.fillMaxWidth(),
            info = stringResource(R.string.gifticon_memo),
            value = gifticonDetailModel.memo
        )
        GifticonMap(
            location = currentLocation,
            searchPlaceModels = searchPlaceModels
        ) {
            getCurrentLocation(context) {
                currentLocation = it
            }
        }
    }
}

@Composable
private fun GifticonDetailInfoRow(
    modifier: Modifier = Modifier,
    info: String = "",
    value: String = ""
) {
    Column(modifier) {
        Row(modifier = Modifier.padding(Paddings.xlarge)) {
            Text(
                text = info,
                modifier = Modifier.width(98.dp),
                style = BuddyConTheme.typography.body03.copy(color = Grey70)
            )
            Text(
                text = value,
                style = BuddyConTheme.typography.subTitle
            )
        }
        DividerHorizontal(
            modifier = Modifier
                .padding(horizontal = Paddings.xlarge)
                .padding(bottom = Paddings.small)
        )
    }
}

@Composable
private fun GifticonMap(
    location: Location?,
    searchPlaceModels: List<SearchPlaceModel> = listOf(),
    onGranted: () -> Unit = {}
) {
    val context = LocalContext.current
    // 위치 권한 체크
    var isGrantedPermission by remember {
        mutableStateOf(checkLocationPermission(context))
    }

    // 위치 권한 유도 팝업
    var showPermissonDialog by remember { mutableStateOf(false) }

    // 위치 권한 요청
    var requestPermissionEvent by remember { mutableStateOf(false) }

    if (isGrantedPermission) {
        onGranted()
    }

    if (showPermissonDialog) {
        DefaultDialog(
            dialogTitle = stringResource(R.string.location_permission_dialog_title),
            dismissText = stringResource(R.string.location_permission_later),
            confirmText = stringResource(R.string.location_permission_granted),
            dialogContent = stringResource(R.string.location_permission_dialog_content),
            onConfirm = {
                showPermissonDialog = false
                requestPermissionEvent = true
            },
            onDismissRequest = { showPermissonDialog = false }
        )
    }

    if (requestPermissionEvent) {
        RequestLocationPermission(
            onGranted = {
                isGrantedPermission = true
            },
            onDeny = {
                isGrantedPermission = false
                requestPermissionEvent = false
            }
        )
    }

    Box(
        modifier = Modifier
            .padding(top = Paddings.xlarge, bottom = 89.dp)
            .padding(horizontal = Paddings.xlarge)
            .fillMaxWidth()
            .height(166.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context)
            },
            update = { it ->
                it.start(
                    object : MapLifeCycleCallback() {
                        override fun onMapDestroy() {
                        }

                        override fun onMapError(error: Exception?) {
                            Timber.e("onMapError ${error?.message}")
                        }
                    },
                    object : KakaoMapReadyCallback() {
                        override fun onMapReady(kakaoMap: KakaoMap) {
                            location?.let { location ->
                                kakaoMap.labelManager?.let { manager ->
                                    getLocationLabel(
                                        labelManager = manager,
                                        latitude = location.latitude,
                                        longitude = location.longitude
                                    )

                                    searchPlaceModels.forEach { seachPlaceModel ->
                                        getLocationLabel(
                                            labelManager = manager,
                                            latitude = seachPlaceModel.y.toDouble(),
                                            longitude = seachPlaceModel.x.toDouble()
                                        )
                                    }
                                }
                            }
                        }

                        override fun getPosition(): LatLng {
                            location?.let { location ->
                                return LatLng.from(location.latitude, location.longitude)
                            } ?: kotlin.run {
                                return super.getPosition()
                            }
                        }
                    }
                )
            }
        )
        if (isGrantedPermission.not()) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Black.copy(0.4f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(BuddyConTheme.colors.background, RoundedCornerShape((22.5).dp))
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .clickable { showPermissonDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(Color.Red)) {
                            append("위치 허용")
                        }
                        append("을 해야 사용할 수 있어요")
                    },
                    style = BuddyConTheme.typography.body04.copy(color = Grey70)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 12.dp, end = 12.dp)
                    .background(Pink50, RoundedCornerShape((18.5).dp))
                    .padding(horizontal = 22.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.gifticon_map_enlargement),
                    style = BuddyConTheme.typography.body04.copy(
                        color = BuddyConTheme.colors.primary
                    )
                )
            }
        }
    }
}

@Composable
private fun RequestLocationPermission(
    onGranted: () -> Unit = {},
    onDeny: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.all { it }
        if (isGranted) {
            onGranted()
        } else {
            onDeny()
        }
    }

    LaunchedEffect(Unit) {
        if (permissions.any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED }) {
            permissionsLauncher.launch(permissions)
        }
    }
}

private fun checkLocationPermission(
    context: Context
): Boolean {
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    return permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
}

// 내 위치 정보
@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: Context,
    onSuccess: (Location) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation.addOnSuccessListener {
        onSuccess(it)
    }
}

// 지도 위 마커 추가
private fun getLocationLabel(
    labelManager: LabelManager,
    latitude: Double,
    longitude: Double
) {
    labelManager.layer
        ?.addLabel(
            LabelOptions.from(LatLng.from(latitude, longitude))
                .setStyles(
                    labelManager.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.ic_location)))
                )
        )
}
