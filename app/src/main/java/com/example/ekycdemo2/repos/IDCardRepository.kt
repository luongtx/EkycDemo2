package com.example.ekycdemo2.repos

import com.example.ekycdemo2.model.IDCard
import com.example.ekycdemo2.repos.impl.IDCardRepositoryImpl

interface IDCardRepository {
    fun saveIDCard(idCard: IDCard)
    fun getIDCard(dataCallback: IDCardRepositoryImpl.DataCallback)
}