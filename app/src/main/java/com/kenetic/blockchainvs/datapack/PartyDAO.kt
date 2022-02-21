package com.kenetic.blockchainvs.datapack

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PartyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParty(partyData: PartyData)

    @Update
    suspend fun updateParty(partyData: PartyData)

    @Delete
    suspend fun deleteParty(partyData: PartyData)

    @Query("")// TODO: add query
    fun getById(): Flow<List<Int>>
}