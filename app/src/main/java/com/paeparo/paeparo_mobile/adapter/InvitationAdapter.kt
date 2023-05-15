package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.ItemInvitationBinding
import com.paeparo.paeparo_mobile.model.Trip
import com.paeparo.paeparo_mobile.util.DateUtil

/**
 * InvitationAdapter class
 *
 * TripFragment의 초대 목록을 관리하는 Adapter 클래스
 *
 * @property accept 초대 수락 시 실행되는 함수
 * @property decline 초대 거절 시 실행되는 함수
 */
class InvitationAdapter(private val accept: (Trip) -> Unit, private val decline: (Trip) -> Unit) :
    ListAdapter<Trip, InvitationAdapter.InvitationViewHolder>(InvitationDiffCallback()) {

    /**
     * InvitationViewHolder class
     *
     * @property binding
     * @constructor Create empty Invitation view holder
     */
    inner class InvitationViewHolder(private val binding: ItemInvitationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(invitation: Trip, accept: (Trip) -> Unit, decline: (Trip) -> Unit) {
            binding.tvItemInvitationTitle.text = invitation.name
            binding.tvItemInvitationDate.text = itemView.context.getString(
                R.string.date_range,
                DateUtil.getDateFromTimestamp(
                    invitation.startDate,
                    DateUtil.yyyyMdFormat
                ),
                DateUtil.getDateFromTimestamp(
                    invitation.endDate,
                    DateUtil.yyyyMdFormat
                )
            )
            binding.tvItemInvitationPlace.text = invitation.region
            binding.tvItemInvitationMember.text = itemView.context.getString(
                R.string.invitation_members_count,
                invitation.members.size
            )
        }
    }

    /**
     * InvitationDiffCallback class
     *
     * Invitation 추가 및 삭제 시 사용되는 DiffUtil 클래스
     */
    class InvitationDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem.tripId == newItem.tripId
        }

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationViewHolder {
        return InvitationViewHolder(
            ItemInvitationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: InvitationViewHolder, position: Int) {
        holder.bind(getItem(position), accept, decline)
    }

    /**
     * 초대 목록을 업데이트하는 함수
     *
     * @param invitationList 새로운 초대 목록
     */
    fun updateInvitationList(invitationList: List<Trip>) {
        submitList(invitationList)
    }
}
