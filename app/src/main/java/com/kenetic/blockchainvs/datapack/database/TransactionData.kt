package com.kenetic.blockchainvs.datapack.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.reactivex.internal.operators.maybe.MaybeDoAfterSuccess

@Entity(tableName = "transaction_history")
data class TransactionData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "transaction_hash") var transactionHash: String,
    @ColumnInfo(name = "method_called") var methodCalled: String,
    @ColumnInfo(name = "gas_fee") var gasFee: Long?,
    @ColumnInfo(name = "transaction_performed_at") var transactionTime: String,
    @ColumnInfo(name = "transaction_successful") var transactionSuccessful: Boolean?,
)