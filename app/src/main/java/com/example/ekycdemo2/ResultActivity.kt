package com.example.ekycdemo2

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ekycdemo2.model.IDCard
import com.example.ekycdemo2.repos.impl.IDCardRepoImpl
import kotlinx.android.synthetic.main.activity_result.*
import java.io.File

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val idCardRepo = IDCardRepoImpl(this)
        idCardRepo.getIDCard(object : IDCardRepoImpl.DataCallback {
            override fun onCallback(idCard: IDCard) {
                img_card_front.setImageURI(Uri.fromFile(File(idCard.storePaths[0])))
                img_card_back.setImageURI(Uri.fromFile(File(idCard.storePaths[1])))
                tv_result.text = idCard.toString()
            }
        })
        val sharedReferenced = getSharedPreferences("prefs", MODE_PRIVATE)
        val pathFace = sharedReferenced.getString("img_face", "")
        img_face.setImageURI(Uri.fromFile(File(pathFace!!)))
    }
}