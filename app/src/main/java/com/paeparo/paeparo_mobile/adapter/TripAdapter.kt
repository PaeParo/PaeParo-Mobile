package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.ItemTripsContentBinding
import com.paeparo.paeparo_mobile.databinding.ItemTripsHeaderBinding
import com.paeparo.paeparo_mobile.model.Trip
import com.paeparo.paeparo_mobile.util.DateUtil

/**
 * TripAdapter class
 *
 * TripFragment의 여행 목록을 관리하는 Adapter 클래스
 *
 * @property tripList Trip 목록
 */
class TripAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        /**
         * Item Type Header
         */
        const val ITEM_TYPE_HEADER = 0

        /**
         * Item Type Content
         */
        const val ITEM_TYPE_CONTENT = 1
    }

    /**
     * Header, Content로 구성된 Trip 목록
     */
    private var tripList: MutableList<Any> = mutableListOf()

    override fun getItemViewType(position: Int): Int {
        return if (tripList[position] is String) {
            ITEM_TYPE_HEADER
        } else {
            ITEM_TYPE_CONTENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_HEADER ->
                HeaderViewHolder(ItemTripsHeaderBinding.inflate(inflater, parent, false))

            ITEM_TYPE_CONTENT ->
                ContentViewHolder(ItemTripsContentBinding.inflate(inflater, parent, false))

            else ->
                throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(tripList[position] as String)
        } else if (holder is ContentViewHolder) {
            holder.bind(tripList[position] as Trip)
        }
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    /**
     * Header를 담고 있는 ViewHolder
     *
     * @property binding ItemTripsHeaderBinding
     */
    inner class HeaderViewHolder(private val binding: ItemTripsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: String) {
            binding.tvItemTripsHeaderTitle.text = header
        }
    }


    /**
     * Content를 담고 있는 ViewHolder
     *
     * @property binding ItemTripsContentBinding
     */
    inner class ContentViewHolder(private val binding: ItemTripsContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: Trip) {
            binding.tvItemTripsContentName.text = trip.name
            binding.tvItemTripsContentPlace.text = trip.region
            binding.tvItemTripsContentDate.text = itemView.context.getString(
                R.string.date_range,
                DateUtil.getDateFromTimestamp(
                    trip.startDate,
                    DateUtil.yyyyMdFormat
                ),
                DateUtil.getDateFromTimestamp(
                    trip.endDate,
                    DateUtil.yyyyMdFormat
                )
            )
            if (trip.members.size > 1) {
                binding.tvItemTripsContentMembers.text = itemView.context.resources.getString(
                    R.string.members_count,
                    trip.members.size - 1
                )
            }

            binding.clItemTripsContentInfo.setOnClickListener {
                // TODO(서윤오): 여행 누를 경우 해당 여행 일정으로 이동
            }
        }
    }

    /**
     * Trip 목록을 업데이트하는 함수
     *
     * @param tripList 새로운 Trip 목록
     */
    fun updateTrips(tripList: List<Trip>) {
        val sortedTrips =
            tripList.sortedWith(compareByDescending<Trip> { trip -> trip.status.ordinal }.thenByDescending { trip -> trip.startDate })
        val groupedTrips = sortedTrips.groupBy { trip -> trip.status }

        this.tripList = mutableListOf<Any>().apply {
            groupedTrips[Trip.TripStatus.ONGOING]?.let { ongoingTrips ->
                if (ongoingTrips.isNotEmpty()) {
                    add("진행 중인 여행")
                    addAll(ongoingTrips)
                }
            }
            groupedTrips[Trip.TripStatus.PLANNING]?.let { planningTrips ->
                if (planningTrips.isNotEmpty()) {
                    add("계획 중인 여행")
                    addAll(planningTrips)
                }
            }
            groupedTrips[Trip.TripStatus.FINISHED]?.let { finishedTrips ->
                if (finishedTrips.isNotEmpty()) {
                    add("지난 여행")
                    addAll(finishedTrips)
                }
            }
        }

        notifyDataSetChanged()
    }
}