package com.kenetic.blockchainvs.app_viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kenetic.blockchainvs.datapack.database.TransactionDAO
import com.kenetic.blockchainvs.datapack.database.TransactionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val transactionDAO: TransactionDAO) : ViewModel() {
    val transactionInProgress = "Transaction currently in progress..."
    val calling = "calling function..."
    val unknown = "Unknown..."
    val callNotPerformedYet = "Call Not Performed Yet"
    val gettingGasUsed = "Getting Gas Used..."
    val gasUsedIs = "Gas used for transaction : "

    var userUsesFingerprint = false

    //------------------------------------------------------------------------data-binding-live-data
    val transactionCost: MutableLiveData<String> = MutableLiveData(callNotPerformedYet)
    val addressList: MutableLiveData<String> = MutableLiveData(callNotPerformedYet)
    val alreadyVoted: MutableLiveData<String> = MutableLiveData(callNotPerformedYet)
    val allPartyVotes: MutableLiveData<String> = MutableLiveData(callNotPerformedYet)
    val balance: MutableLiveData<String> = MutableLiveData(callNotPerformedYet)
    val addMeToVotersList: MutableLiveData<String> = MutableLiveData(callNotPerformedYet)

    //---------------------------------------------------------------------------------dao-functions
    fun insertTransaction(transactionData: TransactionData) {
        CoroutineScope(Dispatchers.IO).launch { transactionDAO.insertParty(transactionData) }
    }

    fun updateTransaction(transactionData: TransactionData) {
        CoroutineScope(Dispatchers.IO).launch { transactionDAO.updateParty(transactionData) }
    }

    fun deleteTransaction(transactionData: TransactionData) {
        CoroutineScope(Dispatchers.IO).launch { transactionDAO.deleteParty(transactionData) }
    }

    fun getAllById(): Flow<List<Int>> = transactionDAO.getAllIds()

    fun getById(id: Int): Flow<TransactionData> = transactionDAO.getById(id)
}

class MainViewModelFactory(private val transactionDAO: TransactionDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(transactionDAO) as T
        }
        throw IllegalArgumentException("Unknown Model Class")
    }
}