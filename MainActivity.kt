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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    }

    private fun submitTransaction(amount: Int, password: String, participant1: String, participant2: String) {
        val client = OkHttpClient()
        val url = "107.172.140.130:5000/submit_transaction"

        val json = JSONObject()
        json.put("transaction_amount", amount)
        json.put("password", password)
        json.put("participants", listOf(participant1, participant2))

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())

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
}
