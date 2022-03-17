package com.kenetic.blockchainvs.datapack.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "party_data")
data class PartyData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "party_name") var partyName: String,
    @ColumnInfo(name = "party_promises") var partyPromises: String,
    @ColumnInfo(name = "party_selected") var partySelected:Boolean,
)