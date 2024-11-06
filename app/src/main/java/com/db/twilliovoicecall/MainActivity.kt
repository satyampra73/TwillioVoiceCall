package com.db.twilliovoicecall

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.db.twilliovoicecall.databinding.ActivityMainBinding
import com.twilio.voice.Call
import com.twilio.voice.CallException
import com.twilio.voice.ConnectOptions
import com.twilio.voice.Voice

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val numberBuilder = StringBuilder()
    private var call: Call? = null
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
            makeCall()
        }
        binding.btnClear.setOnClickListener{
            binding.numberDisplay.text=""
            numberBuilder.clear()
        }
        binding.btnDisconnect.setOnClickListener{

        }
    }

    // Append clicked number or symbol to the display
    private fun appendNumber(number: String) {
        numberBuilder.append(number)
        binding.numberDisplay.text = numberBuilder.toString()
    }

    // Placeholder for call function
    private fun makeCall() {
        val enteredNumber = "+91" + numberBuilder.toString()
        if (enteredNumber.isNotEmpty()) {
            // Fetch the access token from your server
            fetchAccessToken { accessToken ->
                // Using the second connect method which directly takes the accessToken string
                val connectOptions = ConnectOptions.Builder(accessToken)
                    .params(mapOf(
                        "to" to enteredNumber,
                        "from" to "+16316145120"  // Your Twilio-verified number
                    ))
                    .build()

                Voice.connect(applicationContext, connectOptions, callListener)

                // Update UI
                binding.numberDisplay.text = "Calling $enteredNumber..."
                numberBuilder.clear()
            }
        } else {
            binding.numberDisplay.text = "Enter number"
        }
    }

    // Function to fetch access token from the server
    private fun fetchAccessToken(callback: (String) -> Unit) {
        // Implement network call to get the token from your server
        // For demonstration, we'll assume you get the token successfully and invoke callback with it
        val accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTS2I2NjNhZjljOGZiNmVkYTM2ZDJiNDg5YWQ0MGIzM2JkLTE3MzA4OTUwODciLCJpc3MiOiJTS2I2NjNhZjljOGZiNmVkYTM2ZDJiNDg5YWQ0MGIzM2JkIiwic3ViIjoiQUNhMjkyODhjM2Q2YzM5NWRlOGVhMTg5ZGNkOWNkYzNkZSIsImV4cCI6MTczMDg5ODY4NywiZ3JhbnRzIjp7ImlkZW50aXR5IjoiaHNkN2pzc3V3ayIsInZvaWNlIjp7ImluY29taW5nIjp7ImFsbG93Ijp0cnVlfSwib3V0Z29pbmciOnsiYXBwbGljYXRpb25fc2lkIjoiQVBjNGI0NmU2ZmE4MzM5N2Y4ZWIxZjA1ZTk2YzM0ZTg5NSJ9fX19.d4jyarFaG8emLT5Qt5AQ0NV-jIk3R7NcW2Mv4n9jkrE"  // Replace with actual token from server
        callback(accessToken)
    }

    // Listener to handle call events
    private val callListener = object : Call.Listener {
        override fun onConnectFailure(call: Call, callException: CallException) {
            callException.printStackTrace()
            binding.numberDisplay.text = "Call Failed !!"
            Log.d("strVoice",callException.toString())
        }

        override fun onRinging(call: Call) {
            binding.numberDisplay.text = "Ringing..."
        }

        override fun onConnected(call: Call) {
            binding.numberDisplay.text = "Connected"
            binding.btnDisconnect.visibility = View.VISIBLE
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


}