package com.example.codeexam.presentation

import android.annotation.SuppressLint
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.codeexam.R
import kotlin.math.abs
import kotlin.math.sqrt

class SensorAdapter : RecyclerView.Adapter<SensorAdapter.SensorViewHolder>(), SensorEventListener {

    // 센서 목록 예시
    private val sensorTypes = listOf(
        Sensor.TYPE_ACCELEROMETER,
        Sensor.TYPE_GYROSCOPE,
        Sensor.TYPE_MAGNETIC_FIELD,
        Sensor.TYPE_LIGHT
    )

    companion object {
        private const val SHAKE_THRESHOLD = 1.1f
        private const val SHAKE_WAIT_TIME_MS = 250
        private const val ROTATION_THRESHOLD = 2.0f
        private const val ROTATION_WAIT_TIME_MS = 100

        fun newInstance(sensorType: Int): SensorFragment {
            return SensorFragment().apply {
                arguments = Bundle().apply { putInt("sensorType", sensorType) }
            }
        }
    }
    private lateinit var mView: View
    private lateinit var mTextTitle: TextView
    private lateinit var mTextValues: TextView
    private lateinit var mSensorManager: SensorManager
    private var mSensor: Sensor? = null
    private var mSensorType: Int = 0
    private var mShakeTime: Long = 0
    private var mRotationTime: Long = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sensor_item, parent, false)
        return SensorViewHolder(view)
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        val sensorType = sensorTypes[position]
        holder.bind(sensorType)
    }

    override fun getItemCount(): Int = sensorTypes.size

    inner class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sensorTextView: TextView = itemView.findViewById(R.id.sensor_name)

        fun bind(sensorType: Int) {
            sensorTextView.text = when (sensorType) {
                Sensor.TYPE_ACCELEROMETER -> "Accelerometer"
                Sensor.TYPE_GYROSCOPE -> "Gyroscope"
                Sensor.TYPE_MAGNETIC_FIELD -> "MAGNETIC"
                else -> "Unknown Sensor"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return

        mTextValues.text = """
            x = ${event.values[0]}
            y = ${event.values[1]}
            z = ${event.values[2]}
        """.trimIndent()

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> detectShake(event)
            Sensor.TYPE_GYROSCOPE -> detectRotation(event)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    private fun detectShake(event: SensorEvent) {
        val now = System.currentTimeMillis()
        if (now - mShakeTime > SHAKE_WAIT_TIME_MS) {
            mShakeTime = now

            val gForce = sqrt(
                (event.values[0] / SensorManager.GRAVITY_EARTH).let { it * it } +
                        (event.values[1] / SensorManager.GRAVITY_EARTH).let { it * it } +
                        (event.values[2] / SensorManager.GRAVITY_EARTH).let { it * it }
            )

            mView.setBackgroundColor(if (gForce > SHAKE_THRESHOLD) Color.rgb(0, 100, 0) else Color.BLACK)
        }
    }

    private fun detectRotation(event: SensorEvent) {
        val now = System.currentTimeMillis()
        if (now - mRotationTime > ROTATION_WAIT_TIME_MS) {
            mRotationTime = now

            if (event.values.any { abs(it) > ROTATION_THRESHOLD }) {
                mView.setBackgroundColor(Color.rgb(0, 100, 0))
            } else {
                mView.setBackgroundColor(Color.BLACK)
            }
        }
    }

}
