package com.kenetic.blockchainvs.applicationclass

import android.app.Application
import com.kenetic.blockchainvs.datapack.database.PartyDatabase

class ApplicationStarter : Application() {
    val database: PartyDatabase by lazy {
        PartyDatabase.getDatabase(this)
    }
}