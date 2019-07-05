package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import java.lang.ClassCastException



class SleepNightAdapter(val listener: SleepNightOnClickListener): ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {

    private val VIEW_HOLDER_TYPE_HEADER = 0
    private val VIEW_HOLDER_TYPE_ITEM = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return ViewHolder.from(parent)
        return when (viewType) {
            VIEW_HOLDER_TYPE_HEADER -> TextViewHolder.from(parent)
            VIEW_HOLDER_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown ViewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_HOLDER_TYPE_HEADER
            else -> VIEW_HOLDER_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(position) as DataItem.SleepNightItem
                holder.bind(item.night, listener)
            }

        }


    }
    class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.header, parent, false)
                return TextViewHolder(view)
            }
        }
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
class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class SleepNightOnClickListener(val onClickListener: (nighid: Long) -> Unit){
    fun onClick(night: SleepNight) {
        onClickListener(night.nightId)
    }
}

sealed class DataItem{
    data class SleepNightItem(val night: SleepNight) : DataItem() {
        override val id: Long
            get() = night.nightId
    }
    object header : DataItem(){
        override val id: Long
            get() = Long.MIN_VALUE
    }
    abstract val id: Long
}