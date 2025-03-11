package com.example.demoecotiles.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

data class ChartStyle(
    val chartLineColor: Color,
    val unselectedColor: Color,
    val selectedColor: Color,
    val helpersLinesThicknessPx: Float,
    val axisLinesThicknessPx: Float,
    val labelFontSize: TextUnit,
    val minYLabelSpacing: Dp,
    val verticalPadding: Dp,
    val horizontalPadding: Dp,
    val xAxisLabelSpacing: Dp

)
