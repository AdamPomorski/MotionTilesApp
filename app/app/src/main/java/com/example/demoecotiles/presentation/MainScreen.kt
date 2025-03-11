package com.example.demoecotiles.presentation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.demoecotiles.R
import com.example.demoecotiles.data.TileType
import com.example.demoecotiles.ui.theme.LightGreen
import com.example.demoecotiles.ui.theme.NavyBlue
import com.example.demoecotiles.ui.theme.Olive
import com.example.demoecotiles.ui.theme.Orangish
import com.example.demoecotiles.ui.theme.Redish
import com.example.demoecotiles.ui.theme.Yellowish
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel){

    var clickedTile by remember { mutableStateOf(TileType.NONE)
    }
    var messageToSend by remember { mutableStateOf("")}
    val receivedDataPoints by viewModel.receivedDataPoints.observeAsState()
    val receivedScore by viewModel.receivedScore.observeAsState()
    val scope = rememberCoroutineScope()
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBlue)
    ) {
        Row(
            modifier = modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .width(screenWidthDp / 2),
                horizontalAlignment = Alignment.End,
            ) {
                ButtonTile(
                    backgroundColor = LightGreen,
                    widthDp = screenWidthDp / 2,
                    heightDp = 85.dp,
                    cardShape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    ),
                    onClick = { clickedTile = TileType.NONE },
                    isClickable = false)
                ButtonTile(
                    backgroundColor = Yellowish,
                    iconId = R.drawable.lighting,
                    widthDp = 170.dp,
                    heightDp = 170.dp,
                    onClick = {
                        clickedTile = TileType.ENERGY
                        messageToSend = "1"
                        if (messageToSend.isNotEmpty()) {
                            scope.launch {
                                viewModel.publishMessage(messageToSend, "energy_tile")
                            }
                        }
                    })
                ButtonTile(
                    backgroundColor = Redish,
                    iconId = R.drawable.battery_status_full,
                    widthDp = screenWidthDp/2,
                    heightDp = 170.dp,
                    onClick = { clickedTile = TileType.NONE

                    })
                ButtonTile(
                    backgroundColor = LightGreen,
                    iconId = R.drawable.drop,
                    widthDp = 170.dp,
                    heightDp = 170.dp,
                    onClick = {
                        clickedTile = TileType.WATER
                        messageToSend = "1"
                        if (messageToSend.isNotEmpty()) {
                            scope.launch {
                                viewModel.publishMessage(messageToSend, "water_tile")
                            }
                        }
                    })
                ButtonTile(
                    backgroundColor = Orangish,
                    iconId = R.drawable.air,
                    widthDp = 170.dp,
                    heightDp = 170.dp,
                    onClick = {
                        clickedTile = TileType.HEAT
                        messageToSend = "1"
                        if (messageToSend.isNotEmpty()) {
                            scope.launch {

                                viewModel.publishMessage(messageToSend, "heat_tile")
                            }
                        }},
                modifier = Modifier.align(Alignment.Start).width(170.dp).height(170.dp))
                ButtonTile(
                    backgroundColor = Olive,
                    widthDp = screenWidthDp / 2,
                    heightDp = 85.dp,
                    cardShape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    ),
                    onClick = { clickedTile = TileType.NONE },
                    isClickable = false)
            }

            Column {
                LogoTile(widthDp = screenWidthDp / 2)
                MainTile(tileToPresent = clickedTile, dataPointsStr = receivedDataPoints ?: "", scoreStr = receivedScore ?:"0", viewModel = viewModel)
                ButtonTile(
                    backgroundColor = Redish,
                    widthDp = screenWidthDp / 2,
                    heightDp = 125.dp,
                    cardShape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    ),
                    onClick = { clickedTile = TileType.NONE },
                    isClickable = false)

            }


        }
    }

}

@SuppressLint("NewApi")
@Preview
@Composable
private fun MainScreenPreview() {
    val viewModel = MainScreenViewModel()
    MainScreen(viewModel = viewModel)

}

