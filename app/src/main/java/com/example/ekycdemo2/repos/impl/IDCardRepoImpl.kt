package com.example.ekycdemo2.repos.impl

import android.content.Context
import android.util.Log
import com.example.ekycdemo2.model.IDCard
import com.example.ekycdemo2.repos.IDCardRepo
import com.example.ekycdemo2.utils.Constants.Companion.ROOT_NODE
import com.example.ekycdemo2.utils.Constants.Companion.TAG
import com.example.ekycdemo2.utils.Constants.Companion.userPN
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class IDCardRepoImpl : IDCardRepo {

    interface DataCallback {
        fun onCallback(idCard: IDCard);
    }

    override fun saveIDCard(idCard: IDCard) {
        val mDatabaseReference = Firebase.database.reference
        mDatabaseReference.child(ROOT_NODE).child(userPN).setValue(idCard)
        Log.d(TAG, "save id card successfully!")
    }

    override fun getIDCard(dataCallback: DataCallback) {
        val mDatabaseReference = Firebase.database.reference
        val idCardReference = mDatabaseReference.child(ROOT_NODE).child(userPN)
        idCardReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val idCard = snapshot.getValue<IDCard>()
                dataCallback.onCallback(idCard!!);
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}