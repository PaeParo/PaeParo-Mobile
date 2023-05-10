package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.model.Trip
import com.paeparo.paeparo_mobile.util.DateUtil

class TripAdapter(tripList: ArrayList<Trip>? = ArrayList()) :
    RecyclerView.Adapter<TripAdapter.TripViewHolder>() {
    private val _tripList: ArrayList<Trip>

    init {
        _tripList = tripList ?: ArrayList()
    }

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clItemTripInfo = itemView.findViewById<ConstraintLayout>(R.id.cl_item_trip_info)
        val ivItemTripMainImage =
            itemView.findViewById<ShapeableImageView>(R.id.iv_item_trip_main_image)
        val tvItemTripPlace = itemView.findViewById<TextView>(R.id.tv_item_trip_place)
        val tvItemTripDate = itemView.findViewById<TextView>(R.id.tv_item_trip_date)
        val llItemTripMembers = itemView.findViewById<LinearLayoutCompat>(R.id.ll_item_trip_members)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        return TripViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return _tripList.size
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = _tripList[position]

        holder.tvItemTripPlace.text = "장소명"
        holder.tvItemTripDate.text = holder.itemView.context.getString(
            R.string.date_range,
            DateUtil.getDateFromLong(trip.startDate, DateUtil.yyyyMMddFormat),
            DateUtil.getDateFromLong(trip.endDate, DateUtil.yyyyMMddFormat)
        )
    }
}