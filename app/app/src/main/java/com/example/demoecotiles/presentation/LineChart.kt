package com.example.demoecotiles.presentation




import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demoecotiles.data.DataValue
import com.example.demoecotiles.ui.theme.DarkGreen
import com.example.demoecotiles.ui.theme.DemoEcoTilesTheme
import com.example.demoecotiles.ui.theme.Olive
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun LineChart(
    dataPoints : List<DataPoint>,
    style:ChartStyle,
    visibleDataPointsIndices: IntRange,
    unit: String,
    modifier: Modifier = Modifier,
    selectedDataPoint: DataPoint? = null,
    onSelectedDataPoint: (DataPoint) -> Unit = {},
    onXLabelWidthChange: (Float) -> Unit = {},
    showHelperLines: Boolean = true
) {
    val textStyle = LocalTextStyle.current.copy(
        fontSize = style.labelFontSize
    )

    val visibleDataPoints = remember(dataPoints, visibleDataPointsIndices ) {
        dataPoints.slice(visibleDataPointsIndices)
    }

    val maxYValue = remember(visibleDataPoints) {
        visibleDataPoints.maxOfOrNull {it.y } ?: 0f
    }

    val minYValue = remember(visibleDataPoints) {
        visibleDataPoints.minOfOrNull {it.y } ?: 0f
    }

    val measurer = rememberTextMeasurer()

    var xLabelWidth by remember {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(key1 = xLabelWidth) {
        onXLabelWidthChange(xLabelWidth)
    }

    val selectedDataPointIndex = remember(selectedDataPoint) {
        dataPoints.indexOf(selectedDataPoint)
    }

    var drawPoints by remember {
        mutableStateOf(listOf<DataPoint>())
    }

    var isShowingDataPoints by remember {
        mutableStateOf(selectedDataPoint!=null)
    }


    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(drawPoints,xLabelWidth) {
                detectHorizontalDragGestures { change, _ ->
                    val newSelectedDataPointIndex = getSelectedDataPointIndex(
                        touchOffsetX = change.position.x,
                        triggerWidth = xLabelWidth,
                        drawPoints = drawPoints
                    )
                    isShowingDataPoints = (newSelectedDataPointIndex + visibleDataPointsIndices.first) in visibleDataPointsIndices
                    if(isShowingDataPoints){
                        onSelectedDataPoint(dataPoints[newSelectedDataPointIndex])
                    }


                }
            }
    ) {
        val minLabelSpacingYPx = style.minYLabelSpacing.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()
        val horizontalPaddingPx = style.horizontalPadding.toPx()
        val xAxisLabelSpacingPx = style.xAxisLabelSpacing.toPx()


        val xLabelTextLayoutResult = visibleDataPoints.map {
            measurer.measure(
                text = it.xLabel,
                style = textStyle.copy(textAlign = TextAlign.Center)
            )
        }

        val maxXLabelWidth = xLabelTextLayoutResult.maxOfOrNull { it.size.width } ?: 0
        val maxXLabelHeight = xLabelTextLayoutResult.maxOfOrNull { it.size.height } ?: 0
        val maxXLabelLineCount = xLabelTextLayoutResult.maxOfOrNull { it.lineCount } ?: 0
        val xLabelLineHeight = if(maxXLabelLineCount>0){
            maxXLabelHeight / maxXLabelLineCount
        }else 0


        val viewPortHeightPx = size.height - (maxXLabelHeight +  2 * verticalPaddingPx + xLabelLineHeight + xAxisLabelSpacingPx)


        // Y calculations
        val labelViewPortHeightPx = viewPortHeightPx + xLabelLineHeight
        val labelCountExcludingLastLabel = ((labelViewPortHeightPx/ (xLabelLineHeight + minLabelSpacingYPx))).toInt()

        val valueIncrement = (maxYValue - minYValue) / labelCountExcludingLastLabel
        val yLabels = (0..labelCountExcludingLastLabel).map {
            ValueLabel(
                value = maxYValue - (valueIncrement*it),
                unit = ""
            )
        }

        val yLabelTextLayoutResults = yLabels.map {
            measurer.measure(
                text = it.formatted(),
                style = textStyle
            )

        }

        val  maxYLabelWidth = yLabelTextLayoutResults.maxOfOrNull { it.size.width } ?: 0





        val viewPortTopY = verticalPaddingPx + xLabelLineHeight + 10f
        val viewPortBottomY = viewPortTopY + viewPortHeightPx
        val viewPortRightX = size.width
        val viewPortLeftX = 2f * horizontalPaddingPx + maxYLabelWidth
//        val viewPort = Rect(
//            left = viewPortLeftX,
//            top = viewPortTopY,
//            right = viewPortRightX,
//            bottom = viewPortBottomY
//        )

//        drawRect(
//            color = Color.Green,
//            topLeft = viewPort.topLeft,
//            size = viewPort.size
//        )

        if(dataPoints.size==6){
            xLabelWidth = maxXLabelWidth + xAxisLabelSpacingPx + 10f
        }else if (dataPoints.size==5){
            xLabelWidth = maxXLabelWidth + xAxisLabelSpacingPx + 40f
        }
        else{
            xLabelWidth = maxXLabelWidth + xAxisLabelSpacingPx
        }


        xLabelTextLayoutResult.forEachIndexed { index, layoutResult ->
            val x = viewPortLeftX + xAxisLabelSpacingPx / 2f +
                    xLabelWidth * index
            drawText(
                textLayoutResult = layoutResult,
                topLeft = Offset(
                    x = x,
                    y = viewPortBottomY +xAxisLabelSpacingPx
                ),
                color = if(index==selectedDataPointIndex){
                    style.selectedColor
                }else style.unselectedColor

            )

            if(showHelperLines){
                drawLine(
                    color = if(index==selectedDataPointIndex){
                        style.selectedColor
                    }else style.unselectedColor,
                    start = Offset(
                        x = x + layoutResult.size.width / 2f,
                        y = viewPortBottomY
                    ),
                    end = Offset(
                        x = x + layoutResult.size.width / 2f,
                        y = viewPortTopY
                    ),
                    strokeWidth = if(selectedDataPointIndex == index) {
                        style.helpersLinesThicknessPx * 1.8f
                    } else style.helpersLinesThicknessPx
                )
            }

            if(selectedDataPointIndex == index){
                val valueLabel = ValueLabel(
                    value = visibleDataPoints[index].y,
                    unit = unit
                )
                val valueResult = measurer.measure(
                    text = valueLabel.formatted(),
                    style = textStyle.copy(
                        color = style.selectedColor
                    ),
                    maxLines = 1
                )
                val textPositionX = if(selectedDataPointIndex == visibleDataPointsIndices.last){
                    x - valueResult.size.width
                }else{
                    x - valueResult.size.width / 2f
                } + layoutResult.size.width / 2f

                val isTextInVisibleRange = (size.width - textPositionX).roundToInt() in 0..size.width.roundToInt()

                if(isTextInVisibleRange){
                    drawText(
                        textLayoutResult = valueResult,
                        topLeft = Offset(
                            x = textPositionX,
                            y = viewPortTopY - valueResult.size.height - 10f
                        )
                    )
                }

            }
        }

        val heightRequiredForLabels = xLabelLineHeight * (labelCountExcludingLastLabel + 1)
        val remainingHeightForLabels = labelViewPortHeightPx - heightRequiredForLabels
        val spaceBetweenLabels = remainingHeightForLabels / labelCountExcludingLastLabel




        // Tutaj kończy się forEachIndexed na etykietach Y:
        yLabelTextLayoutResults.forEachIndexed { index, result ->
            val x = horizontalPaddingPx + maxYLabelWidth - result.size.width.toFloat()
            val y = viewPortTopY + index *(xLabelLineHeight + spaceBetweenLabels) - xLabelLineHeight/2f

            drawText(
                textLayoutResult = result,
                topLeft = Offset(x = x, y = y),
                color = style.unselectedColor
            )

            if (showHelperLines) {
                drawLine(
                    color = style.unselectedColor,
                    start = Offset(
                        x = viewPortLeftX,
                        y = y + result.size.height.toFloat() / 2f
                    ),
                    end = Offset(
                        x = viewPortRightX,
                        y = y + result.size.height.toFloat() / 2f
                    ),
                    strokeWidth = style.helpersLinesThicknessPx
                )
            }
        }

// -- DODAJEMY RYSOWANIE JEDNOSTKI --
        val unitLayoutResult = measurer.measure(
            text = "["+unit+"]", // np. "kWh" lub co innego
            style = textStyle
        )

// Przykład: rysowanie jednostki w okolicy lewego dolnego rogu osi Y
// Możesz dostosować pozycję X/Y w zależności od tego, gdzie chcesz wypisać jednostkę
        drawText(
            textLayoutResult = unitLayoutResult,
            topLeft = Offset(
                x = horizontalPaddingPx,
                // Np. przy dole całego wykresu, w okolicach viewPortBottomY + small offset
                y = 4f
            ),
            color = style.unselectedColor
        )

        drawPoints = visibleDataPointsIndices.map {
            val x = viewPortLeftX + (it - visibleDataPointsIndices.first) * xLabelWidth  + xLabelWidth / 2f
            val ratio = (dataPoints[it].y - minYValue) / (maxYValue - minYValue)
            val y = viewPortBottomY - (ratio * viewPortHeightPx)
            DataPoint(
                x = x,
                y = y,
                xLabel = dataPoints[it].xLabel
            )
        }

        val conPoints1 = mutableListOf<DataPoint>()
        val conPoints2 = mutableListOf<DataPoint>()
        for(i in 1 until drawPoints.size) {
            val p0 = drawPoints[i - 1]
            val p1 = drawPoints[i]

            val x = (p1.x + p0.x) / 2f
            val y1 = p0.y
            val y2 = p1.y

            conPoints1.add(DataPoint(x, y1, ""))
            conPoints2.add(DataPoint(x, y2, ""))
        }

        val linePath = Path().apply {
            if(drawPoints.isNotEmpty()) {
                moveTo(drawPoints.first().x, drawPoints.first().y)

                for(i in 1 until drawPoints.size) {
                    cubicTo(
                        x1 = conPoints1[i - 1].x,
                        y1 = conPoints1[i - 1].y,
                        x2 = conPoints2[i - 1].x,
                        y2 = conPoints2[i - 1].y,
                        x3 = drawPoints[i].x,
                        y3 = drawPoints[i].y
                    )
                }
            }
        }
        drawPath(
            path = linePath,
            color = style.chartLineColor,
            style = Stroke(
                width = 5f,
                cap = StrokeCap.Round
            )
        )




        drawPoints.forEachIndexed { index, point ->
            if(isShowingDataPoints){

                val circleOffset = Offset(
                    // dont know why there is an offset for 10f in x
                    x = point.x -10f,
                    y = point.y
                )

                drawCircle(
                    color = DarkGreen,
                    radius = 10f,

                    center = circleOffset
                )
                if(selectedDataPointIndex == index) {
                    drawCircle(
                        color = DarkGreen,
                        radius = 15f,
                        center = circleOffset
                    )
                    drawCircle(
                        color = DarkGreen,
                        radius = 15f,
                        center = circleOffset,
                        style = Stroke(
                            width = 3f
                        )
                    )
                }
            }
        }

    }

}

