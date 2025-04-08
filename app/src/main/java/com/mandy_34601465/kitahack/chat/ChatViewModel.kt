package com.mandy_34601465.kitahack.chat

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import com.google.firebase.firestore.FirebaseFirestore
import com.mandy_34601465.kitahack.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class ChatViewModel(
    generativeModel: GenerativeModel,
    application: Application
) : AndroidViewModel(application) {
    //need access to Android system services,
    @RequiresApi(Build.VERSION_CODES.O)
    val currentDateTime = LocalDateTime.now()
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    @RequiresApi(Build.VERSION_CODES.O)
    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "model") {
                text(
                    """
                You are a helpful, and compassionate caretaker assistant. Your Oltie. 
                The current datetime is $currentDateTime,  for date, reply in Day Month and Year if possible.
                For time, reply in hh:MM am or pm. 
                
                Please talk in a natural manner. 
                

                When the user asks for a reminder or medication schedule:
                    If user does not provide specific Date, ask if it is everyday. If yes, set Date to everyday.
                    If user provided a specific day, set Date to the day name (e.g: Friday).
                    If user provided a many different days, set Date to the day names separated by commas. 
                  
                    If user provides more than one time, set Time to the times separated by a comma. 
                    If User does not provide a specific time, keep asking for for a specific time 
                
                Then, start with 
                "I'll remind you.",
                "Righty tighty.",
                "reminder set!",
                "setting a reminder!", or
                "okay, i've added your reminder!"  then use this exact format:
                
                Date: YYYY-MM-DD
                Time: HH:MM
                Medication: [Name or Task]
                
                

                Only use this format if a reminder is involved. Otherwise, respond naturally.
                """.trimIndent()
                )
            }
        )
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private val _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(ChatUiState(chat.history.map { content ->
            // Map the initial messages
            ChatMessage(
                text = content.parts.first().asTextOrNull() ?: "", //upon initialisation, the first message is gone.
                participant = if (content.role == "user") Participant.USER else Participant.OLTIE,
                isPending = false
            )
        }))

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<ChatUiState> =
        _uiState.asStateFlow()


    init {
        loadHistoryFromFirebase()
    } //upon opening the app, load all previous chats...

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadHistoryFromFirebase() {
        viewModelScope.launch {
            try {
                val messages = ChatRepo().loadMessages()
                _uiState.value = ChatUiState(messages)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(userMessage: String) {
        // Add a pending message
        //Pass the message as an argument in ChatMessage.
        val userResponse = ChatMessage(
            text = userMessage,
            participant = Participant.USER,
            isPending = true
        )
        _uiState.value.addMessage(userResponse)


        viewModelScope.launch {
            try {
                userResponse.isPending = false //Stop loading
                ChatRepo().saveMessage(userResponse)

                val response = chat.sendMessage(userMessage)

                _uiState.value.replaceLastPendingMessage()

                //check if the response is a null, if not null, replace the value in response
                response.text?.let { modelResponse ->
                    val response = ChatMessage(
                            text = modelResponse,
                            participant = Participant.OLTIE,
                            isPending = false
                    )

                    _uiState.value.addMessage(response)
                    ChatRepo().saveMessage(response)
                    //TODO: Check and trim AI response:
                    val Alarm = AlarmScheduler(context)
                    val AIresponse = response.toString()
                    if (Alarm.shouldScheduleReminderFromResponse(AIresponse)) {
                        Alarm.handleAIResponse(AIresponse, context)
                    }

                }
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    ChatMessage(
                        text = e.localizedMessage,
                        participant = Participant.ERROR
                    )


                )
            }
        }
    }
}

