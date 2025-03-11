package com.example.demoecotiles.data

import kotlinx.serialization.Serializable

@Serializable
data class JsonDataValue(
    val lodge_id: String,
    val data_type: String,
    val data_range: String,
    val data: List<Double>
)

