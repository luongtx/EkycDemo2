package com.example.ekycdemo2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ekycdemo2.utils.Constants.Companion.ROOT_NODE
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_confirm.setOnClickListener {
            confirm()
        }
    }

    private fun confirm() {
        if (et_phone.text.isEmpty() || et_email.text.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_phone_email), Toast.LENGTH_SHORT).show()
            return
        }
        saveData();
        val intent = Intent(this, ActivityDetectorOption::class.java)
        startActivity(intent)
    }

    private fun saveData() {
        val mPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        val editor = mPreferences.edit()
        val userPN = et_phone.text.toString()
        editor.putString("phone", userPN)
        editor.apply()
        val mFirebaseReference = Firebase.database.reference
        mFirebaseReference.child(ROOT_NODE).child(userPN).push()
    }
}