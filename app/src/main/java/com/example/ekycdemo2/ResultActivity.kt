package com.example.ekycdemo2

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_result.*
import java.io.File

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val sharedReferenced = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        tv_result.text = sharedReferenced.getString("IDCard", "null");
        val pathCardFront = sharedReferenced.getString("id_card_front", "");
        img_card_front.setImageURI(Uri.fromFile(File(pathCardFront!!)))
        val pathCardBack = sharedReferenced.getString("id_card_back", "")
        img_card_back.setImageURI(Uri.fromFile(File(pathCardBack!!)))
        val pathFace = sharedReferenced.getString("img_face", "")
        img_face.setImageURI(Uri.fromFile(File(pathFace!!)))
    }
}