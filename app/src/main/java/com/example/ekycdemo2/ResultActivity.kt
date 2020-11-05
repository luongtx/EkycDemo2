package com.example.ekycdemo2

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ekycdemo2.IDCardScannerActivity.Companion.idCard
import com.example.ekycdemo2.utils.Constants
import kotlinx.android.synthetic.main.activity_result.*
import java.io.File

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        try {
            img_card_front.setImageURI(Uri.fromFile(idCard.storedFiles[0]))
            img_card_back.setImageURI(Uri.fromFile(idCard.storedFiles[1]))
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message);
        }
        val sharedReferenced = getSharedPreferences("prefs", MODE_PRIVATE)
        val pathFace = sharedReferenced.getString("img_face", "")
        img_face.setImageURI(Uri.fromFile(File(pathFace!!)))
        displayIDCardInfo();
    }

    private fun displayIDCardInfo() {
        tvID.text = idCard.id;
        tvName.text = idCard.name;
        tvDOB.text = idCard.birthDay;
        tvAdd.text = idCard.location;
        tvHKTT.text = idCard.location;
        tvNDK.text = idCard.signedDate;
    }
}