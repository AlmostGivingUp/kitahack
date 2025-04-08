package com.mandy_34601465.kitahack


import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
//import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import com.google.androidgamesdk.gametextinput.Settings
import com.mandy_34601465.kitahack.chat.ChatRoute

import com.mandy_34601465.kitahack.ui.theme.KitahackTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
//dotnet add package Vosk //TODO: is this right???

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KitahackTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {

                    val navController = rememberNavController()
                    //navController gives default screen to chat (skipping menu)
                    //TODO: Adding other activities perchance.

                    NavHost(navController = navController, startDestination = "loading") {
                        composable("loading") {
                            LoadingScreen(navController)
                        }
                        composable("chat") {
                            ChatRoute()
                        }
                    }
                }
            }
        }

        fun startSpeechToText() {
            val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)

            val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(bundle: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(v: Float) {}
                override fun onBufferReceived(bytes: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(i: Int) {}

                override fun onResults(bundle: Bundle) {
                    val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (result != null) {
                        // result[0] will give the output of speech
                    }
                }

                override fun onPartialResults(bundle: Bundle) {}
                override fun onEvent(i: Int, bundle: Bundle?) {}
            })
            // starts listening ...
            speechRecognizer.startListening(speechRecognizerIntent)
        }

        fun checkAudioPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // M = 23
                if (ContextCompat.checkSelfPermission(
                        this,
                        "android.permission.RECORD_AUDIO"
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // this will open settings which asks for permission
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:com.programmingtech.offlinespeechtotext")
                    )
                    startActivity(intent)
                    Toast.makeText(this, "Allow Microphone Permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}



