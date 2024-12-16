package com.num.ichat.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.num.ichat.MainActivity
import com.num.ichat.databinding.ActivityProfileBinding
import com.num.ichat.model.UserModel
import java.io.ByteArrayOutputStream
import android.util.Base64

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var dialog: AlertDialog

    private var selectedImageUri: Uri? = null
    private var base64Image: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance("https://ichatapplication-e03ae-default-rtdb.asia-southeast1.firebasedatabase.app/")

        dialog = AlertDialog.Builder(this)
            .setMessage("Updating Profile...")
            .setCancelable(false)
            .create()

        binding.continueBtn.setOnClickListener {
            val userName = binding.userName.text.toString()
            if (userName.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            } else {
                dialog.show()
                uploadProfileInfo()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
            binding.userImage.setImageBitmap(bitmap)
            base64Image = encodeImageToBase64(bitmap)
        }
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun uploadProfileInfo() {
        val user = UserModel(
            uid = auth.uid!!,
            name = binding.userName.text.toString(),
            number = auth.currentUser?.phoneNumber ?: "",
            imageUrl = "" //No image use
        )

        val database = FirebaseDatabase.getInstance("https://ichatapplication-e03ae-default-rtdb.asia-southeast1.firebasedatabase.app/")

        // Upload user data to the specified URL
        database.reference.child("users")
            .child(auth.uid!!)
            .setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
