**Google Technology**
1. Android Studio
User Interface is coded using Kotlin and Jetpack Compose, utilizing SpeechRecognizer and AlarmManager API to achieve speech-to-  text, text-to-speech, and setting reminders.

3. Firebase
In order to keep records of chat history, Firebase is being used. At this prototype stage, no security has been set up to prevent    unauthorised access to the chat history yet.

**Google AI**
1. Gemini AI
API key is obtained from Google AI Studio to access Gemini AI as ‘Oltie’ in this caretaker app. Her prompt is set up in the
ChatViewModel.kt file playing the role of caretaker.

**Challenge**
The biggest challenge is trying to implement the AlarmManager. There are many edge cases on how reminders for medicines are being set, including how can the information be parsed. We ended up struggling to trigger the alarm since we are not eloquent in using this API yet. 

We solved this by instructing Gemini to reply in a certain format with a trigger phrase, triggering the parsing of the response to build a reminder. The alarm is not triggered due to the notification permission not asked, hence we tried to ask for permission for POST_NOTIFICATION


**Steps**
1. Download Android Studio
2. Wait for Gradle file to sync
3. Connect **Android** phone to laptop/Computer using USB cable
4. On your phone, go to Settings > About > Build Number
5. Tap on Build Number 7 times
6. Enable Developer Mode
7. Allow Debugging on your phone
8. The app can now be launched on you phone, have fun!! 
