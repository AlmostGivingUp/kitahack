package com.mandy_34601465.kitahack.chat

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChatRepo {
    //grab Firebase class and create a collection for history storing
    private val db = FirebaseFirestore.getInstance()
    private val chatCollection = db.collection("chats")

    //suspend keyword delays execution without affecting UI
    suspend fun saveMessage(message: ChatMessage) {
        chatCollection.add(message).await()
    }

    suspend fun loadMessages(): List<ChatMessage> {
        return chatCollection
            .orderBy("timestamp")
            .get()
            .await()
            .toObjects(ChatMessage::class.java)
    }

    companion object
}