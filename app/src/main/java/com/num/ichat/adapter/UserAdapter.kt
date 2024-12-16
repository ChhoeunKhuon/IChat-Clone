package com.num.ichat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.num.ichat.R
import com.num.ichat.model.UserModel
import de.hdodenhof.circleimageview.CircleImageView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.num.ichat.databinding.ChatUserItemLayoutBinding

class UserAdapter(
    private var userList: List<UserModel>,
    private val onUserClick: (UserModel) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var filteredList = userList.filter { it.uid != FirebaseAuth.getInstance().uid }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ChatUserItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_user_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = filteredList[position]

        Glide.with(holder.itemView.context)
            .load(user.imageUrl)
            .placeholder(R.drawable.user)
            .into(holder.binding.userImage)

        holder.binding.userName.text = user.name
        holder.binding.recentMsg.text = "Tap to Chat"

        holder.itemView.setOnClickListener {
            onUserClick(user)
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun updateList(newList: List<UserModel>) {
        userList = newList
        filteredList = userList.filter { it.uid != FirebaseAuth.getInstance().uid }
        notifyDataSetChanged()
    }
}



