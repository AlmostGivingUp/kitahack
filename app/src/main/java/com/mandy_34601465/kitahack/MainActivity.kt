package com.mandy_34601465.kitahack
import com.mandy_34601465.kitahack.ui.theme.KitahackTheme

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KitahackTheme
        }
            }
        }
    }
}

@Composable
fun interface() {
    Surface{
        Column{
            Text()
        }
    }
}


public fun callGemini() {
    val MODEL = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = "AIzaSyA97GyI7tpBiuyBjSkghABi75qhxk2zncA"
        )
        val prompt = "Hello"
        MainScope().launch {
            val response = MODEL.generateContent(prompt)
        }
}

