package com.example.demoecotiles.presentation

import java.text.NumberFormat
import java.util.Locale

data class ValueLabel(
    val value: Float,
    val unit: String
){
    fun formatted():String{
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            val fractionDigits = when{
                value > 1000 -> 0
                value in 2f..999f ->2
                else -> 1
            }
            maximumFractionDigits = 1
            minimumFractionDigits = 1
        }
        return "${formatter.format(value)}${unit}"
    }
}
