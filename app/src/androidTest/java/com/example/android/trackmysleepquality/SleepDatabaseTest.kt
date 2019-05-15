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

package com.example.android.trackmysleepquality

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import java.io.IOException

/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var sleepDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        sleepDao = db.sleepDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() {
        val night = SleepNight()
        sleepDao.insert(night)
        val tonight = sleepDao.getTonight()
        assertEquals(tonight?.sleepQuality, -1)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAllNights() {
        val night1 = SleepNight()
        val night2 = SleepNight()
        sleepDao.insert(night1)
        sleepDao.insert(night2)
        val allNights = LiveDataTestUtil.getValue(sleepDao.getAllNights())

        assertEquals(allNights.size, 2)
    }

    @Test
    @Throws(Exception::class)
    fun updateNight() {
        var night = SleepNight()
        sleepDao.insert(night)
        night = sleepDao.getTonight()!!
        night.sleepQuality = 5
        sleepDao.update(night)
        val thisNight = sleepDao.get(night.nightId)
        assertEquals(thisNight?.sleepQuality, night.sleepQuality)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAllNights() {
        val night = SleepNight()
        var rowsCount : Int? = null
        sleepDao.insert(night)
        var allNights = LiveDataTestUtil.getValue(sleepDao.getAllNights())
        assertEquals(1, allNights.size )

        //assertEquals(rowsCount, 1)
        sleepDao.clear()
        allNights = LiveDataTestUtil.getValue(sleepDao.getAllNights())
        assertEquals(0, allNights.size )
    }
}

