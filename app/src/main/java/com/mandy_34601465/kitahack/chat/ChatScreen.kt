package com.mandy_34601465.kitahack.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mandy_34601465.kitahack.GenerativeViewModelFactory
import com.mandy_34601465.kitahack.R
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
internal fun ChatRoute(
    chatViewModel: ChatViewModel = viewModel(factory = GenerativeViewModelFactory)
) {
    val chatUiState by chatViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.padding(20.dp),
        //create a message sending bar.
        bottomBar = {
            MessageInput(
                onSendMessage = { inputText ->
                    chatViewModel.sendMessage(inputText)
                },
                resetScroll = {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 0.dp, bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Oltie",
                    fontWeight = Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp
                )
            }
            // Messages List
            ChatList(chatUiState.messages, listState)
        }
    }
}


@Composable
fun ChatList(
    chatMessages: List<ChatMessage>,
    listState: LazyListState
) {
    LazyColumn(
        reverseLayout = true,
        state = listState
    ) {
        items(chatMessages.reversed()) { message ->
            ChatBubbleItem(message)
        }
    }
}

@Composable
fun ChatBubbleItem(
    chatMessage: ChatMessage
) {
    val isModelMessage = chatMessage.participant == Participant.OLTIE ||
            chatMessage.participant == Participant.ERROR

    val backgroundColor = when (chatMessage.participant) {
        Participant.OLTIE -> MaterialTheme.colorScheme.primaryContainer
        Participant.USER -> MaterialTheme.colorScheme.tertiaryContainer
        Participant.ERROR -> MaterialTheme.colorScheme.errorContainer
    }

    val bubbleShape = if (isModelMessage) {
        RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
    }

    val horizontalAlignment = if (isModelMessage) {
        //Adjusting textbox position according to owner of the message
        Alignment.Start
    } else {
        Alignment.End
    }

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = chatMessage.participant.name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row {
            if (chatMessage.isPending) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(all = 8.dp)
                )
            }
            BoxWithConstraints {
                Card(
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
                ) {
                    Text(
                        text = chatMessage.text,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}


//Speech recog to be added here:

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit = {}
) {
    var userMessage by rememberSaveable { mutableStateOf("") }


    //Added part stt
    var spokenText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var context = LocalContext.current


    val speechHelper = remember {
        SpeechToText(
            activity = context,
            onResult = {
                spokenText = it
            },
            onError = {
                spokenText = "Error: $it"
            }
        )
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {


            OutlinedTextField(
                value = if (isListening) spokenText else userMessage,
                label = { Text(stringResource(R.string.chat_label)) },
                onValueChange = {
                    if (isListening) {
                        spokenText = it
                    } else {
                        userMessage = it
                }},
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .weight(0.85f)
            )

            IconButton(
                onClick = {
                    if (isListening) {
                        isListening = false
                        speechHelper.stopListening()

                    } else {
                        isListening = true
                        speechHelper.startListening()}
                },
                modifier = Modifier
                    .padding(start = 3.dp)
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .weight(0.3f)
            ){
                Icon(
                    painter =
                        if (isListening) {
                            painterResource(R.drawable.mic)
                        } else {
                            painterResource(R.drawable.closemic)
                        }
                    ,
                    contentDescription = stringResource(R.string.action_send),
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }


            IconButton(
                onClick = {
                    if (userMessage.isNotBlank() or spokenText.isNotBlank()) {
                        onSendMessage(if (userMessage.isNotBlank()) userMessage else spokenText)
                        userMessage = ""
                        spokenText = ""
                        resetScroll()
                    }
                },
                modifier = Modifier
                    .padding(start = 3.dp)
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .weight(0.13f)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = stringResource(R.string.action_send),
                    modifier = Modifier
                )
            }
        }
    }
}
