/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {
    private val viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var tonight = MutableLiveData<SleepNight?>()

    // initialize tonight
    init {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private var nights = database.getAllNights()

    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()
            if (night?.endTimeMilli == night?.startTimeMilli) return@withContext night
            else return@withContext null
        }
    }


    fun onStartTracking() {
        if (tonight.value == null || tonight.value?.startTimeMilli != tonight.value?.endTimeMilli) {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    database.insert(SleepNight())
                }
                tonight.value = getTonightFromDatabase()
            }
        }
    }

    fun onStopTracking() {
        if (tonight.value == null) return
        Log.i("SleepTrackerViewModel", "onStopTracking pressed")
        uiScope.launch {
            tonight.value!!.endTimeMilli = System.currentTimeMillis()
            withContext(Dispatchers.IO) {
                database.update(tonight.value!!)
            }
            tonight.value = getTonightFromDatabase()
        }
    }

    fun onClear() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.clear()
            }
            tonight.value = getTonightFromDatabase()
        }
    }

    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }
}

