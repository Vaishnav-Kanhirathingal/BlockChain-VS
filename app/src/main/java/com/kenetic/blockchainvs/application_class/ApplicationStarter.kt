package com.kenetic.blockchainvs.application_class

import android.app.Application
import com.kenetic.blockchainvs.datapack.database.TransactionDatabase

class ApplicationStarter : Application() {
    val database: TransactionDatabase by lazy {
        TransactionDatabase.getDatabase(this)
    }
}