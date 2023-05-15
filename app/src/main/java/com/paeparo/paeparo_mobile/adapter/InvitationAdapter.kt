package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.databinding.ItemInvitationBinding
import com.paeparo.paeparo_mobile.model.Trip

/**
 * InvitationAdapter class
 *
 * TripFragment의 초대 목록을 관리하는 Adapter 클래스
 *
 * @property accept 초대 수락 시 실행되는 함수
 * @property decline 초대 거절 시 실행되는 함수
 * @constructor Create empty Invitation adapter
 */
class InvitationAdapter(private val accept: (Trip) -> Unit, private val decline: (Trip) -> Unit) :
    RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder>() {
    /**
     * 초대 목록
     */
    private var invitationList: MutableList<Trip> = mutableListOf()

    class InvitationViewHolder(private val binding: ItemInvitationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(invitation: Trip, accept: (Trip) -> Unit, decline: (Trip) -> Unit) {
            binding.tvItemInvitationTitle.text = invitation.name
            // TODO(손영진): 초대 거절 처리 구현
            // decline(invitation)

            // TODO(손영진): 초대 수락 처리 구현
            // accept(invitation)
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
        holder.bind(invitationList[position], accept, decline)
    }

    override fun getItemCount(): Int = invitationList.size

    /**
     * 초대 목록을 업데이트하는 함수
     *
     * @param invitationList 새로운 초대 목록
     */
    fun updateInvitations(invitationList: List<Trip>) {
        this.invitationList.clear()
        this.invitationList.addAll(invitationList)
        notifyDataSetChanged()
    }
}
