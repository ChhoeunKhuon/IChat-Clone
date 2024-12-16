package com.num.ichat.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.num.ichat.adapter.ChatAdapter
import com.num.ichat.databinding.FragmentChatBinding
import com.num.ichat.model.UserModel

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var userList: ArrayList<UserModel>
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy {
        FirebaseDatabase.getInstance("https://ichatapplication-e03ae-default-rtdb.asia-southeast1.firebasedatabase.app/")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        userList = ArrayList()

        // Initialize RecyclerView Adapter
        binding.userListRecyclerView.adapter = ChatAdapter(requireContext(), userList)

        // Load users from Firebase
        loadUsers()

        return binding.root
    }

    private fun loadUsers() {
        database.reference.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (snapshot1 in snapshot.children) {
                    val user = snapshot1.getValue(UserModel::class.java)
                    if (user != null && user.uid != firebaseAuth.uid) {
                        userList.add(user)
                    }
                }
                // Notify RecyclerView adapter of data changes
                (binding.userListRecyclerView.adapter as ChatAdapter).notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle Firebase error
                error.message?.let { showToast("Error: $it") }
            }
        })
    }




    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
