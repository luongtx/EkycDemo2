package com.example.ekycdemo2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        val intent = Intent(this, IDCardScannerActivity::class.java)
        startActivity(intent)
    }

    private fun saveData() {
        val mFirebaseReference = Firebase.database.reference;
        mFirebaseReference.child("id_cards").setValue(et_phone.text.toString())
        val mPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        val editor = mPreferences.edit()
        editor.putString("phone", et_phone.text.toString())
    }
}