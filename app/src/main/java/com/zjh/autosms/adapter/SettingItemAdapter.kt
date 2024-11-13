package com.zjh.autosms.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.zjh.autosms.R
import com.zjh.autosms.entity.SettingItem

class SettingItemAdapter : RecyclerView.Adapter<SettingItemAdapter.SettingItemViewHolder>() {
    var data: List<SettingItem>? = null
    var onChildClickListener: ((SettingItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false)
        val holder = SettingItemViewHolder(view)
        holder.tvEnabled.setOnClickListener {
            data?.getOrNull(holder.bindingAdapterPosition)?.let {
                onChildClickListener?.invoke(it)
            }
        }
        return holder
    }

    override fun getItemCount() = data?.size ?: 0

    override fun onBindViewHolder(holder: SettingItemViewHolder, position: Int) {
        val item = data?.getOrNull(position) ?: return
        holder.tvName.text = item.getName()
        holder.tvEnabled.visibility = View.GONE
//        holder.tvEnabled.isEnabled = !item.enabled
//        holder.tvEnabled.text = if (item.enabled) "已开启" else "去开启"
        holder.sw.visibility = View.VISIBLE
        holder.sw.isChecked = item.enabled
        holder.sw.setOnCheckedChangeListener { _, isChecked ->
            data?.getOrNull(holder.bindingAdapterPosition)?.let {
                if (it.enabled != isChecked) {
                    onChildClickListener?.invoke(it)
                }
            }
        }
        holder.tvDescription.text = item.getDescription()
    }

    class SettingItemViewHolder(view: View) : ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvEnabled: TextView = view.findViewById(R.id.tv_enabled)
        val tvDescription: TextView = view.findViewById(R.id.tv_description)
        val sw: SwitchCompat = view.findViewById(R.id.sw)
    }
}