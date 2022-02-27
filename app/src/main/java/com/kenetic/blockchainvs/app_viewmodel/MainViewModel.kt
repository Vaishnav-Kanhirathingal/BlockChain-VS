package com.kenetic.blockchainvs.app_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kenetic.blockchainvs.datapack.database.PartyDAO
import com.kenetic.blockchainvs.datapack.database.PartyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val partyDAO: PartyDAO) : ViewModel() {

    //---------------------------------------------------------------------------------dao-functions
    private fun insertParty(partyData: PartyData) {
        CoroutineScope(Dispatchers.IO).launch { partyDAO.insertParty(partyData) }
    }

    private fun updateParty(partyData: PartyData) {
        CoroutineScope(Dispatchers.IO).launch { partyDAO.updateParty(partyData) }
    }

    private fun deleteParty(partyData: PartyData) {
        CoroutineScope(Dispatchers.IO).launch { partyDAO.deleteParty(partyData) }
    }

    fun getAllById(): Flow<List<Int>> = partyDAO.getAllById()

    fun getById(id: Int): Flow<PartyData> = partyDAO.getById(id)
}

class MainViewModelFactory(private val partyDAO: PartyDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModelFactory(partyDAO) as T
        }
        throw IllegalArgumentException("Unknown Model Class")
    }
}