package com.paeparo.paeparo_mobile.callback

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.adapter.PlanInfoAdapter
import com.paeparo.paeparo_mobile.model.Event

class PlanItemTouchHelperCallback(private val recyclerView: RecyclerView) :
    ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT
    ) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        (recyclerView.adapter as PlanInfoAdapter).moveItem(
            viewHolder.adapterPosition, // Move를하다보면 위아래로 옮기는데 이를 추적하면서 확인
            target.adapterPosition
        )
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        (recyclerView.adapter as PlanInfoAdapter).removeItem(viewHolder.layoutPosition) // layoutPostion 선택이된 포지션을 바로 반환
    }

    // 드래그시 투명
    // holding을 하고 있는 중에 viewholder 변경
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.ACTION_STATE_SWIPE -> {
                (viewHolder as PlanInfoAdapter.PlanInfoViewHolder).setAlpha(0.5f)
            }
        }
    }

    // holding을 했었던 viewholder을 놓았을 떄,
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        (viewHolder as PlanInfoAdapter.PlanInfoViewHolder).setAlpha(1.0f)
    }
}
class PlanInfoDiffCallback : DiffUtil.ItemCallback<Event>() {

    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.hashCode() == newItem.hashCode() //data 클래스의 경우 hashcode == 이 자동적용됨.
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }

}

