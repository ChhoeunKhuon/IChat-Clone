package com.num.ichat.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.num.ichat.databinding.ActivityNumberBinding

class NumberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNumberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Button click listener for submitting the phone number
        binding.button.setOnClickListener {
            val phoneNumber = binding.phoneNumber.text.toString()
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, OTPActivity::class.java)
                intent.putExtra("number", phoneNumber)
                startActivity(intent)
            }
        }
    }
}
