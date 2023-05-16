package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.ItemTripContentBinding
import com.paeparo.paeparo_mobile.databinding.ItemTripHeaderBinding
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
                HeaderViewHolder(ItemTripHeaderBinding.inflate(inflater, parent, false))

            ITEM_TYPE_CONTENT ->
                ContentViewHolder(ItemTripContentBinding.inflate(inflater, parent, false))

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
     * @property binding ItemTripHeaderBinding
     */
    inner class HeaderViewHolder(private val binding: ItemTripHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: String) {
            binding.tvItemTripHeaderTitle.text = header
        }
    }


    /**
     * Content를 담고 있는 ViewHolder
     *
     * @property binding ItemTripContentBinding
     */
    inner class ContentViewHolder(private val binding: ItemTripContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: Trip) {
            binding.tvItemTripContentName.text = trip.name
            binding.tvItemTripContentPlace.text = trip.region
            binding.tvItemTripContentDate.text = itemView.context.getString(
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
                binding.tvItemTripContentMembers.text = itemView.context.resources.getString(
                    R.string.members_count,
                    trip.members.size - 1
                )
            }

            binding.clItemTripContentInfo.setOnClickListener {
                // TODO(서윤오): 여행 누를 경우 해당 여행 일정으로 이동
            }
        }
    }

    /**
     * Trip 목록을 업데이트하는 함수
     *
     * @param tripList 새로운 Trip 목록
     */
    fun updateTripList(tripList: List<Trip>) {
        val sortedTrip =
            tripList.sortedWith(compareByDescending<Trip> { trip -> trip.status.ordinal }.thenByDescending { trip -> trip.startDate })
        val groupedTrip = sortedTrip.groupBy { trip -> trip.status }

        this.tripList = mutableListOf<Any>().apply {
            groupedTrip[Trip.TripStatus.ONGOING]?.let { ongoingTrip ->
                if (ongoingTrip.isNotEmpty()) {
                    add("진행 중인 여행")
                    addAll(ongoingTrip)
                }
            }
            groupedTrip[Trip.TripStatus.PLANNING]?.let { planningTrip ->
                if (planningTrip.isNotEmpty()) {
                    add("계획 중인 여행")
                    addAll(planningTrip)
                }
            }
            groupedTrip[Trip.TripStatus.FINISHED]?.let { finishedTrip ->
                if (finishedTrip.isNotEmpty()) {
                    add("지난 여행")
                    addAll(finishedTrip)
                }
            }
        }

        notifyDataSetChanged()
    }
}