private fun getSelectedDataPointIndex(
    touchOffsetX: Float,
    triggerWidth: Float,
    drawPoints: List<DataPoint>
): Int {
    val triggerRangeLeft = touchOffsetX - triggerWidth / 2f
    val triggerRangeRight = touchOffsetX + triggerWidth / 2f
    return drawPoints.indexOfFirst {
        it.x in triggerRangeLeft..triggerRangeRight
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun LineChartPreview() {
    DemoEcoTilesTheme {
        val coinHistoryRandomized = remember {
            (0..20).map{
                DataValue(
                    value = Random.nextFloat()*1000.0,
                    dateTime = ZonedDateTime.now().plusDays(it.toLong())
                )
            }
        }
        val chartStyle = ChartStyle(
            chartLineColor = Color.Black,
            unselectedColor = Color(0xff7C7C7C),
            selectedColor = Color.Black,
            helpersLinesThicknessPx = 1f,
            axisLinesThicknessPx = 5f,
            labelFontSize = 14.sp,
            minYLabelSpacing = 25.dp,
            verticalPadding = 8.dp,
            horizontalPadding = 8.dp,
            xAxisLabelSpacing = 8.dp
        )


        val dataPoints = remember {
            coinHistoryRandomized.map {
                DataPoint(
                    x = it.dateTime.dayOfYear.toFloat(),
                    y = it.value.toFloat(),
                    xLabel = DateTimeFormatter
                        .ofPattern("d/M")
                        .format(it.dateTime)
                )
            }

        }

        LineChart(
            dataPoints = dataPoints,
            style = chartStyle,
            visibleDataPointsIndices = 0..dataPoints.size-1,
            unit = "kWh",
            selectedDataPoint = dataPoints[1],
            modifier = Modifier
                .width(800.dp)
                .height(300.dp)
                .background(Color.White),
            showHelperLines = true
        )
    }

}

