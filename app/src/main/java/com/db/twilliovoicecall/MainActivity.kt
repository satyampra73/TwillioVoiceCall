package com.db.twilliovoicecall

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.db.twilliovoicecall.databinding.ActivityMainBinding
import com.db.twilliovoicecall.utils.CustomProgressBar
import com.twilio.voice.Call
import com.twilio.voice.CallException
import com.twilio.voice.ConnectOptions
import com.twilio.voice.Voice

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val numberBuilder = StringBuilder()
    companion object{
        var call: Call? = null
    }
    var accessToken :String = ""
    val progressBar = CustomProgressBar()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listeners for each button
        binding.button0.setOnClickListener { appendNumber("0") }
        binding.button1.setOnClickListener { appendNumber("1") }
        binding.button2.setOnClickListener { appendNumber("2") }
        binding.button3.setOnClickListener { appendNumber("3") }
        binding.button4.setOnClickListener { appendNumber("4") }
        binding.button5.setOnClickListener { appendNumber("5") }
        binding.button6.setOnClickListener { appendNumber("6") }
        binding.button7.setOnClickListener { appendNumber("7") }
        binding.button8.setOnClickListener { appendNumber("8") }
        binding.button9.setOnClickListener { appendNumber("9") }
        binding.buttonStar.setOnClickListener { appendNumber("*") }
        binding.buttonHash.setOnClickListener { appendNumber("#") }

        // Set click listener for call button
        binding.buttonCall.setOnClickListener {
            if (binding.numberDisplay.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "Please Dial 10 Digit Mobile Number.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.numberDisplay.text.toString().length < 10) {
                Toast.makeText(
                    this@MainActivity,
                    "Please Dial 10 Digit Mobile Number.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                makeCall()
            }
        }
        binding.btnClear.setOnClickListener {
            binding.numberDisplay.text = ""
            numberBuilder.clear()
        }
        binding.btnDisconnect.setOnClickListener {
            call?.disconnect()
        }

        requestPermissions()
        fetchAccessToken()
    }

    private fun requestPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                1
            )
        }
    }

    // Append clicked number or symbol to the display
    private fun appendNumber(number: String) {
        numberBuilder.append(number)
        binding.numberDisplay.text = numberBuilder.toString()
    }

    private fun makeCall() {
        val enteredNumber = "91" + numberBuilder.toString()
        if (enteredNumber.isNotEmpty()) {
            fetchAccessToken { accessToken ->

                val connectOptions = ConnectOptions.Builder(accessToken)
                    .params(mapOf("To" to enteredNumber))
                    .build()


               call = Voice.connect(applicationContext, connectOptions, callListener)


                // Update UI
                binding.numberDisplay.text = "Calling $enteredNumber..."
                numberBuilder.clear()
            }
        } else {
            binding.numberDisplay.text = "Enter number"
        }
    }

    private fun fetchAccessToken(callback: (String) -> Unit) {
        callback(accessToken)
    }

    // Listener to handle call events
    private val callListener = object : Call.Listener {
        override fun onConnectFailure(call: Call, callException: CallException) {
            callException.printStackTrace()
            binding.numberDisplay.text = "Call Failed !!"
            Log.d("strVoice", callException.toString())
        }

        override fun onRinging(call: Call) {
            binding.numberDisplay.text = "Ringing..."
        }

        override fun onConnected(call: Call) {
            binding.numberDisplay.text = "Connected"
            binding.btnDisconnect.visibility = View.VISIBLE
            val intent = Intent(this@MainActivity, CallActivity::class.java)
            startActivity(intent)
            finish()
        }

        override fun onReconnecting(call: Call, callException: CallException) {
            TODO("Not yet implemented")
        }

        override fun onReconnected(call: Call) {
            TODO("Not yet implemented")
        }

        override fun onDisconnected(call: Call, error: CallException?) {
            binding.numberDisplay.text = "Call ended"
        }
    }
    fun fetchAccessToken() {
        progressBar.showProgress(this@MainActivity)
        // URL for your API endpoint
        val url = "https://twilio.developerbrothersproject.com/api/generate-token"

        // Create a Volley request queue
        val requestQueue = Volley.newRequestQueue(this)

        // Create a JSON request
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                progressBar.hideProgress()
                // Handle the JSON response
                val token = response.optString("token")
                if (token.isNotEmpty()) {
                    // Use the token as needed
                    accessToken = token
                } else {
                    Log.d("strResponse","Token not Found")
                }
            },
            { error ->
                progressBar.hideProgress()
                // Handle error
                Log.d("strResponse","Api error!")
            }
        )

        // Add the request to the queue
        requestQueue.add(jsonObjectRequest)
    }


}