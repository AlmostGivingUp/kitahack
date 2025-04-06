package com.mandy_34601465.kitahack.chat


import java.util.UUID

enum class Participant {
    USER,
    OLTIE,
    ERROR
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    val participant: Participant = Participant.USER,
    var isPending: Boolean = false,
    val timestamp: Long = System.currentTimeMillis() //grab the timestamp
)
