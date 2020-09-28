package com.example.ekycdemo2.repos

import com.example.ekycdemo2.model.IDCard
import com.example.ekycdemo2.repos.impl.IDCardRepoImpl

interface IDCardRepo {
    fun saveIDCard(idCard: IDCard)
    fun getIDCard(dataCallback: IDCardRepoImpl.DataCallback)
}