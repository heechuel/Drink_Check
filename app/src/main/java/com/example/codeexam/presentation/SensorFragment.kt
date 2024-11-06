package com.example.codeexam.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.codeexam.R
import kotlin.math.abs
import kotlin.math.sqrt

class SensorFragment : Fragment(), SensorEventListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorType = arguments?.getInt("sensorType") ?: 0
        mSensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager.getDefaultSensor(mSensorType)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.sensor, container, false)
        mTextTitle = mView.findViewById(R.id.text_title)
        mTextTitle.text = mSensor?.stringType
        mTextValues = mView.findViewById(R.id.text_values)
        return mView
    }

    override fun onResume() {
        super.onResume()
        mSensor?.let { mSensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
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
