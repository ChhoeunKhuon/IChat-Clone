package com.num.ichat.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.num.ichat.MainActivity
import com.num.ichat.databinding.ActivityOtpactivityBinding
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpactivityBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        dialog = AlertDialog.Builder(this)
            .setMessage("Please wait...")
            .setCancelable(false)
            .create()
        dialog.show()

        val phoneNumber = "+855${intent.getStringExtra("number")}"

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    dialog.dismiss()
                    Toast.makeText(this@OTPActivity, "Verification failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, token)
                    this@OTPActivity.verificationId = verificationId
                    dialog.dismiss()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        binding.button.setOnClickListener {
            val otpCode = binding.otp.text.toString()
            if (otpCode.isEmpty()) {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
            } else {
                dialog.show()
                val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
                signInWithCredential(credential)
            }
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            dialog.dismiss()
            if (task.isSuccessful) {
//                startActivity(Intent(this, ProfileActivity::class.java))
//                finish()
                val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                checkUserProfileExists(uid)
            } else {
                Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserProfileExists(uid: String) {
        val database = FirebaseDatabase.getInstance("https://ichatapplication-e03ae-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val userReference = database.reference.child("users").child(uid)

        userReference.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // User profile exists, navigate to MainActivity
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
            } else {
                // User profile does not exist, navigate to ProfileActivity
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error checking profile: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}
