package com.example.triply

import android.app.Application
import com.example.triply.data.local.TriplyDatabase
import com.example.triply.data.repository.TriplyRepository

class TriplyApplication : Application() {
    val database: TriplyDatabase by lazy { TriplyDatabase.getDatabase(this) }
    val repository: TriplyRepository by lazy { TriplyRepository(database.triplyDao()) }
}
