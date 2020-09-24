package com.example.ekycdemo2.processor

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.ekycdemo2.model.IDCard
import com.example.ekycdemo2.utils.Constants.Companion.TAG
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import java.lang.Exception

class IDCardProcessor(val context: Context) : ImageAnalysis.Analyzer {

    var recognizer: TextRecognizer = TextRecognition.getClient()
    private var idCard: IDCard = IDCard()

    interface CallBackAnalyzer {
        fun onTextResults(texts: String)
        fun onCompleted()
        fun onRebindPreview()
    }

    private lateinit var callBackAnalyzer: CallBackAnalyzer

    fun setCallbacks(callBackAnalyzer: CallBackAnalyzer) {
        this.callBackAnalyzer = callBackAnalyzer
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image;
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { texts ->
                    processTextRecognitionResult(texts)
                    imageProxy.close()
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    imageProxy.close()
                }
        }
    }

    private fun processTextRecognitionResult(texts: Text) {
        callBackAnalyzer.onTextResults(texts.text)
        Log.d(TAG, texts.text)
        val blocks = texts.textBlocks
        if (blocks.size > 0) {
            val lines = blocks.flatMap { block -> block.lines }
            process(lines)
        }
    }

    private fun process(lines: List<Text.Line>) {
        var reducedLines: List<Text.Line> = ArrayList();
        if (idCard.facing == IDCard.FRONT) {
            for (line in lines) {
                val elements = line.elements;
                for (element in elements) {
                    if (element.text.matches(Regex("\\d{9}"))) {
                        idCard.id = element.text.toInt()
                        break;
                    }
                }
                if (idCard.id != null) {
                    reducedLines = lines.subList(lines.indexOf(line) + 1, lines.lastIndex + 1);
                    break
                };
            }
            if (reducedLines.isNotEmpty()) {
                try {
                    idCard.name = reducedLines[0].text.substring(reducedLines[0].text.indexOf(".") + 1);
                    idCard.birthDay = reducedLines[1].text.trim();
                    idCard.location = reducedLines[3].text.substring(12)
                    idCard.location = idCard.location + reducedLines[4].text.trim();
                    idCard.signedLocation = reducedLines[5].text.substring(20)
                    idCard.signedLocation = idCard.signedLocation + reducedLines[6].text.trim()
                    Log.d(TAG, "ID card detected");
                    Log.d(TAG, idCard.toString());
                    callBackAnalyzer.onRebindPreview()
                    idCard.facing = IDCard.BACK;
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }
            }
        } else {
            for (line in lines) {
                if (line.text.contains("DAU VET")) {
                    reducedLines = lines.subList(lines.indexOf(line) + 1, lines.lastIndex + 1);
                    try {
                        idCard.feature = reducedLines[0].text + " " + reducedLines[1].text;
                        idCard.issueDate = reducedLines[2].text.trim()
                        idCard.issueLocation = reducedLines[3].text.substring(9);
                        saveIDCard();
                        callBackAnalyzer.onCompleted()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return
                    }
                }
            }
        }
    }


    private val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    private fun saveIDCard() {
        val mDatabaseReference = Firebase.database.reference
        val userPN = sharedPreferences.getString("phone", "")
        mDatabaseReference.child("id_cards").child(userPN!!).setValue(idCard)
    }

    fun saveFilePath(path: String) {
        val editor = sharedPreferences.edit()
        if (idCard.facing == IDCard.FRONT) {
            editor.putString("id_card_front", path)
        } else {
            editor.putString("id_card_back", path)
        }
        editor.apply()
    }

    fun close() {
        recognizer.close()
    }

}