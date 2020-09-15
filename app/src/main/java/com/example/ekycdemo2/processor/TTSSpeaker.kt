package com.example.ekycdemo2.processor

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.android.synthetic.main.activity_face_detection.*
import java.util.*

class TTSSpeaker(private val context: Context, private val textToSpeak: String) {
    lateinit var tts: TextToSpeech

    init {
        tts = TextToSpeech(context) {
            tts.language = Locale.UK
            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}