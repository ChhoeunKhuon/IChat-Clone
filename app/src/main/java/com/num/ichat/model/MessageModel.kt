package com.num.ichat.model

data class MessageModel(
    val message: String? = "",
    val senderId: String? = "",
    val timeStamp: Long? = 0,
)
