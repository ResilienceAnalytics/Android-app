package com.example.blockexplorer

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var transactionAmount: EditText
    private lateinit var password: EditText
    private lateinit var participant1: EditText
    private lateinit var participant2: EditText
    private lateinit var submitButton: Button

    private lateinit var receivePassword: EditText
    private lateinit var receiveParticipant1: EditText
    private lateinit var receiveParticipant2: EditText
    private lateinit var receiveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize submit transaction UI elements
        transactionAmount = findViewById(R.id.transactionAmount)
        password = findViewById(R.id.password)
        participant1 = findViewById(R.id.participant1)
        participant2 = findViewById(R.id.participant2)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val amount = transactionAmount.text.toString()
            val pass = password.text.toString()
            val part1 = participant1.text.toString()
            val part2 = participant2.text.toString()

            if (amount.isNotEmpty() && pass.isNotEmpty() && part1.isNotEmpty() && part2.isNotEmpty()) {
                submitTransaction(amount.toInt(), pass, part1, part2)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize receive transaction UI elements
        receivePassword = findViewById(R.id.receivePassword)
        receiveParticipant1 = findViewById(R.id.receiveParticipant1)
        receiveParticipant2 = findViewById(R.id.receiveParticipant2)
        receiveButton = findViewById(R.id.receiveButton)

        receiveButton.setOnClickListener {
            val pass = receivePassword.text.toString()
            val part1 = receiveParticipant1.text.toString()
            val part2 = receiveParticipant2.text.toString()

            if (pass.isNotEmpty() && part1.isNotEmpty() && part2.isNotEmpty()) {
                receiveTransaction(pass, part1, part2)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun submitTransaction(amount: Int, password: String, participant1: String, participant2: String) {
        val client = OkHttpClient()
        val url = "http://your_server_address/submit_transaction" // Replace with your server URL

        val json = JSONObject().apply {
            put("transaction_amount", amount)
            put("password", password)
            put("participants", listOf(participant1, participant2))
        }

        val body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toString())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Transaction submitted successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Error: $responseBody", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun receiveTransaction(password: String, participant1: String, participant2: String) {
        val client = OkHttpClient()
        val url = "http://your_server_address/receive_transaction" // Replace with your server URL

        val json = JSONObject().apply {
            put("password", password)
            put("participants", listOf(participant1, participant2))
        }

        val body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toString())

        val request =
