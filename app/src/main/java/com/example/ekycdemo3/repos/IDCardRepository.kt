package com.example.ekycdemo3.repos

import com.example.ekycdemo3.model.IDCard
import com.example.ekycdemo3.repos.impl.IDCardRepositoryImpl

interface IDCardRepository {
    fun saveIDCard(idCard: IDCard)
    fun getIDCard(dataCallback: IDCardRepositoryImpl.DataCallback)
}