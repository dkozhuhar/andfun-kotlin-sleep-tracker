package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding

class SleepNightAdapter(val listener: SleepNightOnClickListener): ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, listener)
    }


    class ViewHolder private constructor(val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: SleepNight, listener: SleepNightOnClickListener) {
            binding.onClickListener = listener
            binding.sleepNight = item
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ListItemSleepNightBinding>(layoutInflater,R.layout.list_item_sleep_night, parent, false)

                return ViewHolder(binding)
            }
        }
    }


}
class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>() {
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }
}

class SleepNightOnClickListener(val onClickListener: (nighid: Long) -> Unit){
    fun onClick(night: SleepNight) {
        onClickListener(night.nightId)
    }
}