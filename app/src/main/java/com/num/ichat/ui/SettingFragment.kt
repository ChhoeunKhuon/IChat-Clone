package com.num.ichat.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.num.ichat.R
import com.num.ichat.activity.NumberActivity
import com.num.ichat.model.UserModel
import de.hdodenhof.circleimageview.CircleImageView

class SettingFragment : Fragment() {

    private lateinit var profileImage: CircleImageView
    private lateinit var saveChangesButton: Button
    private lateinit var logOutButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var imageUrlEditText: EditText
    private lateinit var database: DatabaseReference

    private var currentUserUid: String = FirebaseAuth.getInstance().uid.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = inflater.inflate(R.layout.fragment_setting, container, false)

        // Initialize Views
        profileImage = binding.findViewById(R.id.profileImage)
        saveChangesButton = binding.findViewById(R.id.saveChangesButton)
        logOutButton = binding.findViewById(R.id.logOutButton)
        usernameEditText = binding.findViewById(R.id.usernameEditText)
        phoneNumberEditText = binding.findViewById(R.id.phoneNumberEditText)
        imageUrlEditText = binding.findViewById(R.id.imageUrlEditText)

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance("https://ichatapplication-e03ae-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")

        // Fetch user data from Firebase and set it to views
        loadUserData()

        // Set up Save Changes button
        saveChangesButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()
            val imageUrl = imageUrlEditText.text.toString()

            if (username.isNotEmpty() && phoneNumber.isNotEmpty() && imageUrl.isNotEmpty()) {
                saveChanges(username, phoneNumber, imageUrl)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up Log Out button
        logOutButton.setOnClickListener {
            logOut()
        }

        return binding
    }

    private fun loadUserData() {
        database.child(currentUserUid).get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(UserModel::class.java)
            if (user != null) {
                usernameEditText.setText(user.name)
                phoneNumberEditText.setText(user.number)
                imageUrlEditText.setText(user.imageUrl)

                // Load the profile image using Glide
                Glide.with(requireContext())
                    .load(user.imageUrl)
                    .placeholder(R.drawable.user) // Use default placeholder if no image
                    .into(profileImage)
            }
        }
    }

    private fun saveChanges(username: String, phoneNumber: String, imageUrl: String) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Confirm Changes")
            .setMessage("Are you sure you want to save these changes?")
            .setPositiveButton("Yes") { _, _ ->
                // Save the new username, phone number, and image URL to Firebase
                database.child(currentUserUid).child("name").setValue(username)
                database.child(currentUserUid).child("number").setValue(phoneNumber)
                database.child(currentUserUid).child("imageUrl").setValue(imageUrl)
                    .addOnSuccessListener {
                        // Update the profile image locally using Glide
                        Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.user)
                            .into(profileImage)

                        // Show success message
                        Toast.makeText(requireContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        // Show error message
                        Toast.makeText(requireContext(), "Failed to save changes", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Close the dialog
            }
            .create()

        alertDialog.show()
    }



    private fun logOut() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Confirm Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                navigateToLoginActivity()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        logOutButton.setOnClickListener {
            alertDialog.show()
        }
    }


    private fun navigateToLoginActivity() {
        val intent = Intent(requireActivity(), NumberActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

}
