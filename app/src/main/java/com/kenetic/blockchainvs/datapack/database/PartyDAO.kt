package com.kenetic.blockchainvs.datapack.database

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

    @Query("SELECT ID FROM PARTY_DATA")
    fun getAllById(): Flow<List<Int>>

    @Query("SELECT * FROM PARTY_DATA WHERE ID = :id")
    fun getById(id: Int): Flow<PartyData>
}