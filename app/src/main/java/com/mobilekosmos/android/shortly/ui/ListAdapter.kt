package com.mobilekosmos.android.shortly.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mobilekosmos.android.shortly.R
import com.mobilekosmos.android.shortly.data.model.ShortURLEntity

// We don't use ViewBinding here as the overhead is not worth it: you must change/adapt the layout considerably, implement adapters, refactor the whole class, etc.
class ListAdapter(
    private val onClickListener: OnListItemClickListener
) : ListAdapter<ShortURLEntity, com.mobilekosmos.android.shortly.ui.ListAdapter.UrlsViewHolder>(
    PlantDiffCallback()
) {

    interface OnListItemClickListener {
        fun onDeleteClick(urlEntity: ShortURLEntity)
        fun onCopyClick(shortLink: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrlsViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.li_url, parent, false)
        val viewHolder = UrlsViewHolder(view)
        viewHolder.urlDeleteView.setOnClickListener { clickedView: View ->
            val shortLink = clickedView.tag as String
            currentList.forEach {
                if (it.short_link == shortLink) {
                    onClickListener.onDeleteClick(it)
                    return@forEach
                }
            }

        }
        viewHolder.urlCopyButton.setOnClickListener { clickedView: View ->
            val shortLink = clickedView.tag as String
            onClickListener.onCopyClick(shortLink)
        }
        return viewHolder
    }

    class UrlsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val urlShortView: TextView = itemView.findViewById(R.id.url_short)
        val urlOriginalView: TextView = itemView.findViewById(R.id.url_original)
        val urlDeleteView: ImageView = itemView.findViewById(R.id.url_delete)
        val urlCopyButton: MaterialButton = itemView.findViewById(R.id.url_copy)
    }

    override fun onBindViewHolder(holder: UrlsViewHolder, position: Int) {
        val urlEntity = getItem(position)
        Log.d("+++", "onBindViewHolder pos: $position url: $urlEntity.original_link")
        holder.urlShortView.text = urlEntity.short_link
        holder.urlOriginalView.text = urlEntity.original_link
        holder.urlDeleteView.tag = urlEntity.short_link
        holder.urlCopyButton.tag = urlEntity.short_link
    }
}

private class PlantDiffCallback : DiffUtil.ItemCallback<ShortURLEntity>() {

    override fun areItemsTheSame(oldItem: ShortURLEntity, newItem: ShortURLEntity): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: ShortURLEntity, newItem: ShortURLEntity): Boolean {
        return oldItem == newItem
    }
}