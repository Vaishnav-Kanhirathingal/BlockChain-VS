package com.kenetic.blockchainvs.datapack.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParty(transactionData: TransactionData)

    @Update
    suspend fun updateParty(transactionData: TransactionData)

    @Delete
    suspend fun deleteParty(transactionData: TransactionData)

    @Query("SELECT ID FROM TRANSACTION_HISTORY ORDER BY transaction_performed_at DESC")
    fun getAllIds(): Flow<List<Int>>

    @Query("SELECT * FROM TRANSACTION_HISTORY WHERE ID = :id")
    fun getById(id: Int): Flow<TransactionData>
}