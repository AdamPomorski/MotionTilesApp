package com.example.demoecotiles.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import com.example.demoecotiles.BuildConfig

class MainScreenViewModel: ViewModel() {
    private val mqttClient: MqttClient
    private val brokerUrl = BuildConfig.MQTT_BROKER_URL
    private val topicToSubscribe = "#"
//    private val topicToPublish = "lodge_1/energy_tile"

    private val _receivedDataPoints = MutableLiveData<String>()
    val receivedDataPoints: LiveData<String> get() = _receivedDataPoints
    private val _receivedScore = MutableLiveData<String>()
    val receivedScore: LiveData<String> get() = _receivedScore

    init {
        val clientId = MqttClient.generateClientId()
        mqttClient = MqttClient(brokerUrl, clientId, MemoryPersistence())

        val options = MqttConnectOptions().apply {
            isCleanSession = true
            userName = BuildConfig.MQTT_USERNAME
            password = BuildConfig.MQTT_PASSWORD.toCharArray()
        }

        viewModelScope.launch(Dispatchers.IO) {

            try {
                mqttClient.connect(options)
                subscribeToTopic()
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }


    }

    private fun subscribeToTopic() {
        try {
            mqttClient.subscribe(topicToSubscribe) { topic, message ->
                if (topic == "lodge_1/main_tile") {

                    _receivedDataPoints.postValue(message.toString())
                }
                if (topic == "lodge_1/score_tile") {
                    _receivedScore.postValue(message.toString())
                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publishMessage(message: String, topic:String) {
        viewModelScope.launch(Dispatchers.IO) {

            val topicToPublish = "lodge_1/$topic"
            try {
                mqttClient.publish(topicToPublish, MqttMessage(message.toByteArray()))
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            mqttClient.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }


}