package com.example.ekycdemo2

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ekycdemo2.model.IDCard
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_result.*
import java.io.File

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val sharedReferenced = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val userPN = sharedReferenced.getString("phone", "")

        val mDatabaseReference = Firebase.database.reference
        val idCardReference = mDatabaseReference.child("id_cards").child(userPN!!)
        idCardReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val idCard = snapshot.getValue<IDCard>()
                tv_result.text = idCard?.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        val pathCardFront = sharedReferenced.getString("id_card_front", "");
        img_card_front.setImageURI(Uri.fromFile(File(pathCardFront!!)))
        val pathCardBack = sharedReferenced.getString("id_card_back", "")
        img_card_back.setImageURI(Uri.fromFile(File(pathCardBack!!)))
        val pathFace = sharedReferenced.getString("img_face", "")
        img_face.setImageURI(Uri.fromFile(File(pathFace!!)))
    }
}