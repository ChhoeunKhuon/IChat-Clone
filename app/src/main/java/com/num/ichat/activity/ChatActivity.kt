package com.num.ichat.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.num.ichat.R
import com.num.ichat.adapter.MessageAdapter
import com.num.ichat.databinding.ActivityChatBinding
import com.num.ichat.databinding.ActivityNumberBinding
import com.num.ichat.model.MessageModel
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var senderUid: String
    private lateinit var receiverUid: String
    private lateinit var chatRoomId: String // Unified room for sender and receiver
    private lateinit var list: ArrayList<MessageModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get sender UID (current user) and receiver UID (from the intent)
        senderUid = FirebaseAuth.getInstance().uid.toString()
        receiverUid = intent.getStringExtra("uid")!!

        // Initialize the message list
        list = ArrayList()

        // Generate chat room ID by lexicographically sorting sender and receiver UIDs
        chatRoomId = if (senderUid < receiverUid) senderUid + receiverUid else receiverUid + senderUid

        // Inflate layout and set content view
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase database
        database = FirebaseDatabase.getInstance("https://ichatapplication-e03ae-default-rtdb.asia-southeast1.firebasedatabase.app/")

        // Send message on button click
        binding.imageView.setOnClickListener {
            if (binding.messageBox.text.isEmpty()) {
                Toast.makeText(this, "Please enter your message", Toast.LENGTH_SHORT).show()
            } else {
                val message = MessageModel(binding.messageBox.text.toString(), senderUid, Date().time)
                val randomKey = database.reference.push().key!!

                // Send message to both sender and receiver's chat rooms (same room for both)
                database.reference.child("chats")
                    .child(chatRoomId).child("message").child(randomKey).setValue(message)
                    .addOnSuccessListener {
                        binding.messageBox.text = null // Clear message box after sending
                        Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // Listen for messages in the unified chat room
        database.reference.child("chats").child(chatRoomId).child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear() // Clear the current message list

                    // Add new messages to the list
                    for (snapshot1 in snapshot.children) {
                        val data = snapshot1.getValue(MessageModel::class.java)
                        data?.let { list.add(it) }
                    }

                    // Update the RecyclerView adapter to display the new messages
                    binding.recyclerView.adapter = MessageAdapter(this@ChatActivity, list)
                    // Scroll to the latest message
                    binding.recyclerView.scrollToPosition(list.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
