package com.example.demoecotiles.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.collection.emptyLongSet
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demoecotiles.data.DataValue
import com.example.demoecotiles.data.JsonDataValue
import com.example.demoecotiles.data.TileType

import com.example.demoecotiles.ui.theme.DarkGreen
import com.example.demoecotiles.ui.theme.LightGreen
import com.example.demoecotiles.ui.theme.Olive
import com.example.demoecotiles.ui.theme.Orangish
import com.example.demoecotiles.ui.theme.Pink80
import com.example.demoecotiles.ui.theme.Redish
import com.example.demoecotiles.ui.theme.Yellowish
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainTile(
    tileToPresent: TileType,
    dataPointsStr: String = "",
    scoreStr: String = "0",
    modifier: Modifier = Modifier
        .height(625.dp)
        .fillMaxWidth(),
    viewModel: MainScreenViewModel
) {
    val scope = rememberCoroutineScope()

    var unit: String = ""
    var data_range: String = "daily"

    val (newScore, newUnit) = when (tileToPresent) {
        TileType.ENERGY -> 100 to "kWh"
        TileType.WATER -> 200 to "m³"
        TileType.HEAT -> 300 to "kWh"
        TileType.NONE -> 0 to ""
    }

    unit = newUnit

    val backgroundColor = getBackgroundColor(tileToPresent)

    // Manage the selected state for the buttons
    var selectedState by remember { mutableStateOf("daily") }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        ),
        border = BorderStroke(2.dp, Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Start from top to place buttons above the chart
        ) {

            // Row of buttons
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val states = listOf("daily", "weekly", "average")
                val selectedColor = Olive
                val defaultColor = DarkGreen

                states.forEach { state ->
                    Button(
                        onClick = {
                            selectedState = state
                            // Publish the selected state message
                            scope.launch {
                                viewModel.publishMessage(state, "roller_tile")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedState == state) selectedColor else defaultColor,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.width(50.dp) // Adjust width as needed
                    ) {
                        Text(
                            text = state.take(1).uppercase(),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rest of your code remains the same
            val coinHistoryRandomized =
                (0..20).map {
                    DataValue(
                        value = Random.nextFloat() * 1000.0,
                        dateTime = ZonedDateTime.now().minusDays((20 - it).toLong())
                    )
                }

            var dataPoints = coinHistoryRandomized.map {
                DataPoint(
                    x = it.dateTime.dayOfYear.toFloat(),
                    y = it.value.toFloat(),
                    xLabel = DateTimeFormatter
                        .ofPattern("hh:mm")
                        .format(it.dateTime)
                )
            }

            if (dataPointsStr.isNotEmpty()) {
                val json = Json { ignoreUnknownKeys = true }

                // Deserializacja pojedynczego obiektu
                val jsonData: JsonDataValue = json.decodeFromString(dataPointsStr)
                data_range = jsonData.data_range.lowercase()

                // Mapowanie na listę DataValue na podstawie data_range
                val dataValueList: List<DataValue> = when (data_range) {
                    "daily" -> generateDailyData(jsonData.data)
                    "weekly" -> generateWeeklyData(jsonData.data)
                    "average" -> generateAverageData(jsonData.data)
                    else -> {
                        println("Unknown data_range: ${jsonData.data_range}")
                        emptyList()
                    }
                }

                dataPoints = dataValueList.mapIndexed { index, dataValue ->
                    val xLabel = when (data_range) {
                        "daily" -> DateTimeFormatter.ofPattern("HH").format(dataValue.dateTime)
                        "weekly" -> dataValue.dateTime.dayOfWeek
                            .getDisplayName(TextStyle.FULL, Locale.getDefault()) // Pełna nazwa dnia
                            .take(2)

                        "average" -> when (index) {
                            0 -> "You"      // Jeśli "You" to faktycznie intencja
                            1 -> "Average"
                            else -> ""
                        }

                        else -> DateTimeFormatter.ofPattern("HH:mm").format(dataValue.dateTime)
                    }

                    DataPoint(
                        x = dataValue.dateTime.dayOfYear.toFloat(),
                        y = dataValue.value.toFloat(),
                        xLabel = xLabel
                    )
                }
            }

            if (dataPoints.isNotEmpty()) {

                AnimatedVisibility(
                    visible = tileToPresent != TileType.NONE
                ) {
                    var selectedDataPoint by remember {
                        mutableStateOf<DataPoint?>(null)
                    }

                    var labelWidth by remember {
                        mutableFloatStateOf(0f)
                    }
                    var totalChartWidth by remember {
                        mutableFloatStateOf(0f)
                    }
                    val amountOfVisibleDataPoints = if (labelWidth > 0) {
                        ((totalChartWidth - 2.5 * labelWidth) / labelWidth).toInt()
                    } else {
                        0
                    }
                    val startIndex = (dataPoints.lastIndex - amountOfVisibleDataPoints)
                        .coerceAtLeast(0)



                    LineChart(
                        dataPoints = dataPoints,
                        style = ChartStyle(
                            chartLineColor = DarkGreen,
                            unselectedColor = Color.White.copy(
                                alpha = 1f
                            ),
                            selectedColor = Color.White,
                            helpersLinesThicknessPx = 5f,
                            axisLinesThicknessPx = 5f,
                            labelFontSize = 10.sp,
                            minYLabelSpacing = 4.dp,
                            verticalPadding = 8.dp,
                            horizontalPadding = 8.dp,
                            xAxisLabelSpacing = 4.dp
                        ),
                        visibleDataPointsIndices = startIndex..dataPoints.lastIndex,
                        unit = unit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .aspectRatio(4 / 3f)
                            .onSizeChanged { totalChartWidth = it.width.toFloat() },
                        selectedDataPoint = selectedDataPoint,
                        onSelectedDataPoint = {
                            selectedDataPoint = it
                        },
                        onXLabelWidthChange = { labelWidth = it }
                    )

                }

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Score: $scoreStr",
                    color = Color.White,
                    fontSize = 24.sp
                )
            }else{
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "No data available",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun MainTilePreview() {
    val viewModel = MainScreenViewModel()
    MainTile(tileToPresent = TileType.ENERGY, viewModel = viewModel)
}

// Funkcja generująca dane dla zakresu "daily"
@RequiresApi(Build.VERSION_CODES.O)
fun generateDailyData(data: List<Double>): List<DataValue> {
//    if (data.size != 8) {
//        throw IllegalArgumentException("Expected 8 data points for 'daily' range.")
//    }

    val now = ZonedDateTime.now()
    val startTime = now.withHour(0).withMinute(0).withSecond(0).withNano(0)

    return data.mapIndexed { index, value ->
        val dateTime = startTime.plusHours(3 * index.toLong())
        DataValue(value, dateTime)
    }
}

// Funkcja generująca dane dla zakresu "weekly"
@RequiresApi(Build.VERSION_CODES.O)
fun generateWeeklyData(data: List<Double>): List<DataValue> {
    val days = data.size
    val specificHour = 18 // 18:00
    val now = ZonedDateTime.now()
    val todayAtSpecificHour = now.withHour(specificHour).withMinute(0).withSecond(0).withNano(0)

    return data.mapIndexed { index, value ->
        val dateTime = todayAtSpecificHour.minusDays((days - 1 - index).toLong())
        DataValue(value, dateTime)
    }
}

// Funkcja generująca dane dla zakresu "average"
@RequiresApi(Build.VERSION_CODES.O)
fun generateAverageData(data: List<Double>): List<DataValue> {
    if (data.size != 2) {
        throw IllegalArgumentException("Expected 2 data points for 'average' range.")
    }

    val now = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
    val yesterdayMidnight = now.minusDays(1)
    val todayMidnight = now

    return listOf(
        DataValue(data[0], yesterdayMidnight),
        DataValue(data[1], todayMidnight)
    )
}
@Composable
fun getBackgroundColor(tileType: TileType): Color {
    return when (tileType) {
        TileType.ENERGY -> Yellowish
        TileType.WATER -> LightGreen
        TileType.HEAT -> Orangish// Pomarańczowy
        TileType.NONE -> Olive
    }
}
