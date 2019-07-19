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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_sleep_tracker.*
import kotlinx.android.synthetic.main.fragment_sleep_tracker.view.*

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        val application = requireNotNull(activity).application

        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        val sleepTrackerViewModel = ViewModelProviders.of(this, SleepTrackerViewModelFactory(dataSource, application)).get(SleepTrackerViewModel::class.java)

        //val sleepTrackerViewModel = ViewModelProviders.of(this.activity!!, SleepTrackerViewModelFactory(dataSource, application)).get(SleepTrackerViewModel::class.java)

        binding.lifecycleOwner = this

        binding.sleepTrackerViewModel = sleepTrackerViewModel

        binding.root.stop_button.setOnClickListener {
//            Log.i("SleepTrackerFragment", "STOP clicked")
            if (binding.sleepTrackerViewModel!!.tonight.value != null) {
                binding.sleepTrackerViewModel!!.onStopTracking()
                this.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(binding.sleepTrackerViewModel!!.tonight.value?.nightId!!))
            }
        }

        binding.root.clear_button.setOnClickListener {
            sleepTrackerViewModel.onClear()
            Snackbar.make(it,"Data cleared", Snackbar.LENGTH_SHORT).show()
        }
        val layoutManager = GridLayoutManager(context, 3)
        layoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(position) {
                    0 -> 3
                    else -> 1
                }
            }
        }



        val adapter = SleepNightAdapter(SleepNightOnClickListener{
//            nightId  -> Toast.makeText(context,"$nightId",Toast.LENGTH_LONG).show()
            this.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepDetailFragment(it))
        })

        sleepTrackerViewModel.nights.observe(this, Observer { adapter.submitItemListAndHeader(it) })

        binding.root.sleep_list.adapter = adapter
        binding.root.sleep_list.layoutManager = layoutManager

        return binding.root
    }
}
