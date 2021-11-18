package com.android.practice.rotationstestapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import com.android.practice.rotationstestapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {

    companion object {
        const val LOG_TAG: String = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var sensorManager: SensorManager
    private var magneticSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null

    private var accelerometerValue = FloatArray(3)
    private var rotationMetricsValue = FloatArray(9)
    private var geoMagneticValue = FloatArray(3)
    private var resultAzimuth: Double = 0.0
    private var resultPitch: Double = 0.0
    private var resultRoll: Double = 0.0
    private var accResultX: Double = 0.0
    private var accResultY: Double = 0.0
    private var accResultZ: Double = 0.0
    private var result = FloatArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initPhoneSensor()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this, magneticSensor)
        sensorManager.unregisterListener(this, accelerometerSensor)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            when (event.sensor.type) {
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    System.arraycopy(event.values, 0, geoMagneticValue, 0, geoMagneticValue.size)
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    System.arraycopy(event.values, 0, accelerometerValue, 0, accelerometerValue.size)
                    binding.accResultX.text = event.values[0].toString()
                    binding.accResultY.text = event.values[1].toString()
                    binding.accResultZ.text = event.values[2].toString()
                }
            }
            SensorManager.getRotationMatrix(rotationMetricsValue, null, accelerometerValue, geoMagneticValue)
            SensorManager.getOrientation(rotationMetricsValue, result)
            resultAzimuth = Math.toDegrees(result[0].toDouble())
            resultPitch = Math.toDegrees(result[1].toDouble())
            resultRoll = Math.toDegrees(result[2].toDouble())
            binding.resultAzimuth.text = resultAzimuth.toString()
            binding.resultPitch.text = resultPitch.toString()
            binding.resultRoll.text = resultRoll.toString()
            //Log.d(LOG_TAG, "$resultPitch, $resultPitch, $resultRoll")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun initPhoneSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (sensor in sensorList) {
            Log.d(LOG_TAG, "There is a ${sensor.name} sensor")
        }

        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
}