package com.example.ekycdemo2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val sharedReferenced = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        tv_result.text = sharedReferenced.getString("IDCard","null");
    }
}