package com.db.twilliovoicecall

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.db.twilliovoicecall.databinding.ActivityCallBinding
import java.util.concurrent.TimeUnit

class CallActivity : AppCompatActivity() {
    lateinit var binding: ActivityCallBinding
    private val handler = Handler(Looper.getMainLooper())
    private var callDurationInSeconds = 0
    var phoneNumber = ""
    private lateinit var audioManager: AudioManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startCallTimer()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Set initial speaker status
        updateSpeakerStatus(false)
        phoneNumber = intent.getStringExtra("mobile") ?: ""
        Log.d("strData", "Phone Number : " + phoneNumber)
        binding.txtMobile.text = phoneNumber

        binding.hangupButton.setOnClickListener {
            endCall()
        }

        binding.imgSpeakerOn.setOnClickListener {
            updateSpeakerStatus(true)
            binding.imgSpeakerOn.visibility = View.GONE
            binding.imgSpeakerOff.visibility = View.VISIBLE
        }

        binding.imgSpeakerOff.setOnClickListener {
            updateSpeakerStatus(false)
            binding.imgSpeakerOn.visibility = View.VISIBLE
            binding.imgSpeakerOff.visibility = View.GONE
        }
    }

    private val updateCallDurationRunnable = object : Runnable {
        override fun run() {
            callDurationInSeconds++
            val minutes = TimeUnit.SECONDS.toMinutes(callDurationInSeconds.toLong())
            val seconds = callDurationInSeconds % 60
            binding.txtCallDuration.text = String.format("%02d:%02d", minutes, seconds)
            handler.postDelayed(this, 1000)
        }
    }

    private fun startCallTimer() {
        handler.post(updateCallDurationRunnable)
    }

    private fun endCall() {
        handler.removeCallbacks(updateCallDurationRunnable)
        MainActivity.call?.disconnect()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        // Perform additional actions to end the call here
    }

    private fun updateSpeakerStatus(enableSpeaker: Boolean) {
        audioManager.isSpeakerphoneOn = enableSpeaker
    }
}