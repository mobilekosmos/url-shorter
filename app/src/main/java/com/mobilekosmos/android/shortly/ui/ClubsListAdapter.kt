package com.mobilekosmos.android.shortly.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mobilekosmos.android.shortly.R
import com.mobilekosmos.android.shortly.data.model.ShortEntity
import com.mobilekosmos.android.shortly.ui.ClubsListAdapter.ClubViewHolder

// We don't use ViewBinding here as the overhead is not worth it: you must change/adapt the layout considerably, implement adapters, refactor the whole class, etc.
class ClubsListAdapter(clickListener: OnClubClickListener?) :
    RecyclerView.Adapter<ClubViewHolder>() {

    // We don't use lateinit because we cannot guarantee it's not accessed before initialization (getItemCount() is being called before).
    var dataset: List<ShortEntity>? = null
        set(value) {
            field = value
            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    private val onClickListener: OnClubClickListener

    init {
        requireNotNull(clickListener) { "You must pass a clickListener." }
        onClickListener = clickListener
    }

    interface OnClubClickListener {
        fun onClubClick(clubObject: ShortEntity, clubImageView: ImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.li_short, parent, false)
        val viewHolder = ClubViewHolder(view)
        viewHolder.itemView.setOnClickListener { clickedView: View ->
            dataset?.let {
                val pos = clickedView.tag as Int
                val clubObject = it[pos]
//                onClickListener.onClubClick(clubObject, viewHolder.clubImageView)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
//        dataset?.let {
//            val clubObject = it[position]
//            holder.clubTitleView.text = clubObject.name
//            holder.itemView.tag = position
//            holder.clubImageView.transitionName = clubObject.name
//            holder.clubSubTitleView.text = clubObject.country
//            holder.clubValueView.text = holder.clubSubTitleView.context.getString(
//                R.string.club_value,
//                clubObject.value.toString()
//            )
//        }
    }

    override fun getItemCount(): Int {
        return dataset?.size ?: 0
    }

    class ClubViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val clubTitleView: TextView = itemView.findViewById(R.id.club_title)
//        val clubSubTitleView: TextView = itemView.findViewById(R.id.club_subtitle)
//        val clubValueView: TextView = itemView.findViewById(R.id.club_value)
//        val clubImageView: ImageView = itemView.findViewById(R.id.club_image)
    }
}