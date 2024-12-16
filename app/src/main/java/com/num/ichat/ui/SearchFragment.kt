package com.num.ichat.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.num.ichat.R
import com.num.ichat.activity.ChatActivity
import com.num.ichat.adapter.UserAdapter
import com.num.ichat.model.UserModel

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var database: DatabaseReference
    private var userList = mutableListOf<UserModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.userListRecyclerView)
        val searchView: SearchView = view.findViewById(R.id.idUser)

        adapter = UserAdapter(userList) { user ->
            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                putExtra("uid", user.uid)
                putExtra("name", user.name)
                putExtra("imageUrl", user.imageUrl)
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance("https://ichatapplication-e03ae-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users")

        fetchUsers()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = userList.filter {
                    it.name.contains(newText ?: "", ignoreCase = true)
                }
                adapter.updateList(filteredList) // Update adapter with filtered list
                return true
            }
        })

        return view
    }

    private fun fetchUsers() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (data in snapshot.children) {
                    val user = data.getValue(UserModel::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                adapter.updateList(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}

